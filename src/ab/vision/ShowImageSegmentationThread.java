package ab.vision;

import java.awt.image.BufferedImage;

import ab.utils.ShowImageSegmentation;

public class ShowImageSegmentationThread extends Thread{
	ShowImageSegmentation  showImageSegFrame;
 public ShowImageSegmentationThread(ShowImageSegmentation showImageSegFrame)
 {
	 super(showImageSegFrame);
	 this.showImageSegFrame = showImageSegFrame;
	
 }

public synchronized void refresh(BufferedImage screenshot, int[][] meta) {
	this.showImageSegFrame.refresh(screenshot, meta);
	
}

 
}
