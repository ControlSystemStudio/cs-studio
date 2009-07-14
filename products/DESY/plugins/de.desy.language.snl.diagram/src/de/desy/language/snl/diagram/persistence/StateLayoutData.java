package de.desy.language.snl.diagram.persistence;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

public class StateLayoutData {
	
	private final Point _point;
	private final Dimension _dimension;

	public StateLayoutData(Point point, Dimension dimension) {
		assert point != null : "point != null";
		assert dimension != null : "dimension != null";
		
		_point = point;
		_dimension = dimension;
	}

	public Point getPoint() {
		return _point;
	}

	public Dimension getDimension() {
		return _dimension;
	}

}
