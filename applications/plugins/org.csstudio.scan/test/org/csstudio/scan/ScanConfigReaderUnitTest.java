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

import static org.csstudio.utility.test.HamcrestMatchers.greaterThanOrEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.ScanConfig;
import org.junit.Test;

/** JUnit Test of the {@link ScanConfig}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanConfigReaderUnitTest
{
	@Test
	public void testReadLegacyFile() throws Exception
	{
		ScanConfig config = new ScanConfig("examples/beamline.xml");
        final DeviceInfo[] devices = config.getDevices();
		assertThat(devices.length, greaterThanOrEqualTo(4));

		for (DeviceInfo device : devices)
			System.out.println(device);

		DeviceInfo device = find(devices, "motor_x");
		assertThat(device, not(nullValue()));
		assertThat(device.getAlias(), equalTo("xpos"));

        device = find(devices, "setpoint");
        assertThat(device, not(nullValue()));
        assertThat(device.getAlias(), equalTo("setpoint"));


        device = find(devices, "readback");
        assertThat(device, not(nullValue()));
        assertThat(device.getAlias(), equalTo("readback"));

        config = new ScanConfig("examples/simulation.xml");
        assertThat(config.getSlewRate("neutrons"), equalTo(7.0));
	}

	@Test
	public void testConfigFile() throws Exception
	{
        final ScanConfig config = new ScanConfig("examples/scan_config.xml");
        final DeviceInfo[] devices = config.getDevices();
        assertThat(devices.length, greaterThanOrEqualTo(4));

        for (DeviceInfo device : devices)
            System.out.println(device);

        DeviceInfo device = find(devices, "motor_x");
        assertThat(device, not(nullValue()));
        assertThat(device.getAlias(), equalTo("xpos"));

        device = find(devices, "setpoint");
        assertThat(device, not(nullValue()));
        assertThat(device.getAlias(), equalTo("setpoint"));

        device = find(devices, "readback");
        assertThat(device, not(nullValue()));
        assertThat(device.getAlias(), equalTo("readback"));
        
        assertThat(config.getSlewRate("neutrons"), equalTo(7.0));
        // Not specifically listed, but there's ".pos" pattern
        assertThat(find(devices, "qpos"), nullValue());
        assertThat(config.getSlewRate("qpos"), equalTo(0.2));
        
        // Unknown PV
        assertThat(config.getSlewRate("whatever"), equalTo(ScanConfig.DEFAULT_SLEW_RATE));
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
