package ab.vision;

import java.awt.image.BufferedImage;

import ab.utils.ShowImageSegmentation;

public class RefreshSegmentationThread extends Thread{
	ShowImageSegmentation  showImageSegFrame;
 public RefreshSegmentationThread(ShowImageSegmentation showImageSegFrame)
 {
	 super(showImageSegFrame);
	 this.showImageSegFrame = showImageSegFrame;
	
 }

public synchronized void refresh(BufferedImage screenshot, int[][] meta) {
	this.showImageSegFrame.refresh(screenshot, meta);
	
}

 
}
