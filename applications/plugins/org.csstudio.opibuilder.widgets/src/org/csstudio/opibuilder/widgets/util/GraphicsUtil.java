package org.csstudio.opibuilder.widgets.util;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.widgets.figureparts.PolarPoint;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**The utility class contains functions that all related with graphics.
 * @author Xihui Chen
 *
 */
public class GraphicsUtil {

	public static synchronized boolean testPatternSupported(Graphics graphics){
		if(PreferencesHelper.isAdvancedGraphicsDisabled())
			return false;
		
		boolean support3D = true;
		//just test if pattern is supported on the platform.		
		try {						
			graphics.setBackgroundPattern(null);			
		} catch (Exception e) {
			support3D= false;				
		}
		
		return support3D;
	}
	
	/**Calculate the three points for an arrow.
	 * @param startPoint the start point of the line
	 * @param endPoint the end point of the line
	 * @param l the length of the arrow line
	 * @param angle the radians angle between the line and the arrow line.
	 * @return A point list which includes the three points:
	 * <br>0: Right arrow point;
	 * <br>1: Left arrow point;
	 * <br>2: Intersection point.
	 */
	public static PointList calcArrowPoints(Point startPoint, Point endPoint,
			int l, double angle){

		PointList result = new PointList();
		
		PolarPoint ppE = PolarPoint.point2PolarPoint(endPoint, startPoint);
		
		PolarPoint ppR = new PolarPoint(l, ppE.theta - angle);
		PolarPoint ppL = new PolarPoint(l, ppE.theta + angle);
		
		//the intersection point bettwen arrow and line.
		PolarPoint ppI = new PolarPoint((int) (l * Math.cos(angle)), ppE.theta); 
		
		Point pR = ppR.toPoint().translate(endPoint);
		Point pL = ppL.toPoint().translate(endPoint);
		Point pI = ppI.toPoint().translate(endPoint);
	
		result.addPoint(pR);
		result.addPoint(pL);
		result.addPoint(pI);
		
		return result;
		
	}
	
	
	
}
