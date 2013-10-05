package ab.planner;

import java.awt.Point;

import example.State;

public interface Strategy {

public Point getTarget(State state);
public boolean useHighTrajectory(State state);
public float getTapPoint(State state);

}
