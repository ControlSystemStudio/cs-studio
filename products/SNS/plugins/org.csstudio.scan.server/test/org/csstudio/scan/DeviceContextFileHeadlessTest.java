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
import org.csstudio.scan.device.DeviceContext;
import org.csstudio.scan.device.DeviceContextFile;
import org.csstudio.scan.device.PVDevice;
import org.junit.Test;

/** [Headless] JUnit Plug-in Test of the DeviceContextFile
 * 
 *  Needs plugin support because devices access PVs
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceContextFileHeadlessTest
{
	@Test
	public void testReadDeviceContextFile() throws Exception
	{
		final DeviceContext devices = DeviceContextFile.read("examples/devices.xml");
		assertEquals(4, devices.getDevices().length);
		
		for (Device device : devices.getDevices())
			System.out.println(device);
		
		final Device device = devices.getDevice("xpos");
		assertNotNull(device);
		assertEquals("xpos", device.getName());
		assertEquals(PVDevice.class, device.getClass());

		final String text = device.toString();
		assertTrue(text.contains("Device 'xpos'"));
		assertTrue(text.contains("motor_x"));
	}
}
