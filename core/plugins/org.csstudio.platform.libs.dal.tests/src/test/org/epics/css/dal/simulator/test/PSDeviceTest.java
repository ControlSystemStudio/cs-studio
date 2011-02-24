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

import java.util.BitSet;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.LongProperty;
import org.epics.css.dal.PatternProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.device.AbstractDevice;
import org.epics.css.dal.device.PowerSupply;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.impl.LongPropertyImpl;
import org.epics.css.dal.impl.StringPropertyImpl;
import org.epics.css.dal.impl.test.AbstractDeviceTest;
import org.epics.css.dal.impl.test.DynamicValuePropertyTest;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.simulation.ps.PSDeviceProxy;
import org.epics.css.dal.simulation.ps.PowerSupplyImpl;
import org.epics.css.dal.spi.DefaultDeviceFactoryService;
import org.epics.css.dal.spi.DeviceFactory;
import org.epics.css.dal.spi.LinkPolicy;


public class PSDeviceTest extends AbstractDeviceTest
{
	private static AbstractDevice aDevice;
	private static AbstractApplicationContext ctx;
	private static DeviceFactory devFactory;

	public void testOnCommand()
	{
		PowerSupply psDevice = (PowerSupply)getDevice();
		PatternProperty status = psDevice.getStatus();

		try {
			psDevice.on();

			BitSet onStatus = status.getValue();
			psDevice.off();

			BitSet offStatus = status.getValue();
			assertFalse(onStatus.equals(offStatus));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected AbstractDevice getDevice()
	{
		if (aDevice == null) {
			try {
				aDevice = devFactory.getDevice("PS_1", PowerSupply.class, null);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}

			assertNotNull(aDevice);
		}

		return aDevice;
	}

	protected void setUp() throws Exception
	{
		if (ctx == null) {
			ctx = new DefaultApplicationContext("DeviceTest");
		}

		SimulatorPlug.getInstance()
		.registerDeviceImplementationClass(PowerSupply.class, PowerSupplyImpl.class);
		SimulatorPlug.getInstance()
		.registerDeviceProxyImplementationClass(PowerSupply.class,
		    PSDeviceProxy.class);

		if (devFactory == null) {
			assertNotNull(ctx);
			devFactory = DefaultDeviceFactoryService.getDeviceFactoryService()
				.getDeviceFactory(ctx, LinkPolicy.SYNC_LINK_POLICY);
			assertNotNull(devFactory);
			assertEquals(ctx, devFactory.getApplicationContext());
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		aDevice = null;
	}

	@Override
	protected AbstractApplicationContext getContext()
	{
		return ctx;
	}

	@Override
	protected DynamicValuePropertyTest getPropertyTest(
	    DynamicValueProperty property)
	{
		if (property.getClass().equals(DoublePropertyImpl.class)) {
			DoublePropertyTest test = new DoublePropertyTest((DoubleProperty)property);

			return test;
		} else if (property.getClass().equals(LongPropertyImpl.class)) {
			LongPropertyTest test = new LongPropertyTest((LongProperty)property);

			return test;
		} else if (property.getClass().equals(StringPropertyImpl.class)) {
			StringPropertyTest test = new StringPropertyTest((StringProperty)property);

			return test;
		}

		return null;
	}
}

/* __oOo__ */
