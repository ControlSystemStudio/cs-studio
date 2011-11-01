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


import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.device.PVDevice;
import org.junit.Test;


/** [Headless] JUnit Plug-in test of the {@link DeviceValueCondition}
 *  @author Kay Kasemir
 */
public class DeviceValueConditionHeadlessTest
{
    @Test(timeout=5000)
    public void testPVDevice() throws Exception
    {
        // Device with local PV, updated by test
        final PVDevice device = new PVDevice("demo", "loc://my_pv");
        device.start();
        device.write(1.0);

        // Thread that ramps PV from 1 to 5
        final Thread ramp = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    for (int i=1; i<=5; ++i)
                    {
                        Thread.sleep(500l);
                        System.out.println("Setting value to " + i);
                        device.write(Double.valueOf(i));
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

        }, "RampValues");
        ramp.start();

        // Wait for values on ramp
        final DeviceValueCondition condition = new DeviceValueCondition(device, 3.0, 0.5);
        condition.await();
        System.out.println("Value reached 3!");

        condition.setDesiredValue(5.0);
        condition.await();
        System.out.println("Value reached 5!");

        System.out.println("Setting value to 2.3");
        device.write(Double.valueOf(2.3));

        System.out.println("Checking for value that is already close to 2, i.e. possibly no updates");
        condition.setDesiredValue(2.0);
        condition.await();

        ramp.join();
        device.stop();
    }
}
