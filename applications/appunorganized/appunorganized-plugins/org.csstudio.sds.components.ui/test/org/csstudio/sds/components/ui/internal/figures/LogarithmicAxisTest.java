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

import org.junit.Before;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 *
 */
public class LogarithmicAxisTest {

    private IAxis _axis;

    @Before
    public void setUp() {
        // axis:
        // 4.9E-324 = Double.MIN_VALUE
        // 1e-323 --> y = -966
        // ...         ...
        // 1e-1   --> y = 0
        // 1e0    --> y = 3
        // 1e1    --> y = 6
        // 1e2    --> y = 9
        // ...         ...
        // 1e308  --> y = 927
        // 1.79..E308 = Double.MAX_VALUE
        _axis = new LogarithmicAxis(0.1, 100.0, 10);
    }

    @Test
    public void testValueToCoordinate() throws Exception {
        assertEquals(0, _axis.valueToCoordinate(0.1));
        assertEquals(3, _axis.valueToCoordinate(1.0));
        assertEquals(6, _axis.valueToCoordinate(10.0));
        assertEquals(9, _axis.valueToCoordinate(100.0));
    }

    @Test
    public void invalidDataRange() throws Exception {
        // Make sure that the axis can be created with or set to an invalid
        // data range.
        final IAxis axis = new LogarithmicAxis(10.0, 1.0, 10);
        axis.setDataRange(20.0, 5.0);

        // For logarithmic axis, any range that includes zero is invalid, too!
        axis.setDataRange(0.0, 10.0);
        axis.setDataRange(-10.0, 10.0);
    }

    @Test
    public void testSetDataRange() throws Exception {
        assertEquals(9, _axis.valueToCoordinate(100.0));
        _axis.setDataRange(1.0, 1e9);
        assertEquals(2, _axis.valueToCoordinate(100.0));
    }

    @Test
    public void testSetDisplaySize() throws Exception {
        assertEquals(3, _axis.valueToCoordinate(1.0));
        _axis.setDisplaySize(100);
        assertEquals(33, _axis.valueToCoordinate(1.0));
    }

    @Test
    public void valuesOutsideRange() throws Exception {
        assertEquals(12, _axis.valueToCoordinate(1000.0));
        assertEquals(-3, _axis.valueToCoordinate(0.01));
    }

    @Test
    public void testExtrema() throws Exception {
        assertEquals(-966, _axis.valueToCoordinate(1e-323));
        assertEquals(927, _axis.valueToCoordinate(1e308));
        assertEquals(Integer.MAX_VALUE, _axis.valueToCoordinate(Double.POSITIVE_INFINITY));
    }

    @Test
    public void testLegalValues() throws Exception {
        // All values > 0 should be legal
        assertTrue(_axis.isLegalValue(Double.POSITIVE_INFINITY));
        assertTrue(_axis.isLegalValue(Double.MAX_VALUE));
        assertTrue(_axis.isLegalValue(Double.MIN_VALUE));

        assertFalse(_axis.isLegalValue(Double.NaN));
        assertFalse(_axis.isLegalValue(0.0));
        assertFalse(_axis.isLegalValue(-Double.MIN_VALUE));
        assertFalse(_axis.isLegalValue(-1.0));
        assertFalse(_axis.isLegalValue(-Double.MAX_VALUE));
        assertFalse(_axis.isLegalValue(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testCalculateTicks() throws Exception {
        final IAxis axis = new LogarithmicAxis(1e0, 1e9, 1000);
        // major ticks only
        final List<Tick> ticks = axis.calculateTicks(100, -1);
        // expected: 10 ticks at 1e0, 1e1, ..., 1e9
        //System.out.println(ticks);
        assertEquals(10, ticks.size());
        for (int i = 0; i < 10; i++) {
            assertEquals(Math.pow(10, i), ticks.get(i).value(), 0.001);
        }
    }


}
