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
import static org.junit.Assert.assertTrue;

import org.csstudio.scan.condition.Condition;
import org.csstudio.scan.condition.WaitForDevicesCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.PVDevice;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of the {@link WaitForDevicesCondition}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitForDevicesConditionHeadlessTest
{
    @Test(timeout=3000)
    public void testAwait() throws Exception
    {
        final Device[] devices =
        {
                new PVDevice(new DeviceInfo("motor_x", "xpos", true, true)),
                new PVDevice(new DeviceInfo("motor_y", "ypos", true, true)),
        };

        final Condition connect = new WaitForDevicesCondition(devices);

        System.out.println(connect);
        assertTrue(connect.toString().startsWith("Waiting for device "));

        for (Device device : devices)
            device.start();

        connect.await();

        System.out.println(connect);
        assertEquals("All devices ready", connect.toString());

        for (Device device : devices)
            device.stop();
    }
}
