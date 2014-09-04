package proc;


//import java.awt.Graphics;
//import java.awt.image.BufferedImage;

import benchmark.Stopwatch;

import util.Log;

import cl.niclabs.skandium.muscles.Merge;

public class MergeResults implements
		Merge<ImagePartitionDescriptor, ImageDescriptor> {
	static private Log logger = new Log();

	//@Override
	public ImageDescriptor merge(ImagePartitionDescriptor[] param)
			throws Exception {
		
		
		// enabling logger for this section
		Stopwatch timer = new Stopwatch();
		logger.setOn();
		timer.start();
		// create the new image
		// calculate image size
/*		int w = 0;
		int h = 0;
		for (int i = 0; i < param.length; i++) {
			if (param[i].getXpos() == 0)
				h += param[i].getImg().getHeight();
			if (param[i].getYpos() == 0)
				w += param[i].getImg().getWidth();
		}
		// recombine img
		BufferedImage combined = new BufferedImage(w, h,
				param[0].getImg().getType());

		// paint all the images, preserving the alpha channels
		Graphics g = combined.getGraphics();

		// calculate offset between row and column
		// int widht_subimg = param[0].getImg().getWidth() ;
		// int height_subimg = param[0].getImg().getHeight() ;
		*/
		double elabTime = 0;
		for (int i = 0; i < param.length; i++) {
			
			//g.drawImage(param[i].getImg(), param[i].getXpos(),
				//	param[i].getYpos(), null);
			//benchmarking
			elabTime += param[i].getElaborationTime();
		}
		ImageDescriptor res = new ImageDescriptor(param[0].getBwimg(), param[0].getName(), param[0].getCLEnv());
		timer.stop();
		//AVG
		elabTime = elabTime / param.length ;
		param[0].getBenchmark().setElaborationAVGTime(elabTime);
		param[0].getBenchmark().setMergingTime(timer.getElapsedTime());
		param[0].getBenchmark().setPxTime(((elabTime*param.length)/(param[0].getImg().getHeight()*param[0].getImg().getWidth())));
		res.setLastOfStream(param[0].isLastOfStream());
		res.setBenchmark(param[0].getBenchmark());
		//help gb
		//usefull to free memory but slows the performance
		//param=null;
		util.Utils.gcCall();
		
		return res;
	}
}
