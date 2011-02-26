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

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.EnumProperty;
import org.epics.css.dal.EnumPropertyCharacteristics;



public abstract class EnumPropertyTestStandard extends DynamicValuePropertyTest
{
	public EnumPropertyTestStandard()
	{
		super(EnumProperty.class);
	}

	@Override
	public boolean matchValue(Object expected, Object got)
	{
		assertEquals(got.getClass(), Long.class);
		assertEquals(((Long)expected).longValue(), ((Long)got).longValue());

		return true;
	}

	@Override
	public void testGetDataType()
	{
		assertEquals(getProperty().getDataType(), Long.class);
	}
	
	public void testEnums() {
		
		try {
	
			EnumProperty p= (EnumProperty)getProperty();
			
			String[] descs= p.getEnumDescriptions();
			assertNotNull(descs);
			assertEquals(descs, p.getCharacteristic(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS));
			
			Object[] vals= p.getEnumValues();
			assertNotNull(vals);
			assertEquals(vals, p.getCharacteristic(EnumPropertyCharacteristics.C_ENUM_VALUES));
			
			assertEquals(vals.length, descs.length);
			
			Long l= p.getMaximum();
			assertNotNull(l);
			assertEquals((long)vals.length, l.longValue());
			l= p.getMinimum();
			assertNotNull(l);
			assertEquals((long)0, l.longValue());
			
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
}

/* __oOo__ */
