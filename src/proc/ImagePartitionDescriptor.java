package proc;
import java.awt.image.BufferedImage;


import benchmark.TimeReport;



public class ImagePartitionDescriptor {
	private BufferedImage img;
	private BufferedImage bwimg;
	private String name;
	private int xpos ;
	private int ypos ;
	private int wdim ;
	private int hdim ;
	private CLEnv env;
	private TimeReport benchmark;
	private double elaborationTime;
	private boolean lastOfStream = false;
	
	public void setImg(BufferedImage img) {
		this.img = img;
	}
	public BufferedImage getImg() {
		return img;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getXpos() {
		return xpos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public int getYpos() {
		return ypos;
	}
	
	public void setBenchmark(TimeReport benchmark) {
		this.benchmark = benchmark;
	}
	public TimeReport getBenchmark() {
		return benchmark;
	}

	public void setElaborationTime(double elaborationTime) {
		this.elaborationTime = elaborationTime;
	}
	public double getElaborationTime() {
		return elaborationTime;
	}
	public void setCLEnv(CLEnv env) {
		this.env = env;
	}
	public CLEnv getCLEnv() {
		return env;
	}
	public void setWdim(int wdim) {
		this.wdim = wdim;
	}
	public int getWdim() {
		return wdim;
	}
	public void setHdim(int hdim) {
		this.hdim = hdim;
	}
	public int getHdim() {
		return hdim;
	}
	public void setBwimg(BufferedImage bwimg) {
		this.bwimg = bwimg;
	}
	public BufferedImage getBwimg() {
		return bwimg;
	}
	public void setLastOfStream(boolean lastOfStream) {
		this.lastOfStream = lastOfStream;
	}
	public boolean isLastOfStream() {
		return lastOfStream;
	}
}
