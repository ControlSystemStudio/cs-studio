package org.csstudio.sds.components.ui.internal.figureparts;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class PolarPoint {
	
	/**
	 * The radial coordinate 
	 */
	public int r;
	
	
	/**
	 * The angular coordinate in radians
	 */
	public double theta;	
	
	/**
	 * @param r
	 * @param theta
	 */
	public PolarPoint(int r, double theta) {
		this.r = r;
		this.theta = theta;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PolarPoint) {
			PolarPoint p = (PolarPoint)obj;
			return p.r == r && p.theta == theta;
		}
		return false;
	}
	
	/**
	 * Transform the polar point to the {@link Point} in rectangular coordinates. 
	 * The rectangular coordinates has the same origin as the polar coordinates.
	 * @return the point in rectangular coordinates
	 */
	public Point toPoint() {
		int x = (int) (r * Math.cos(theta));
		int y = (int) (-r * Math.sin(theta));
		return new Point(x, y);		
	}	
	
	/**
	 * Transform the polar point to the {@link Point} in the absolute coordinate system.
	 * It is assumed that the origin of the polar coordinate system is the central point of 
	 * the rectangle. 
	 * @param rect the paint area of the figure 
	 * @return the point in absolute coordinate system.
	 */
	public Point toAbsolutePoint(Rectangle rect) {
		Point p = this.toPoint();
		return p.translate(rect.width/2, rect.height/2).translate(
				rect.x, rect.y);
	}
	
	/**Transform the polar point to the {@link Point} in the relative coordinate system, 
	 * whose origin is (rect.x, rect.y). 
	 * It is assumed that the origin of the polar coordinate system is the central point of 
	 * the rectangle. 
	 * @param rect the paint area of the figure
	 * @return the point in relative coordinate system.
	 */
	public Point toRelativePoint(Rectangle rect) {
		Point p = this.toPoint();
		return p.translate(rect.width/2, rect.height/2);		
	}
	
	
}
