package util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import benchmark.Stopwatch;

public class ImageStreamBWSeq {
	static private String pathSource = "../images/";
	static private String pathDest = "../bwimages/";
	static private int maxIncomingFile = 10;
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// measuring performance
		Stopwatch timer = new Stopwatch();
		Stopwatch filetimer = new Stopwatch();

		timer.start();
		filetimer.start();

		File myDir = new File(pathSource);
		if (myDir.exists() && myDir.isDirectory()) {
			File[] files = myDir.listFiles();
			int min = (files.length < maxIncomingFile) ? files.length
					: maxIncomingFile;
			int count = 0;
			for (int i = 0; i < files.length && count < min; i++) {
				if (files[i].getName().endsWith(".jpg"))
					try {
						// future on results
						count++;
						BufferedImage img = ImageIO.read(files[i]);

						// getting width and height of image
						double image_width = img.getWidth();
						double image_height = img.getHeight();

						BufferedImage bimg = null;

						// drawing a new image
						bimg = new BufferedImage((int) image_width,
								(int) image_height,
								BufferedImage.TYPE_BYTE_GRAY);
						Graphics2D gg = bimg.createGraphics();
						gg.drawImage(img, 0, 0, img.getWidth(null),
								img.getHeight(null), null);

						writeOnDisk(bimg, pathDest, files[i].getName());
						System.out.println(files[i].toString()+ " on "+ filetimer.getElapsedTime()+" PXTime="+filetimer.getElapsedTime()/(bimg.getHeight()*bimg.getWidth()));
						bimg=null;
						img=null;
						filetimer.reset().start();
					} catch(java.lang.OutOfMemoryError e){
						System.gc();
						i--;
						count--;
					}catch (java.awt.color.CMMException e) {
						count-- ;
					} catch (IOException e) {
						count--;
					}

			}
			timer.stop();
			System.out.println("Time :"+ timer.getElapsedTime() +"AVG LATENCY:"+timer.getElapsedTime()/count);
			

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




}
