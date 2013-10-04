package ab.demo;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ab.demo.other.ActionRobot;
import ab.demo.other.NaiveMind;
import ab.planner.NaiveTrajectoryPlanner;
import ab.planner.Strategy;
import ab.utils.ImageSegFrame;
import ab.vision.Vision;
import ab.vision.VisionUtils;

public class StarterControlPanel {

	private JFrame frmControlPanel;
	private Vision vision = null;
	private NaiveTrajectoryPlanner tp = null;
	private Strategy naiveMind;
	private ImageSegFrame segFrame = null;

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
		frmControlPanel.setVisible(true);
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
					SwingUtilities.invokeLater(new Runnable() {
					    public void run() {    	
					    	 btnFindTarget.doClick();
					    }
					  });
					SwingUtilities.invokeLater(new Runnable() {
					    public void run() {    	
					    	btnShoot.doClick();
					    }
					  });
				 
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
					segFrame = new ImageSegFrame("Vision Process: Scenario Recognition", screenshot, meta);			
				} else {
					segFrame.refresh(screenshot, meta);
				}
			
				
			}
		});
		frmControlPanel.getContentPane().add(btnScenarioRecognition);
		
		btnFindTarget = new JButton("Set Target");
		btnFindTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(vision == null)
					btnScenarioRecognition.doClick();		
				if(naiveMind == null)
					naiveMind = new NaiveMind();
				//Get the target point
				target = naiveMind.getTarget(vision);
				segFrame.getFrame().setTitle("Set Target");
				//draw the point on the image segmentation frame
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {    	
				    	
				    	segFrame.highlightTarget(target);
				    }
				  });
				
			}
		});
		frmControlPanel.getContentPane().add(btnFindTarget);
		
		btnShoot = new JButton("Shoot");
		btnShoot.addActionListener(new ActionListener() {
			//Find a release point according the target
			public void actionPerformed(ActionEvent e) {
			
				//initialize the trajectory planner
				if (tp == null)
					tp = new NaiveTrajectoryPlanner();
				if(target != null)
				{
					
				    tp.getShot(target);
			
					BufferedImage plot = tp.plotTrajectory();
					int[][] meta = VisionUtils.computeMetaInformation(plot);
			    	segFrame.refresh(plot, meta);
			    	segFrame.getFrame().setTitle("Plotting the Trajectory");
			    	
					SwingUtilities.invokeLater(new Runnable() {
						    public void run() {    	
						    	actionRobot.cshoot(tp.shot);	
						    }
						  });
					
				}
				else
					System.out.println(" You need a target before shooting");
				
					
			}
		});
		frmControlPanel.getContentPane().add(btnShoot);
	}
	

}
