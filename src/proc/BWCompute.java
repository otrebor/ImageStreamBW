package proc;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


//import java.awt.image.BufferedImage;
//import java.awt.image.ColorConvertOp;

import util.Log;

import benchmark.Stopwatch;
import cl.niclabs.skandium.muscles.Execute;


public class BWCompute implements Execute<ImagePartitionDescriptor,ImagePartitionDescriptor>{
	
	  Log logger = new Log();
	  
	    //@Override
	    public ImagePartitionDescriptor execute(ImagePartitionDescriptor param) {
	    	Stopwatch timer = new Stopwatch();
			logger.setOn();
			// elaboration 
			timer.start();
			//way1 best performance
			BufferedImage img = param.getImg().getSubimage(param.getXpos(), param.getYpos(), param.getWdim(), param.getHdim());
			Graphics2D gg = param.getBwimg().createGraphics();
			gg.drawImage(img, param.getXpos(), param.getYpos() , param.getWdim(),
					param.getHdim(), null); 
			//way2 best memory consumption
/*			WritableRaster inRaster = param.getImg().getRaster();
			WritableRaster outRaster = param.getBwimg().getRaster();
			int pxArray[] = new int[4];
			for(int x = param.getXpos() ; x < param.getXpos() + param.getWdim() ; x++){
				for(int y = param.getYpos() ; y < param.getYpos() + param.getHdim() ; y++){
					inRaster.getPixel(x, y, pxArray);
					int luma[] =  new int[1];
					luma[0] = (int)(pxArray[1]*0.3 + pxArray[2]*0.59+ pxArray[3]*0.11);
					outRaster.setPixel(x, y,luma);
				}
			}
	        */
			timer.stop();
			param.setElaborationTime(timer.getElapsedTime());
	        return param;
	    }
}
