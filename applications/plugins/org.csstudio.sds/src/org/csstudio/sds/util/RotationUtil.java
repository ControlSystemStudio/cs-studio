package org.csstudio.sds.util;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;

/**
 * This class can be used to rotate a point.
 * @author Kai Meyer
 *
 */
public final class RotationUtil {
	
	/**
	 * Private constructor, to avoid instantiation.
	 */
	private RotationUtil() {}
	
	/**
	 * Rotates the given {@link Point} with the given angle relative to the rotation point.
	 * Converts the given point to a {@link PrecisionPoint} and calls {@link #doRotate(PrecisionPoint, double, PrecisionPoint)}.
	 * @param point The {@link Point} to rotate
	 * @param angle The angle to rotate (in Degrees)
	 * @param rotationPoint The rotation point
	 * @return The rotated Point
	 */
	public static PrecisionPoint rotate(final Point point, final double angle, final Point rotationPoint) {
		PrecisionPoint pPoint = point instanceof PrecisionPoint ? (PrecisionPoint) point : new PrecisionPoint(point);
		PrecisionPoint pRotationPoint = rotationPoint instanceof PrecisionPoint ? (PrecisionPoint) rotationPoint : new PrecisionPoint(rotationPoint);
		
		return doRotate(pPoint, angle, pRotationPoint);
	}
	
	/**
	 * Rotates the given {@link Point} with the given angle relative to the rotation point.
	 * @param point The {@link Point} to rotate
	 * @param angle The angle to rotate (in Degrees)
	 * @param rotationPoint The rotation point
	 * @return The rotated Point
	 */
	public static PrecisionPoint doRotate(final PrecisionPoint point, final double angle, final PrecisionPoint rotationPoint) {
		assert point!=null : "Precondition violated: point!=null";
		assert rotationPoint!=null : "Precondition violated: rotationPoint!=null";
		double trueAngle = Math.toRadians(angle);
		double sin = Math.sin(trueAngle);
		double cos = Math.cos(trueAngle);
		
		//Relative coordinates to the rotation point
		double relX = point.preciseX-rotationPoint.preciseX;
		double relY = point.preciseY-rotationPoint.preciseY;
		
		double temp = relX * cos - relY * sin;

		double y = relX * sin + relY * cos;
		double x = temp;
		
		return new PrecisionPoint(x+rotationPoint.x,y+rotationPoint.y);
	}

}
