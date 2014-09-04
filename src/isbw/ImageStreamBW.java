package isbw;

import benchmark.Stopwatch;
import benchmark.TimeReport;
import cl.niclabs.skandium.muscles.Execute;
import cl.niclabs.skandium.muscles.Merge;
import cl.niclabs.skandium.muscles.Split;
import cl.niclabs.skandium.skeletons.Farm;
import cl.niclabs.skandium.skeletons.Map;
import cl.niclabs.skandium.skeletons.Skeleton;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.OptionSet;

import proc.*;

import com.nativelibs4java.opencl.CLDevice;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLPlatform;
import util.Log;

public class ImageStreamBW {
	static private String pathSource = "../images/";
	static private String pathDest = "../bwimages/";
	static private int maxIncomingFile = 10;
	static private int workers = 2;
	static private boolean notGPU = true;
	static private Log logger = new Log();
	private static boolean autogpu = false;
	private static boolean singlekernel = false;
	private static boolean verbose = false;
	private static LinkedBlockingQueue<Future<ImageDescriptor>> queue = new LinkedBlockingQueue<Future<ImageDescriptor>>();
	private static ArrayList<TimeReport> arr = new ArrayList<TimeReport>();
	private static Thread t;
	private static int nloader = 5;
	private static Counter count = new Counter(maxIncomingFile);


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		argParse(args);
		ExecutorService pool = Executors.newFixedThreadPool(nloader);
		
		// measuring performance
		int THREADS = Runtime.getRuntime().availableProcessors();
		Stopwatch timer = new Stopwatch();
		Stopwatch interallivalTimer = new Stopwatch();
		logger.setOn();

		CLEnv env = null;

		// 1. Define the skeleton program structure
		// Map style for the worker 
		Split<ImageDescriptor, ImagePartitionDescriptor> split = new SplitImage(
				workers );
		Execute<ImagePartitionDescriptor, ImagePartitionDescriptor> exec = null;
		List<CLDevice> devices = util.GPUtils.getDevices();
		if (devices.size() == 0 || notGPU) {
			exec = new BWCompute();
		} else {
			CLDevice prefdev = askForDevice(devices);

			if (prefdev == null) {
				exec = new BWCompute();
			} else {
				// try to compile the program only one time
				try {
					// setting up a single kernel for all the workers and
					// measuring time
					timer.start();
					env = new CLEnv(prefdev, singlekernel);
					timer.stop();
					logger.println("compiling kernel for GPU takes: "
							+ timer.getElapsedTime() + " msec");
					timer.reset();

					exec = new BWComputeCL();
				} catch (Exception e) {
					System.out.println(e + "/n working with CPU");
					exec = new BWCompute();
				}
			}
		}
		Merge<ImagePartitionDescriptor, ImageDescriptor> merge = new MergeResults();
		logger.println(exec.getClass().getName());
		timer.start();

		Skeleton<ImageDescriptor, ImageDescriptor> map = new Map<ImageDescriptor, ImageDescriptor>(
				split, exec, merge);

		Skeleton<ImageDescriptor, ImageDescriptor> farm = new Farm<ImageDescriptor, ImageDescriptor>(
				map);

		// 2. Input parameters

		// read files from directory (is better to generate a Stream?)
		File myDir = new File(pathSource);
		if (myDir.exists() && myDir.isDirectory()) {
			File[] files = myDir.listFiles();
			int min = (files.length < maxIncomingFile) ? files.length
					: maxIncomingFile;


			collectResult();
			interallivalTimer.start();
			count.setMax(min);
			double avgTa = 0;
			for (int i = 0; i < files.length && count.getValue() < min; i++) {
				if (files[i].getName().endsWith(".jpg"))
					try {
						// future on results
						pool.submit(new ImgLoader(files[i],queue,farm, count,env));
/*						queue.add(farm
								.input(new ImageDescriptor(files[i], env)));*/
						interallivalTimer.stop();
						avgTa += interallivalTimer.getElapsedTime();
						interallivalTimer.reset().start();
						// logger.println(files[i].toString());
					} catch (java.awt.color.CMMException e) {

					}
					
			}
			// 3. Do something else...
			pool.shutdown();
			try {
				pool.awaitTermination(count.getValue(), TimeUnit.SECONDS);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// 4. Block for the results
			try {
				t.join();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			timer.stop();
			// free resources
			if (singlekernel) {
				for (CLKernel kernel : env.getCLKernels())
					kernel.release();
				env.getCLProgram().release();
				env.getCLQueue().release();
			}

			/*
			 * TimeReport.writeCSV("benchmark/" + exec.getClass().getName()
			 * + "_"+((singlekernel&&!notGPU)?"SK":"MK") + "_PD-" + workers
			 * + "_ON-" + THREADS + "_IS-" + inputFut.size() + "_ET" +
			 * timer.getElapsedTime() + ".csv", arr);
			 */

			printStats(arr , avgTa/count.getValue() , timer.getElapsedTime() , count.getValue() , workers , nloader , THREADS);
			System.exit(0);

		}
		System.err.println("Wrong Path for computation");
		System.exit(1);
	}

	public static void collectResult() {
		//is necessary to use a thread to write on disk results to reduce the probability of heapMaxSizeReached
		if (t == null) {
			t = new Thread("writer") {

				public void run() {
					boolean term = false;
					ImageDescriptor res;
					if(verbose)
						logger.println(TimeReport.fieldNames);

						
						while(!term || !queue.isEmpty()) {
							
							try {
								res = queue.poll(1, TimeUnit.SECONDS).get();
								if(res==null)
									continue;
								TimeReport bk = res.getBenchmark();
								arr.add(bk);
								writeOnDisk(res.getImg(), pathDest, res.getName());
								if(res.isLastOfStream())
									term=true;
								res.getBenchmark().stopLatencyTimer();
								res.getBenchmark().setRatioLatencyPX(res.getWidth(),
										res.getHeight());
								if(verbose)
									bk.printFormatted();
							} catch (InterruptedException e) {
								term = true;
							} catch (ExecutionException e) {
								e.printStackTrace();
							} catch (NullPointerException e){
							}
						}


				}
			};
			t.start();
		}
	}

	public static void writeOnDisk(BufferedImage img, String path,
			String filename) {

		File f = new File(path + filename);
		try {
			// png is an image format (like gif or jpg)
			ImageIO.write(img, "jpg", f);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static CLDevice askForDevice(List<CLDevice> devices) {
		CLDevice suggested = CLPlatform.getBestDevice(
				Arrays.asList(CLPlatform.DeviceFeature.MaxComputeUnits),
				devices);
		if (autogpu) {
			System.out.println("USING DEVICE: "+suggested.getName());
			return suggested;
		}
		int i;
		for (i = 0; i < devices.size(); i++) {
			System.out
					.println(i
							+ "] JAVACL ->"
							+ devices.get(i).getName()
							+ " "
							+ ((devices.get(i).equals(suggested)) ? "(suggested)"
									: ""));
		}
		System.out.println(i + "] CPU ");
		System.out.println("Please digit the number of the preferred device:");
		Scanner in = new Scanner(System.in);
		int dev = in.nextInt();
		in.close();
		if (dev >= 0 && dev < devices.size())
			return devices.get(dev);
		return null;
	}

	public static void argParse(String[] args) {
		File destDir = new File(pathDest);
		File sourceDir = new File(pathSource);

		OptionParser parser = new OptionParser();
		OptionSpec<Integer> maxincomingfile = parser.accepts("maxincomingfile")
				.withRequiredArg().ofType(Integer.class)
				.describedAs("max number of trasformed images")
				.defaultsTo(maxIncomingFile);
		OptionSpec<Integer> pardeg = parser.accepts("pardeg").withRequiredArg()
				.ofType(Integer.class)
				.describedAs("parallel degree of this elaboration")
				.defaultsTo(workers);
		OptionSpec<File> outdir = parser.accepts("outdir").withRequiredArg()
				.ofType(File.class)
				.describedAs("Directory for trasformed Images")
				.defaultsTo(destDir);
		OptionSpec<File> indir = parser.accepts("indir").withRequiredArg()
				.ofType(File.class).describedAs("Directory of coloured images")
				.defaultsTo(sourceDir);
		OptionSpec<String> gpu = parser
				.accepts("gpu")
				.withOptionalArg()
				.ofType(String.class)
				.describedAs(
						"delegate elaboration to OpenCL devices : use auto argument to enter non interactive mode");
		OptionSpec<String> kernel = parser
				.accepts("kernel")
				.withOptionalArg()
				.ofType(String.class)
				.describedAs(
						"OpenCL device work with 'single' or 'multiple' kernels")
				.defaultsTo("multiple");
		OptionSpec<Void> gc = parser.accepts("gc",
				"Enable Explicit invocation of gc");
		OptionSpec<Void> verbose = parser.accepts("v",
		"verbose output");
		
		parser.acceptsAll(asList("h", "?"), "show help");
		try {
			OptionSet options = parser.parse(args);
			if (options.has("?") || options.has("h")) {
				printHelp(parser);
			}
			if (options.has(maxincomingfile)) {
				maxIncomingFile = (Integer) options.valueOf(maxincomingfile);
			}
			if (options.has(pardeg)) {
				workers = (Integer) options.valueOf(pardeg);
			}
			if (options.has(outdir)) {
				pathDest = ((File) options.valueOf(outdir)).getPath() + "/";
			}
			if (options.has(indir)) {
				pathSource = ((File) options.valueOf(indir)).getPath() + "/";
			}
			if (options.has(gpu)) {
				notGPU = false;
				if (options.hasArgument(gpu)
						&& options.valueOf(gpu).equals("auto")) {
					autogpu = true;
				}
				if (options.has(kernel) && options.hasArgument(kernel)
						&& options.valueOf(kernel).equals("single")) {
					singlekernel = true;
				}
			}
			if (options.has(gc)) {
				util.Utils.enableGC();
			}
			if (options.has(verbose)) {
				ImageStreamBW.verbose = true;
			}
			System.out.println("indir=" + pathSource + " outdir=" + pathDest
					+ " pardeg=" + workers + " maxincomingfile="
					+ maxIncomingFile + " GPU=" + !notGPU + " AUTOGPU="
					+ autogpu + " SINGLEKERNEL=" + singlekernel + " GC="
					+ util.Utils.gcIsEnabled());
		} catch (joptsimple.OptionException e) {
			System.out.println(e.toString() + "\n");
			printHelp(parser);

		}

	}

	private static void printHelp(OptionParser parser) {
		try {
			parser.printHelpOn(System.out);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			System.exit(1);
		}
	}

	private static void printStats(Collection<TimeReport> arr, double interarrival, double elapsed , int itemsnumber , int worker , int loader , int threads) {

		Iterator<TimeReport> it = arr.iterator();
		double avgsplitting = 0 , avgmerging=0, avgpxtime = 0, avgelab = 0, avgloading = 0, avglatency = 0, avgpxratio = 0;
		int size = arr.size();
		while (it.hasNext()) {
			TimeReport tr = it.next();
			avgpxtime += tr.getPxTime();
			avgelab += tr.getElaborationAVGTime();
			avgloading += tr.getLoadingTime();
			avglatency += tr.getLatency();
			avgpxratio += tr.getLatencyPxRatio();
			avgsplitting += tr.getSplittingTime();
			avgmerging += tr.getMergingTime();
		}
		avgpxtime = avgpxtime / size;
		avgelab = avgelab / size;
		avgloading = avgloading / size;
		avglatency = avglatency / size ;
		avgpxratio = avgpxratio / size ;
		avgsplitting = avgsplitting /size;
		avgmerging = avgmerging /size;
		logger.println("STATS (AVG) :");
		logger.println("LOADING; INTERARRIVAL; SPLITTING; WORKER ELAB TIME; PXTIME; MERGING; LATENCY; LATENCY/PX ; COMPLETION TIME; ITEM NUM; WORKERS; LOADERS; TOTAL PAR DEG; AVAILABLE PROCESSORS");
		System.out.printf(Locale.ITALY, "%f; %f; %f; %f; %f; %f; %f; %f; %f ;%d ;%d ;%d ;%d ;%d \n", avgloading , interarrival, avgsplitting, avgelab
				, avgpxtime ,avgmerging, + avglatency
				, avgpxratio , elapsed , itemsnumber, worker , loader , worker+loader+2, threads);
		//Parallel degree = num loader + worker farm + main + writer

/*		logger.println( avgloading + "; "+ interarrival +"; " + avgsplitting+"; " + avgelab
				+ "; " + avgpxtime + "; " +avgmerging+ "; " + avglatency
				+ "; " + avgpxratio);*/
	}
}
