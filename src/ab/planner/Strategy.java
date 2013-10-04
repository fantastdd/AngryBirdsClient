package ab.planner;

import java.awt.Point;

import ab.vision.Vision;

public interface Strategy {
public Point getTarget(Vision vision);
}
