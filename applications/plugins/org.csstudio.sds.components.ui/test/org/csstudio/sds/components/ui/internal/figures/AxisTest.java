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

import org.junit.Test;


/**
 * 
 * @author Joerg Rathlev
 */
public class AxisTest {
	
	@Test
	public void testSimpleMapping() {
		Axis axis = new Axis(0.0, 9.0, 10);
		assertEquals(0, axis.valueToCoordinate(0.0));
		assertEquals(1, axis.valueToCoordinate(1.0));
		assertEquals(9, axis.valueToCoordinate(9.0));
		
		// Values outside the data range
		assertEquals(20, axis.valueToCoordinate(20.0));
		assertEquals(-10, axis.valueToCoordinate(-10.0));
	}
	
	@Test
	public void testRounding() throws Exception {
		Axis axis = new Axis(0.0, 9.0, 10);
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
		Axis axis = new Axis(0.0, 1.0, 101);
		assertEquals(0, axis.valueToCoordinate(0.0));
		assertEquals(100, axis.valueToCoordinate(1.0));
		assertEquals(50, axis.valueToCoordinate(0.5));
		assertEquals(1, axis.valueToCoordinate(0.01));
	}
	
	@Test
	public void testSetDataRange() throws Exception {
		Axis axis = new Axis(0.0, 9.0, 10);
		assertEquals(0, axis.valueToCoordinate(0.0));
		axis.setDataRange(-9.0, 9.0);
		assertEquals(5, axis.valueToCoordinate(0.0));
	}
	
	@Test
	public void testSetDisplaySize() throws Exception {
		Axis axis = new Axis(0.0, 10.0, 10);
		assertEquals(5, axis.valueToCoordinate(5.0));
		axis.setDisplaySize(20);
		assertEquals(10, axis.valueToCoordinate(5.0));
	}

}
