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

package org.csstudio.sds.components.ui.internal.figures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;


/**
 *
 * @author Joerg Rathlev
 */
public class LinearAxisTest {

    @Test
    public void testSimpleMapping() {
        IAxis axis = new LinearAxis(0.0, 9.0, 10);
        assertEquals(0, axis.valueToCoordinate(0.0));
        assertEquals(1, axis.valueToCoordinate(1.0));
        assertEquals(9, axis.valueToCoordinate(9.0));
    }

    @Test
    public void valuesOutsideDataRange() throws Exception {
        IAxis axis = new LinearAxis(0.0, 9.0, 10);
        assertEquals(20, axis.valueToCoordinate(20.0));
        assertEquals(-10, axis.valueToCoordinate(-10.0));
    }

    @Test
    public void testRounding() throws Exception {
        LinearAxis axis = new LinearAxis(0.0, 9.0, 10);
        assertEquals(0, axis.valueToCoordinate(0.1));
        assertEquals(0, axis.valueToCoordinate(0.49));
        assertEquals(1, axis.valueToCoordinate(0.5));
        assertEquals(1, axis.valueToCoordinate(0.9));
        assertEquals(1, axis.valueToCoordinate(1.49));
        assertEquals(2, axis.valueToCoordinate(1.5));

        // negative values
        assertEquals(0, axis.valueToCoordinate(-0.1));
        assertEquals(0, axis.valueToCoordinate(-0.5));
        assertEquals(-1, axis.valueToCoordinate(-0.51));
    }

    @Test
    public void testScaling() throws Exception {
        IAxis axis = new LinearAxis(0.0, 1.0, 101);
        assertEquals(0, axis.valueToCoordinate(0.0));
        assertEquals(100, axis.valueToCoordinate(1.0));
        assertEquals(50, axis.valueToCoordinate(0.5));
        assertEquals(1, axis.valueToCoordinate(0.01));
    }

    @Test
    public void testSetDataRange() throws Exception {
        IAxis axis = new LinearAxis(0.0, 9.0, 10);
        assertEquals(0, axis.valueToCoordinate(0.0));
        axis.setDataRange(-9.0, 9.0);
        assertEquals(5, axis.valueToCoordinate(0.0));
    }

    @Test
    public void testSetDisplaySize() throws Exception {
        IAxis axis = new LinearAxis(0.0, 10.0, 10);
        assertEquals(5, axis.valueToCoordinate(5.0));
        axis.setDisplaySize(20);
        assertEquals(10, axis.valueToCoordinate(5.0));
    }

    @Test
    public void testInvalidDataRange() throws Exception {
        // This test simply checks that an axis can be created with an invalid
        // data range.
        IAxis axis = new LinearAxis(0.0, -10.0, 10);
        axis.setDataRange(10.0, 0.0);
    }

    @Test
    public void testExtrema() throws Exception {
        IAxis axis = new LinearAxis(0.0, 9.0, 10);
        assertEquals(Integer.MAX_VALUE, axis.valueToCoordinate(Double.MAX_VALUE));
        assertEquals(Integer.MIN_VALUE, axis.valueToCoordinate(-Double.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, axis.valueToCoordinate(Double.POSITIVE_INFINITY));
        assertEquals(Integer.MIN_VALUE, axis.valueToCoordinate(Double.NEGATIVE_INFINITY));
        // note: Double.MIN_VALUE is smallest *positive* nonzero value!
        assertEquals(0, axis.valueToCoordinate(Double.MIN_VALUE));
    }

    @Test
    public void testLegalValues() throws Exception {
        IAxis axis = new LinearAxis(0.0, 9.0, 10);
        // All values except NaN should be legal.
        assertTrue(axis.isLegalValue(Double.NEGATIVE_INFINITY));
        assertTrue(axis.isLegalValue(Double.POSITIVE_INFINITY));
        assertTrue(axis.isLegalValue(Double.MAX_VALUE));
        assertTrue(axis.isLegalValue(-Double.MAX_VALUE));

        assertFalse(axis.isLegalValue(Double.NaN));
    }

    @Test
    public void testCalculateTicks() throws Exception {
        IAxis axis = new LinearAxis(0.0, 9.0, 1000);
        // major ticks only
        List<Tick> ticks = axis.calculateTicks(100, -1);
        // expected: 10 ticks at 0.0, 1.0, ..., 9.0
        assertEquals(10, ticks.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(i, ticks.get(i).value(), 0.001);
        }
    }

    @Test
    public void testCalculateIntegerTicks() throws Exception {
        IAxis axis = new LinearAxis(0.0, 2.0, 1000);
        // major ticks only
        List<Tick> ticks = axis.calculateIntegerTicks(100, -1);
        // Because we want ticks only at integral numbers, we expect 3 ticks
        // at 0.0, 1.0, 2.0
        assertEquals(3, ticks.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i, ticks.get(i).value(), 0.001);
        }
    }

}
