package ab.objtracking;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import ab.demo.other.ActionRobot;
import ab.utils.ImageSegFrame;
import ab.vision.ShowSeg;

public class DisplayTracking extends ShowSeg implements Runnable{
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DisplayTracking dt = new DisplayTracking();
		dt.run();
	}

	@Override
	public void run() {
		//the display frame;
		 ImageSegFrame frame = null;
		long screenshot_time = 0l;
		long vision_process_time = 0l;
		while (true) {	
			long current_time = System.nanoTime();
			// capture an image
			BufferedImage screenshot = ActionRobot.doScreenShot();
			// analyse and show image
			screenshot_time = System.nanoTime() - current_time;
			int[][] meta = computeMetaInformation(screenshot);
			screenshot = analyseScreenShot(screenshot);
			vision_process_time = System.nanoTime() - screenshot_time - current_time;
			if (frame == null) {
				frame = new ImageSegFrame("Object Tracking", screenshot,
						meta);
				 frame.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			} else {
				frame.refresh(screenshot, meta);
			}
			System.out.println(" screenshot time : " + screenshot_time + " vision process time " + vision_process_time);
		}
	}

}
