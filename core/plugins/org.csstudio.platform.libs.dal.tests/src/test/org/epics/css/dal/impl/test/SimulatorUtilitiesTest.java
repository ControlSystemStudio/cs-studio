/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.epics.css.dal.impl.test;

import junit.framework.TestCase;

import org.epics.css.dal.DoubleSimpleProperty;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.simulation.DoublePropertyProxyImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.simulation.SimulatorUtilities;


public class SimulatorUtilitiesTest extends TestCase
{
	/*
	 * Test method for 'org.epics.css.dal.simulation.SimulatorUtilities.getProxyImplementationClass(Class<? extends SimpleProperty>)'
	 */
	public final void testGetProxyImplementationClass()
	{
		Class c = SimulatorUtilities
			.getPropertyProxyImplementationClass(DoubleSimpleProperty.class,DoublePropertyImpl.class);

		assertEquals(DoublePropertyProxyImpl.class, c);
	}
	
	public void testCharacteristic() {
		
		try {
		
			PropertyProxy ppi= SimulatorPlug.getInstance().getPropertyProxy("P1");
			
			Object value = "SOMETHING";
			
			Object ret= SimulatorUtilities.putCharacteristic("ch", "P1", value);

			assertNull(ret);
			
			ret= SimulatorUtilities.getCharacteristic("ch", ppi);
			
			assertNotNull(ret);
			assertEquals(value, ret);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
}

/* __oOo__ */
