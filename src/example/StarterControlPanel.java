package example;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ab.demo.other.ActionRobot;
import ab.planner.ExampleTrajectoryPlanner;
import ab.planner.Strategy;
import ab.utils.ImageSegFrame;
import ab.vision.ABUtil;
import ab.vision.VisionUtils;
import example.strategy.Strategy_RandomHitPigs;
import example.strategy.Strategy_RandomHitSupporters;

public class StarterControlPanel {

	private JFrame frmControlPanel;
	
	private ExampleTrajectoryPlanner tp = null;
	private Strategy exampleStrategy;
	private ImageSegFrame segFrame = null;
	private State currentState = null;
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
					new StarterControlPanel();
				
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
		exampleStrategy = new Strategy_RandomHitSupporters();
	}
	public StarterControlPanel(Strategy strategy)
	{
		exampleStrategy = strategy;
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
				ActionRobot.fullyZoomOut();
				currentState = ABUtil.getState();
				currentState.PrintAllObjects();
				int[][] meta = VisionUtils.computeMetaInformation(currentState.image);			
				if (segFrame == null) {
					segFrame = new ImageSegFrame("Vision Process: Scenario Recognition", VisionUtils.analyseScreenShot(currentState.image), meta);			
				} else {
					segFrame.refresh(VisionUtils.analyseScreenShot(currentState.image), meta);
				}
			
				
			}
		});
		frmControlPanel.getContentPane().add(btnScenarioRecognition);
		
		btnFindTarget = new JButton("Set Target");
		btnFindTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(currentState == null)
					btnScenarioRecognition.doClick();		
				if(exampleStrategy == null)
					exampleStrategy = new Strategy_RandomHitSupporters();
				//Get the target point
				target = exampleStrategy.getTarget(currentState);
			
				if(target != null)
				{
					segFrame.getFrame().setTitle("Set Target");
					//draw the point on the image segmentation frame
					SwingUtilities.invokeLater(new Runnable() {
					    public void run() {    	
					    	segFrame.highlightTarget(target);
					    }
					  });
				} else
					JOptionPane.showMessageDialog(null, " The strategy module cannot find the target");
				
			}
		});
		frmControlPanel.getContentPane().add(btnFindTarget);
		
		btnShoot = new JButton("Shoot");
		btnShoot.addActionListener(new ActionListener() {
			//Find a release point according the target
			public void actionPerformed(ActionEvent e) {
			
				//initialize the trajectory planner
				if (tp == null)
					tp = new ExampleTrajectoryPlanner();
				if(exampleStrategy == null)
					exampleStrategy = new Strategy_RandomHitSupporters();
				if(target != null)
				{
					State state = ABUtil.getState();
				
				    tp.getShot(state, target, exampleStrategy.useHighTrajectory(state), exampleStrategy.getTapPoint(state));
			
					BufferedImage plot = tp.plotTrajectory();
					int[][] meta = VisionUtils.computeMetaInformation(plot);
			    	segFrame.refresh(plot, meta);
			    	segFrame.getFrame().setTitle("Plotting the Trajectory");
			    	
					SwingUtilities.invokeLater(new Runnable() {
						    public void run() {    	
						    	actionRobot.fshoot(tp.shot);	
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
