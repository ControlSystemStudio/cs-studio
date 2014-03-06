/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.*;

import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.SimulatedDevice;
import org.csstudio.scan.device.VTypeHelper;
import org.junit.Test;
import static org.csstudio.utility.test.HamcrestMatchers.greaterThanOrEqualTo;

/** JUnit test of the {@link SimulatedDevice}s
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulatedDeviceUnitTest implements DeviceListener
{
	private int changes = 0;

	@Test
	public void testSpreadsheet() throws Exception
	{
		final Device x = new SimulatedDevice("x");
		x.addListener(this);

		x.write(3.14);
		assertThat(changes, greaterThanOrEqualTo(1));

		assertEquals(3.14, VTypeHelper.toDouble(x.read()), 0.01);
	}

	@Override
    public void deviceChanged(final Device device)
    {
		try
        {
	        System.out.println(device + " = " + device.read());
	        ++changes;
        }
        catch (Exception e)
        {
	        e.printStackTrace();
        }
    }
}
