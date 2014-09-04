package proc;


import com.nativelibs4java.opencl.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import org.bridj.*;

import benchmark.Stopwatch;

import util.Log;

import cl.niclabs.skandium.muscles.Execute;

public class BWComputeCL implements
		Execute<ImagePartitionDescriptor, ImagePartitionDescriptor> {

	Log logger = new Log();

	// @Override
	public ImagePartitionDescriptor execute(ImagePartitionDescriptor param) {
		logger.setOn();
		// create new IMG
		final BufferedImage bufferedImage = param.getImg();
		if (bufferedImage == null)
			return null;

		Stopwatch timer = new Stopwatch();
		try {
			CLEnv env = null;
			// if i must use a single kernel for the whole computation
			if (param.getCLEnv().isSingleKernel()) {
				env = param.getCLEnv();
			} else {
				// otherwise i will create another CLEnvoirment
				env = new CLEnv(param.getCLEnv().getCLDevice(), param.getCLEnv().isSingleKernel());
			}

			// getting dimension
			int width = param.getWdim(), height = param.getHdim();

			// calculate if image is to big for cpu
			CLDevice[] devs = CLEnv.getCLContext().getDevices();
			long minWidth = width, minHeight = height;
			boolean splitw = false, splith = false;
			for (CLDevice dev : devs) {
				if (dev.getImage2DMaxWidth() < minWidth) {
					minWidth = dev.getImage2DMaxWidth();
					splitw = true;
				}
				if (dev.getImage2DMaxHeight() < minHeight) {
					minHeight = dev.getImage2DMaxHeight();
					splith = true;
				}
			}
			long wpart = 1, hpart = 1;
			// calculating the number of sub_image to pass to the GPU
			// (wpart*hpart subimages)
			if (splitw) {
				wpart = width / minWidth + ((width % minWidth) == 0 ? 0 : 1);
			}
			if (splith) {
				hpart = height / minHeight
						+ ((height % minHeight) == 0 ? 0 : 1);
			}

			// paint all the images, preserving the alpha channels
			WritableRaster bwrast = param.getBwimg().getRaster();
			// benchmarking
			timer.start();
			// way 1
			// Graphics g = param.getBwimg().getGraphics();
			// BufferedImage res = null;
			for (int x = 0; x < wpart; x++) {
				for (int y = 0; y < hpart; y++) {
					int x_start = (int) (param.getXpos() + (x * minWidth));
					int y_start = (int) (param.getYpos() + (y * minHeight));
					int x_len = (int) ((x + 1 == wpart && x != 0) ? width
							% minWidth : minWidth);
					int y_len = (int) ((y + 1 == hpart && y != 0) ? height
							% minHeight : minHeight);
					// way 1
					// split & elab subimage
					/*
					 * try{ res = elabImg(param.getImg(), env, x_start , y_start
					 * ,x_len ,y_len); g.drawImage(res, x_start, y_start, null);
					 * res = null; } catch(java.lang.OutOfMemoryError e){
					 * e.getStackTrace(); util.Utils.gcCall();
					 * System.out.println("GC call"); res =
					 * elabImg(param.getImg(), env, x_start , y_start ,x_len
					 * ,y_len); g.drawImage(res, x_start, y_start, null); res =
					 * null; }
					 */
					// way 2
					boolean done = false;
					while (!done)
						try {
							ARGBtoFloatArray(param.getImg(), env, x_start,
									y_start, x_len, y_len, bwrast);
							done = true;
						} catch (java.lang.OutOfMemoryError e) {
							util.Utils.gcCall();
						}
				}
			}
			timer.stop();
			param.setElaborationTime(timer.getElapsedTime());

			// free kernel resources
			if (!param.getCLEnv().isSingleKernel()) {
				for (CLKernel kernel : env.getCLKernels())
					kernel.release();
				env.getCLProgram().release();
				env.getCLQueue().release();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return param;
	}

	private void ARGBtoFloatArray(BufferedImage bufferedImage, CLEnv env,
			int x_start_pos, int y_start_pos, int width, int height,
			WritableRaster bwRaster) {
		CLKernel[] kernels = env.getCLKernels();
		CLContext context = CLEnv.getCLContext();
		CLQueue queue = env.getCLQueue();
		CLBuffer<Float> cloutput = context.createFloatBuffer(
				CLMem.Usage.Output, (long) (width * height));
		BufferedImage image = bufferedImage.getSubimage(x_start_pos,
				y_start_pos, width, height);

		// ChannelDataType channelDataType =
		// CLImageFormat.ChannelDataType.UNormInt8;
		// CLImageFormat imageFormat = new CLImageFormat(ChannelOrder.BGRA,
		// channelDataType);

		CLImage2D climage = context.createImage2D(CLMem.Usage.Input, image,
				true);
		// CLImage2D climage = context.createImage2D(CLMem.Usage.Input,
		// imageFormat, width, height);
		// climage.write(queue, image);
		synchronized (kernels[0]) {
			try {
				kernels[0].setArgs(climage, cloutput, width);
				kernels[0].enqueueNDRange(queue, new int[] { width, height })
						.waitFor();
			} catch (CLException ex) {
				throw new RuntimeException(
						"Error occurred while running kernel '"
								+ kernels[0].getFunctionName() + "': " + ex, ex);
			}
		}
		Pointer<Float> output = cloutput.read(queue);
		climage.release();
		cloutput.release();
		int[] px = new int[1];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				px[0] = ( output.get(y * width + x)).intValue();
				bwRaster.setPixel(x_start_pos+x, y_start_pos+y, px);
			}
		}

	}

	/*
	 * private BufferedImage elabImg(BufferedImage bufferedImage, CLEnv env,
	 * 
	 * int x_start_pos, int y_start_pos, int w, int h) { CLKernel[] kernels =
	 * env.getCLKernels(); CLContext context = CLEnv.getCLContext(); CLQueue
	 * queue = env.getCLQueue(); System.out.println("x: " + x_start_pos + " y: "
	 * + y_start_pos + " w: " + w + " h: " + h + " bim: (" +
	 * bufferedImage.getWidth() + "," + bufferedImage.getHeight() + ")");
	 * BufferedImage sub = bufferedImage.getSubimage(x_start_pos, y_start_pos,
	 * w, h); CLImage2D imageIn = context.createImage2D(CLMem.Usage.InputOutput,
	 * sub, false); CLImage2D imageOut =
	 * context.createImage2D(CLMem.Usage.InputOutput, imageIn.getFormat(), w,
	 * h);
	 * 
	 * CLEvent lastEvent = null; CLImage2D finalImageOut = null;
	 * 
	 * for (CLKernel kernel : kernels) { synchronized (kernel) { try {
	 * kernel.setArgs(imageIn, imageOut); finalImageOut = imageOut; imageOut =
	 * imageIn; imageIn = finalImageOut; // enqueue the work with resolution
	 * global work size, local // work size is chosen automatically lastEvent =
	 * kernel.enqueueNDRange(queue, new int[] { w, h }, lastEvent); } catch
	 * (CLException ex) { throw new RuntimeException(
	 * "Error occurred while running kernel '" + kernel.getFunctionName() +
	 * "': " + ex, ex); } } } // wait for calculation end // TODO: find a way to
	 * wait the end of the last calculation in the loop lastEvent.waitFor(); //
	 * read result BufferedImage result = finalImageOut.read(queue); // not so
	 * needed, gb release automatically resources imageIn.release();
	 * imageOut.release(); sub = null; return result; }
	 */

}
