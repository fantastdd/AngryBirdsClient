package ab.demo;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;

import ab.demo.other.ActionRobot;
import ab.demo.other.NaiveMind;
import ab.demo.other.Shot;
import ab.planner.NaiveTrajectoryPlanner;
import ab.utils.ShowImageSegmentation;
import ab.vision.RefreshSegmentationThread;
import ab.vision.ShowImageSegmentationThread;
import ab.vision.Vision;
import ab.vision.VisionUtils;

public class StarterControlPanel {

	private JFrame frmControlPanel;
	private Vision vision = null;
	private NaiveTrajectoryPlanner tp = null;
	private ShowImageSegmentation segFrame = null;
	private ShowImageSegmentationThread segFrameThread;
	private Point target = null;
	private ActionRobot actionRobot = new ActionRobot();
	private JButton btnScenarioRecognition;
	private JButton btnFindTarget;
	private JButton btnShoot;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StarterControlPanel window = new StarterControlPanel();
					window.frmControlPanel.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StarterControlPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmControlPanel = new JFrame();
		frmControlPanel.setTitle("Control Panel");
		frmControlPanel.setBounds(100, 100, 445, 88);
		frmControlPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmControlPanel.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnAutoRun = new JButton("Auto Run");
		btnAutoRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				 btnScenarioRecognition.doClick();
				
				 btnFindTarget.doClick();
				 btnShoot.doClick();
			}
		});
		frmControlPanel.getContentPane().add(btnAutoRun);
		
		 btnScenarioRecognition = new JButton("Vision Process");
		//Vision Process: 1. Zoom out 2. Take screenshot 3. Identify Objects
		btnScenarioRecognition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//Add a loading-image here
				actionRobot.fullyZoom();
				BufferedImage screenshot = ActionRobot.doScreenShot();
				vision = new Vision(screenshot);
				vision.reportObjects();
				int[][] meta = VisionUtils.computeMetaInformation(screenshot);
				screenshot = VisionUtils.analyseScreenShot(screenshot);
				
				if (segFrame == null) {
					
					segFrame = new ShowImageSegmentation("Vision Process: Scenario Recognition", screenshot, meta);
					segFrameThread = new ShowImageSegmentationThread(segFrame);
					segFrameThread.start();
					
				} else {
					segFrameThread.refresh(screenshot, meta);
				}
			
				
			}
		});
		frmControlPanel.getContentPane().add(btnScenarioRecognition);
		
		btnFindTarget = new JButton("Find Target");
		btnFindTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Get the target point
				target = NaiveMind.getTarget(vision);
				//draw the point on the image segmentation frame
				target = new Point(700,20);
				segFrame.highlightTarget(target);
			}
		});
		frmControlPanel.getContentPane().add(btnFindTarget);
		
		btnShoot = new JButton("Shoot");
		btnShoot.addActionListener(new ActionListener() {
			//Find a release point according the target
			public void actionPerformed(ActionEvent e) {
				Shot shot = null;
				//initialize the trajectory planner
				if (tp == null)
					tp = new NaiveTrajectoryPlanner();
				if(target != null)
				{
					
					shot = tp.getShot(target);
					//Plot Trajectory
					BufferedImage plot = tp.plotTrajectory();
					int[][] meta = VisionUtils.computeMetaInformation(plot);
					//plot = VisionUtils.analyseScreenShot(plot);
					//segFrame = new ShowImageSegmentation("Plotting Trajectory",plot, meta);
					// RefreshSegmentationThread rst = new  RefreshSegmentationThread(segFrame);
					// rst.start();
					segFrameThread.refresh(plot, meta);
					actionRobot.cshoot(shot);		
					
				}
				else
					System.out.println(" You need a target before shooting");
				
					
			}
		});
		frmControlPanel.getContentPane().add(btnShoot);
	}

}
