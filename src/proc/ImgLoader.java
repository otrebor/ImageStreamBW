package proc;

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import cl.niclabs.skandium.skeletons.Skeleton;

public class ImgLoader implements Runnable {

	private File fl ;
	private LinkedBlockingQueue<Future<ImageDescriptor>> queue;
	private Skeleton<ImageDescriptor, ImageDescriptor> farm;
	private Counter count;
	private CLEnv env;
	
	public ImgLoader(File fl, LinkedBlockingQueue<Future<ImageDescriptor>> queue,Skeleton<ImageDescriptor, ImageDescriptor> farm, Counter count, CLEnv env){
		this.fl=fl;
		this.queue = queue;
		this.farm = farm;
		this.count=count;
		this.env = env;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			ImageDescriptor img = new ImageDescriptor(fl, env);
			synchronized(queue){
				if(count.getValue()==count.getMax()-1)
					img.setLastOfStream(true);
				if(count.getValue()<count.getMax()){
					queue.add(farm.input(img));
					count.increment();				
				}
			}
		}catch(java.awt.color.CMMException e){
			
		}
	}

}
