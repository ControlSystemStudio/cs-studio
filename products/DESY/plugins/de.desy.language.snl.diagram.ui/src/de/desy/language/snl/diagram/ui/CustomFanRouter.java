package de.desy.language.snl.diagram.ui;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

public class CustomFanRouter extends AutomaticRouter {
	
	private int _separation = 10;

	@Override
	protected void handleCollision(PointList list, int index) {
		Point firstPoint = list.getFirstPoint();
		Point lastPoint = list.getLastPoint();
		
		if (index != 0) {
			list.removeAllPoints();
			list.addAll(calculateNewPoints(firstPoint, lastPoint, index));
		}
	}
	
	private PointList calculateNewPoints(Point firstPoint, Point lastPoint, int index) {
		PointList result = new PointList();
		return result;
	}

	public void setSeparation(int separation) {
		_separation = separation;
	}

	public int getSeparation() {
		return _separation;
	}

}
