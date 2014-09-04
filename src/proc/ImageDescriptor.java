package proc;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



import util.Log;

import benchmark.Stopwatch;
import benchmark.TimeReport;


public class ImageDescriptor {

	private BufferedImage img;
	private String name;
	private static Log logger = new Log();
	private CLEnv env ;
	private TimeReport benchmark = null ;
	private boolean lastOfStream = false;
	
	
	public ImageDescriptor(File file, CLEnv env){
		logger.setOn();
		Stopwatch timer = new Stopwatch();
		this.benchmark = new TimeReport();
		this.benchmark.startLatencyTimer();
		timer.start();
        try {
            img = ImageIO.read(file);
       } catch (IOException ex) {
           ex.printStackTrace();
           System.exit(0);
       }
       timer.stop();
       this.benchmark.setLoadingTime(timer.getElapsedTime());
       this.benchmark.setFilename(file.getName());
       this.setCLEnv(env);
       setName(file.getName());
	}
	
	public ImageDescriptor(BufferedImage im, String filename, CLEnv env){
		if(im==null || filename==null)
			throw new IllegalArgumentException("im = null");
		img = im ;
		this.setName(filename);
	    this.setCLEnv(env);
	}

	public BufferedImage getImg() {
		return img;
	}


	public int getWidth() {
		return this.img.getWidth();
	}

	public int getHeight() {
		return this.img.getHeight();
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setBenchmark(TimeReport benchmark) {
		this.benchmark = benchmark;
	}

	public TimeReport getBenchmark() {
		return benchmark;
	}

	public void setCLEnv(CLEnv env) {
		this.env = env;
	}

	public CLEnv getCLEnv() {
		return env;
	}

	public void setLastOfStream(boolean lastOfStream) {
		this.lastOfStream = lastOfStream;
	}

	public boolean isLastOfStream() {
		return lastOfStream;
	}



}
