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
 /**
 * Owned by DESY.
 */
package org.csstudio.sds.components.ui.internal.feedback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.junit.Before;
import org.junit.Test;

/**
 * TestCases for {@link PointListHelper}.
 *
 * @author Sven Wende
 *
 */
public final class PointListHelperTest {

    /**
     * A sample point list.
     */
    private PointList _points;

    /**
     * Test case setup.
     */
    @Before
    public void setUp() {
        _points = new PointList();
        _points.addPoint(1, 1);
        _points.addPoint(1, 100);
        _points.addPoint(100, 100);
        _points.addPoint(100, 1);
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.components.ui.internal.feedback.PointListHelper#scaleToSize(org.eclipse.draw2d.geometry.PointList, int, int)}.
     */
    @Test
    public void testScaleToSize() {
        int newW = 40;
        int newH = 60;
        PointList newPoints = PointListHelper.scaleToSize(_points, newW, newH);
        Rectangle newBounds = newPoints.getBounds();

        assertTrue(newPoints!=_points); // working on copies?
        assertTrue(newBounds.width == newW);
        assertTrue(newBounds.height == newH);
        assertEquals(_points.getBounds().getLocation(), newBounds.getLocation()); // location should not change
        assertEquals(new Point(1, 1), newPoints.getPoint(0));
        assertEquals(new Point(1, newH), newPoints.getPoint(1));
        assertEquals(new Point(newW, newH), newPoints.getPoint(2));
        assertEquals(new Point(newW, 1), newPoints.getPoint(3));

        boolean exception = false;
        try {
            PointListHelper.scaleToSize(_points, -20, 10);
        } catch (IllegalArgumentException e) {
            exception = true;
        }

        assertTrue(exception);
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.components.ui.internal.feedback.PointListHelper#scaleToLocation(org.eclipse.draw2d.geometry.PointList, int, int)}.
     */
    @Test
    public void testScaleToLocation() {
        PointList newPoints = PointListHelper.scaleToLocation(_points, 2, 2);
        Rectangle newBounds = newPoints.getBounds();

        assertTrue(newPoints!=_points); // working on copies?
        assertEquals(_points.getBounds().getSize(), newBounds.getSize()); // size should not change
        assertTrue(newBounds.x == 2);
        assertTrue(newBounds.y == 2);
        assertEquals(new Point(2, 2), newPoints.getPoint(0));
        assertEquals(new Point(2, 101), newPoints.getPoint(1));
        assertEquals(new Point(101, 101), newPoints.getPoint(2));
        assertEquals(new Point(101, 2), newPoints.getPoint(3));
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.components.ui.internal.feedback.PointListHelper#scaleTo(org.eclipse.draw2d.geometry.PointList, org.eclipse.draw2d.geometry.Rectangle)}.
     */
    @Test
    public void testScaleTo() {
        PointList newPoints = PointListHelper.scaleTo(_points, new Rectangle(2, 2, 40, 60));
        Rectangle newBounds = newPoints.getBounds();
        assertTrue(newPoints!=_points); // working on copies?
        assertTrue(newBounds.x == 2);
        assertTrue(newBounds.y == 2);
        assertTrue(newBounds.width == 40);
        assertTrue(newBounds.height == 60);
        assertEquals(new Point(2, 2), newPoints.getPoint(0));
        assertEquals(new Point(2, 61), newPoints.getPoint(1));
        assertEquals(new Point(41, 61), newPoints.getPoint(2));
        assertEquals(new Point(41, 2), newPoints.getPoint(3));
    }

}
