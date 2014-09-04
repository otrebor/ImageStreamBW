package proc;

import java.awt.image.BufferedImage;

import benchmark.Stopwatch;
import benchmark.TimeReport;
import util.Log;
import cl.niclabs.skandium.muscles.Split;

public class SplitImage implements
		Split<ImageDescriptor, ImagePartitionDescriptor> {
	/*
	 * A Split muscle is responsible of transforming a single data element into
	 * a list of elements. While in this example the single element's type is
	 * the same as the list's elements type, they may be different.
	 */
	int numParts; // The number of sub intervals to create from an Interval
	static private Log logger = new Log();

	public SplitImage(int numParts) {
		this.numParts = numParts;
	}

	//@Override
	public ImagePartitionDescriptor[] split(ImageDescriptor param)
			throws Exception {
		// enabling logger and timer for this section
		Stopwatch timer = new Stopwatch();
		TimeReport benchmark = param.getBenchmark();
		
		timer.start();
		logger.setOn();
		// split image in sub images
		// slice of subimages are rows of that image
		int widht = param.getWidth();
		int height = param.getHeight();

		//calculating the height of sub images. the widht will remain the total widht
		int height_subimg = height / numParts + (height % numParts) / numParts;

		ImagePartitionDescriptor[] result = new ImagePartitionDescriptor[numParts];
		BufferedImage bwImg = new BufferedImage(param.getImg().getWidth(), param.getImg().getHeight(), BufferedImage.TYPE_BYTE_GRAY);

		for (int j = 0; j < numParts; j++) {
			int x_upperleft_pos = 0;
			int y_upperleft_pos = j * height_subimg;
			// if the subimg is on the last row of img, i add the rest of pixels
			// remained out of the splitting
			// little bit of load unbalancing but only for one slice of image
			int pWidht = widht;
			int pHeight = j < (numParts - 1) ? height_subimg : height_subimg
					+ (height % height_subimg);
			result[j] = new ImagePartitionDescriptor();
			// setting the image
			result[j].setImg(param.getImg());
			result[j].setXpos(x_upperleft_pos);
			result[j].setYpos(y_upperleft_pos);
			result[j].setWdim(pWidht);
			result[j].setHdim(pHeight);
			result[j].setName(param.getName());	
			result[j].setBwimg(bwImg);
			result[j].setCLEnv(param.getCLEnv());
			result[j].setBenchmark(benchmark);
			result[j].setLastOfStream(param.isLastOfStream());
		}
		timer.stop();
		benchmark.setSplittingTime(timer.getElapsedTime());
		// help gc (reduce heap size)
		param = null;
		util.Utils.gcCall();
		
		return result;

	}
}
