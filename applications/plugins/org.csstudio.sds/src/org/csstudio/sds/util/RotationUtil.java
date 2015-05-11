/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
