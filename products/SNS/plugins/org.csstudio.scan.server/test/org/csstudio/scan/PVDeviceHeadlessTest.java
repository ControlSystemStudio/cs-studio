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

import java.util.concurrent.CountDownLatch;

import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.PVDevice;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link PVDevice}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVDeviceHeadlessTest implements DeviceListener
{
    final private CountDownLatch updates = new CountDownLatch(1);

    /** Check if device can be created, sends updates */
    @Test(timeout=5000)
    public void testPVDevice() throws Exception
    {
        final PVDevice device = new PVDevice(new DeviceInfo("sim://sine", "demo", true, true));
        device.start();
        try
        {
            device.addListener(this);

            // Wait for update
            updates.await();
        }
        finally
        {
            device.stop();
        }
    }

    // DeviceListener
    @Override
    public void deviceChanged(final Device device)
    {
        try
        {
            System.out.println(device + " = " + device.read());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        updates.countDown();
    }
}
