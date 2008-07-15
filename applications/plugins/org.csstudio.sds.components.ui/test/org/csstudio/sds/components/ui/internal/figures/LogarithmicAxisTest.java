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

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 *
 */
public class LogarithmicAxisTest {
	
	@Test
	public void testValueToCoordinate() throws Exception {
		IAxis axis = new LogarithmicAxis(0.1, 100.0, 100);
		assertEquals(0, axis.valueToCoordinate(0.1));
		assertEquals(33, axis.valueToCoordinate(1.0));
		assertEquals(66, axis.valueToCoordinate(10.0));
		assertEquals(99, axis.valueToCoordinate(100.0));
	}
	
	@Ignore("error handling not designed yet")
	@Test(expected = IllegalArgumentException.class)
	public void lowerBoundEqualsZero() throws Exception {
		new LogarithmicAxis(0.0, 100.0, 100);
	}
	
	@Ignore("error handling not designed yet")
	@Test(expected = IllegalArgumentException.class)
	public void negativeLowerBound() throws Exception {
		new LogarithmicAxis(-1.0, 100.0, 100);
	}
	
	@Test
	public void testSetDataRange() throws Exception {
		IAxis axis = new LogarithmicAxis(0.1, 10.0, 100);
		assertEquals(99, axis.valueToCoordinate(10.0));
		axis.setDataRange(0.1, 100.0);
		assertEquals(66, axis.valueToCoordinate(10.0));
	}
	
	@Test
	public void testSetDisplaySize() throws Exception {
		IAxis axis = new LogarithmicAxis(0.1, 100.0, 10);
		assertEquals(3, axis.valueToCoordinate(1.0));
		axis.setDisplaySize(100);
		assertEquals(33, axis.valueToCoordinate(1.0));
	}
	
	@Ignore("error handling not designed yet")
	@Test
	public void testZeroValue() throws Exception {
		IAxis axis = new LogarithmicAxis(0.1, 100.0, 100);
		assertEquals(Integer.MIN_VALUE, axis.valueToCoordinate(0.0));
	}
	
	@Ignore("error handling not designed yet")
	@Test
	public void testNegativeValue() throws Exception {
		IAxis axis = new LogarithmicAxis(0.1, 100.0, 100);
		assertEquals(Integer.MIN_VALUE, axis.valueToCoordinate(-1.0));
	}

}
