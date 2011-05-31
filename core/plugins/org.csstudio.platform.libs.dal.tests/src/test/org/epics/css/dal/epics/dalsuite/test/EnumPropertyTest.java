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

package org.epics.css.dal.epics.dalsuite.test;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.EnumProperty;
import org.epics.css.dal.EnumPropertyCharacteristics;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.epics.EPICSApplicationContext;
import org.epics.css.dal.epics.PropertyFactoryImpl;
import org.epics.css.dal.impl.test.EnumPropertyTestStandard;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;


public class EnumPropertyTest extends EnumPropertyTestStandard
{
	private static PropertyFactory pfac;
	private static AbstractApplicationContext ctx;
	private static EnumProperty prop;
	private static String name;
	private long curVal = 0;

	public EnumPropertyTest()
	{
		super();
		name = new String("PV_MBBI_01");
	}

	public EnumPropertyTest(EnumProperty property)
	{
		prop = property;
		name = property.getName();
	}

	@Override
	public String getPropertyUniqueName()
	{
		return name;
	}

	@Override
	public DynamicValueProperty getProperty()
	{
		return getProperty(null);
	}

	@Override
	public DynamicValueProperty getProperty(LinkListener l)
	{
		if (prop == null) {
			try {
				prop = pfac.getProperty(name, EnumProperty.class, l);
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.toString());
			}
		}

		return prop;
	}

	public Object getRandomValue()
	{
		Long rv = new Long(curVal);

		try {
			curVal++;

			if (curVal > prop.getMaximum()) {
				curVal = prop.getMinimum();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rv;
	}

	@Override
	protected void setUp() throws Exception
	{
		if (ctx == null) {
			ctx = new EPICSApplicationContext("EPICSJUnitTest");
		}

		if (pfac == null) {
			pfac = DefaultPropertyFactoryService.getPropertyFactoryService()
				.getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);
			assertNotNull(pfac);
			assertEquals(LinkPolicy.SYNC_LINK_POLICY, pfac.getLinkPolicy());
			assertEquals(ctx, pfac.getApplicationContext());
			assertEquals(PropertyFactoryImpl.class, pfac.getClass());
		}
	}

	@Override
	protected AbstractApplicationContext getContext()
	{
		return ctx;
	}

	@Override
	protected Object getExpectedCharacteristicValue(String characteristicName) throws Exception
	{
		if (NumericPropertyCharacteristics.C_DESCRIPTION.equals(
		        characteristicName)) {
			return "EPICS Channel 'PV_MBBI_01'";
		} else if (NumericPropertyCharacteristics.C_POSITION.equals(
		        characteristicName)) {
			return new Double(0);
		} else if (NumericPropertyCharacteristics.C_PROPERTY_TYPE.equals(
		        characteristicName)) {
			return "property";
		} else if (NumericPropertyCharacteristics.C_RESOLUTION.equals(
		        characteristicName)) {
			return 0xF;
		} else if (NumericPropertyCharacteristics.C_SCALE_TYPE.equals(
		        characteristicName)) {
			return "linear";
		} else if (NumericPropertyCharacteristics.C_UNITS.equals(
		        characteristicName)) {
			return "N/A";
		} else if (NumericPropertyCharacteristics.C_FORMAT.equals(
		        characteristicName)) {
			return "%d";
		} else if (NumericPropertyCharacteristics.C_GRAPH_MAX.equals(
		        characteristicName)) {
			return new Long(((String[])getProperty().getCharacteristic(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS)).length);
		} else if (NumericPropertyCharacteristics.C_GRAPH_MIN.equals(
		        characteristicName)) {
			return new Long(0);
		} else if (NumericPropertyCharacteristics.C_MAXIMUM.equals(
		        characteristicName)) {
			return new Long(((String[])getProperty().getCharacteristic(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS)).length);
		} else if (NumericPropertyCharacteristics.C_MINIMUM.equals(
		        characteristicName)) {
			return new Long(0);
		}

		return null;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		if (pfac!=null && prop!=null) {
			pfac.getPropertyFamily().destroy(prop);
			prop=null;
		}
	}
}

/* __oOo__ */
