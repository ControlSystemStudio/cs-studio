package org.csstudio.opibuilder.widgets.util;

import org.csstudio.opibuilder.widgets.figureparts.PolarPoint;
import org.csstudio.opibuilder.widgets.model.PolyLineModel.ArrowType;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

public class GraphicsUtil {

	public static synchronized boolean testPatternSupported(Graphics graphics){
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
	
	public static Rectangle getPointsBoundsWithArrows(PointList points, ArrowType arrowType, int arrowLength, double arrowAngle){
		PointList copy = points.getCopy();
		if(points.size() >=2){		
			if(arrowType == ArrowType.To || arrowType == ArrowType.Both)
				copy.addAll(calcArrowPoints(points.getPoint(points.size()-2),
					points.getLastPoint(), arrowLength, arrowAngle));
			if(arrowType == ArrowType.From || arrowType == ArrowType.Both)
				copy.addAll(calcArrowPoints(points.getPoint(1),
					points.getFirstPoint(), arrowLength, arrowAngle));				
		}
		return copy.getBounds();
	}
	
}
