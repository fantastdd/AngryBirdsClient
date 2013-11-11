package ab.objtracking;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ab.objtracking.tracker.SMETracker;
import ab.utils.ImageSegFrame;
import ab.utils.ImageTrackFrame;
import ab.vision.VisionUtils;
import ab.vision.real.MyVisionUtils;

public class TrackingFrameComparison implements Runnable {
	
	String filename;
	public static volatile boolean goToNextFrame = false;
	public static volatile boolean goToPrevFrame = false;
	public TrackingFrameComparison(String filename)
	{
		this.filename = filename;
	}
	/**
	 * @param args
	 */
	static public void main(String[] args) {
		TrackingFrameComparison tfc = new TrackingFrameComparison("t");
		tfc.run();
	}
/*	public BufferedImage loadImage(int Pointer)
	{
		return ImageIO.read(new File(filename + "image"))
	}*/
	@Override
	public void run() {
		
		
			BufferedImage prevScreenshot, nextScreenshot = null;
			SMETracker tracker = new SMETracker();
			
			// get list of images to process
			File[] images = null;
			// check if argument is a directory or an image
			int pointer = 0;
			if ((new File(filename)).isDirectory()) 
			{
					images = new File(filename).listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File directory, String fileName) {
							return fileName.endsWith(".png");
					}
					});
			
				// iterate through the images
				Arrays.sort(images);
				
				/*for (File file: images)
				{
					try {
						screenshots.add(ImageIO.read(file));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}*/
				assert(images.length > 1);
				try {
					prevScreenshot = ImageIO.read(images[pointer]);
			
				DisplayTracking_NewVision.flipAskForInitialScenario();
				prevScreenshot = MyVisionUtils.constructImageSegWithTracking(prevScreenshot, tracker);
				prevScreenshot = VisionUtils.resizeImage(prevScreenshot, 800, 1200);
				
				nextScreenshot = ImageIO.read(images[pointer + 1]);
				//long time = System.nanoTime();
				nextScreenshot = MyVisionUtils.deepCopy(MyVisionUtils.constructImageSegWithTracking(nextScreenshot, tracker));
				//System.out.println("Processing time: " + (System.nanoTime() - time)/1000000);
				nextScreenshot = VisionUtils.resizeImage(nextScreenshot, 800, 1200);
				
				ImageTrackFrame prevFrame = new ImageTrackFrame(" Prev Frame " + images[pointer].getName(), prevScreenshot, null);
				prevFrame.setTracker(tracker);
				prevFrame.setInitialFrame(true);
				prevFrame.refresh(prevScreenshot);
				
				ImageTrackFrame nextFrame = new ImageTrackFrame(" Next Frame " + images[pointer + 1].getName(), nextScreenshot, null);
				nextFrame.setTracker(tracker);
				nextFrame.refresh(nextScreenshot);
				
				while(true)
				{
					
					if(goToNextFrame)
					{
						goToNextFrame = !goToNextFrame;
						
						pointer++;
						if(pointer == images.length - 1)
							pointer = 0;
					
					    prevScreenshot = ImageIO.read(images[pointer]);
						DisplayTracking_NewVision.flipAskForInitialScenario();
						prevScreenshot = MyVisionUtils.constructImageSegWithTracking(prevScreenshot, tracker);
						prevScreenshot = VisionUtils.resizeImage(prevScreenshot, 800, 1200);
						prevFrame.setTitle(" Prev Frame " + images[pointer].getName());
						prevFrame.refresh(prevScreenshot);
						
						nextScreenshot = ImageIO.read(images[pointer + 1]);;
						//long time = System.nanoTime();
						nextScreenshot = MyVisionUtils.constructImageSegWithTracking(nextScreenshot, tracker);
						//System.out.println("Processing time: " + (System.nanoTime() - time)/1000000);
						nextScreenshot = VisionUtils.resizeImage(nextScreenshot, 800, 1200);
						nextFrame.setTitle(" Next Frame " + images[pointer + 1].getName());
						nextFrame.refresh(nextScreenshot);
						
						
					} else
						if(goToPrevFrame)
						{
							goToPrevFrame = !goToPrevFrame;
							pointer--;
							if(pointer < 0)
								pointer = images.length - 2;
							prevScreenshot = ImageIO.read(images[pointer]);
							DisplayTracking_NewVision.flipAskForInitialScenario();
							prevScreenshot = MyVisionUtils.constructImageSegWithTracking(prevScreenshot, tracker);
							prevScreenshot = VisionUtils.resizeImage(prevScreenshot, 800, 1200);
							prevFrame.setTitle(" Prev Frame " + images[pointer].getName());
							prevFrame.refresh(prevScreenshot);
							
							nextScreenshot =ImageIO.read(images[pointer + 1]);
							//long time = System.nanoTime();
							nextScreenshot = MyVisionUtils.constructImageSegWithTracking(nextScreenshot, tracker);
							//System.out.println("Processing time: " + (System.nanoTime() - time)/1000000);
							nextScreenshot = VisionUtils.resizeImage(nextScreenshot, 800, 1200);
							nextFrame.setTitle(" Next Frame " + images[pointer + 1].getName());
							nextFrame.refresh(nextScreenshot);
						} 
				}
				
				
				
				
			
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
	}

	}
	}
