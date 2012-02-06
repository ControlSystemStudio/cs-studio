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
		assertEquals(4, devices.length);

		for (DeviceInfo device : devices)
			System.out.println(device);

		assertEquals("motor_x", devices[0].getName());
		assertEquals("xpos", devices[0].getAlias());
	}
}
