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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceContext;
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
		final DeviceContext context = DeviceContext.getDefault();

		for (Device device : context.getDevices())
			System.out.println(device);

		final Device device = context.getDevice("xpos");
		assertNotNull(device);
		assertEquals("xpos", device.getAlias());
        assertEquals("motor_x", device.getName());
		assertEquals(PVDevice.class, device.getClass());
	}
}
