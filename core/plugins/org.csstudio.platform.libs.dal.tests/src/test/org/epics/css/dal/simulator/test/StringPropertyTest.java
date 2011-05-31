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

package org.epics.css.dal.simulator.test;

import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.PropertyCharacteristics;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.impl.test.StringPropertyTestStandard;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;


public class StringPropertyTest extends StringPropertyTestStandard
{
	private static StringProperty prop;
	private static String name;
	private AbstractApplicationContext ctx;
	private PropertyFactory pfac;

	public StringPropertyTest()
	{
		super();
	}

	public StringPropertyTest(StringProperty property)
	{
		prop = property;
		name = property.getName();
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
			name = new String("StringProperty");

			try {
				prop = pfac.getProperty(name, StringProperty.class, l);
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.toString());
			}
		}

		return prop;
	}

	@Override
	public String getPropertyUniqueName()
	{
		return name;
	}

	@Override
	protected void setUp() throws Exception
	{
		if (ctx == null) {
			ctx = new DefaultApplicationContext("SimulatorJUnitTest");
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
	protected void tearDown() throws Exception {
		pfac.getPropertyFamily().destroy(prop);
		prop = null;
	}
	
	@Override
	protected AbstractApplicationContext getContext()
	{
		return ctx;
	}

	@Override
	protected Object getExpectedCharacteristicValue(String characteristicName)
	{
		if (PropertyCharacteristics.C_DESCRIPTION.equals(characteristicName)) {
			return "Simulated Property";
		} else if (PropertyCharacteristics.C_POSITION.equals(characteristicName)) {
			return new Double(0.0);
		} else if (PropertyCharacteristics.C_PROPERTY_TYPE.equals(
		        characteristicName)) {
			return "property";
		}

		return null;
	}
}

/* __oOo__ */
