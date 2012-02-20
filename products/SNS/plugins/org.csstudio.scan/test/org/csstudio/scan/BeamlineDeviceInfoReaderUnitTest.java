/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.scan.device.BeamlineDeviceInfoReader;
import org.csstudio.scan.device.DeviceInfo;
import org.junit.Test;

/** JUnit Test of the {@link BeamlineDeviceInfoReader}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class BeamlineDeviceInfoReaderUnitTest
{
	@Test
	public void testReadDeviceContextFile() throws Exception
	{
		final DeviceInfo[] devices = BeamlineDeviceInfoReader.read("examples/beamline.xml");
		assertTrue(devices.length >= 4);

		for (DeviceInfo device : devices)
			System.out.println(device);

		DeviceInfo device = find(devices, "motor_x");
		assertNotNull(device);
		assertEquals("xpos", device.getAlias());
		assertTrue(device.isLoggable());
        assertTrue(device.isScannable());

        device = find(devices, "setpoint");
        assertNotNull(device);
        assertEquals("setpoint", device.getAlias());
        assertFalse(device.isLoggable());
        assertTrue(device.isScannable());

        device = find(devices, "readback");
        assertNotNull(device);
        assertEquals("readback", device.getAlias());
        assertTrue(device.isLoggable());
        assertFalse(device.isScannable());
	}

	/** @param devices
	 *  @param name Requested device name
	 *  @return {@link DeviceInfo} with specified name or <code>null</code>
	 */
    private DeviceInfo find(final DeviceInfo[] devices, final String name)
    {
        for (DeviceInfo device : devices)
            if (device.getName().equals(name))
                return device;
        return null;
    }
}
