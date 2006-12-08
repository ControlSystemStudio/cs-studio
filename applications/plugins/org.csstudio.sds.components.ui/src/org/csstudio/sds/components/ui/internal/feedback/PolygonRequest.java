package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.requests.CreateRequest;

/**
 * A custom request type, which is only used for the creation of polygons or
 * polylines.
 * 
 * @author Sven Wende
 */
public final class PolygonRequest extends CreateRequest {
	/**
	 * The _points of the polygons, that have already been set.
	 */
	private PointList _points = new PointList();

	/**
	 * Gets the point list, which contains the polygon _points.
	 * 
	 * @return the polygon point list
	 */
	public PointList getPoints() {
		return _points;
	}
}
