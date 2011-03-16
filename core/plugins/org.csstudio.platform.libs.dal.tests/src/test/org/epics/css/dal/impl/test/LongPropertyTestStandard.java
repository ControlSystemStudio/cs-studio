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

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DoubleAccess;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.IllegalViewException;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.StringAccess;


public abstract class LongPropertyTestStandard extends DynamicValuePropertyTest
{
	public LongPropertyTestStandard()
	{
		super(LongProperty.class);
	}

	public void testLongPropertyCharacteristics() throws DataExchangeException
	{
		LongProperty prop = (LongProperty)getProperty();

		assertEquals(prop.getMaximum(),
		    prop.getCharacteristic(NumericPropertyCharacteristics.C_MAXIMUM));
		assertEquals(prop.getMaximum().getClass(), Long.class);

		assertEquals(prop.getMinimum(),
		    prop.getCharacteristic(NumericPropertyCharacteristics.C_MINIMUM));
		assertEquals(prop.getMinimum().getClass(), Long.class);

		assertTrue(prop.getMaximum() >= prop.getMinimum());
	}

	public void testDAMultiplexers()
		throws DataExchangeException, IllegalViewException
	{
		LongProperty prop = (LongProperty)getProperty();
		DynamicValueProperty property = (DynamicValueProperty)prop;
		property.setValue(getRandomValue());

		StringAccess sa = prop.getDataAccess(StringAccess.class);
		Long value = prop.getValue();
		assertEquals(value.toString(), sa.getValue());

		String strVal = "12345";
		sa.setValue(strVal);
		assertEquals(strVal, prop.getValue().toString());
		assertEquals(new Long(Long.parseLong(strVal)), prop.getValue());

		DoubleAccess da = prop.getDataAccess(DoubleAccess.class);
		assertEquals(da.getValue(), new Double(prop.getValue().doubleValue()));

		Double doubleVal = 10.0;
		da.setValue(doubleVal);
		assertEquals(doubleVal, new Double(prop.getValue().doubleValue()));

		DynamicValueListenerImpl dvl1 = new DynamicValueListenerImpl();
		DynamicValueListenerImpl dvl2 = new DynamicValueListenerImpl();
		prop.addDynamicValueListener(dvl1);
		sa.addDynamicValueListener(dvl2);

		try {
			synchronized (dvl1) {
				property.setValue(getRandomValue());
				dvl1.wait(1000);
				assertEquals(dvl1.lastValue.toString(), dvl2.lastValue);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		prop.removeDynamicValueListener(dvl1);
		sa.removeDynamicValueListener(dvl2);
	}

	@Override
	public Object getRandomValue()
	{
		return new Long((long)(Math.random() * 10));
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
}

/* __oOo__ */
