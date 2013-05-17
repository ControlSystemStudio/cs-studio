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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.epics.util.time.TimeDuration.ofSeconds;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.condition.DeviceValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.PVDevice;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link DeviceValueCondition}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceValueConditionHeadlessTest
{
    private PVDevice getDemoDevice() throws Exception
    {
    	final PVDevice device = new PVDevice(new DeviceInfo("loc://my_pv", "demo", true, true));
        
        // Register listener that notifies device so test code can wait for a device update
        device.addListener(new DeviceListener()
        {
			@Override
			public void deviceChanged(final Device device) 
			{
				synchronized (device)
				{
					device.notifyAll();
				}
			}
		});

        return device;
    }

    @Test(timeout=5000)
    public void testEqualsConditionWithRampPV() throws Exception
    {
        // Device with local PV, updated by test
        final PVDevice device = getDemoDevice();
        device.start();
        device.write(1.0);
        synchronized (device) { device.wait(500); }

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
        final DeviceValueCondition condition =
                new DeviceValueCondition(device, Comparison.EQUALS, 3.0, 0.5, null);
        condition.await();
        System.out.println("Value reached 3!");

        condition.setDesiredValue(5.0);
        condition.await();
        System.out.println("Value reached 5!");

        System.out.println("Setting value to 2.3");
        device.write(Double.valueOf(2.3));
        synchronized (device) { device.wait(500); }

        System.out.println("Checking for value that is already close to 2, i.e. possibly no updates");
        condition.setDesiredValue(2.0);
        assertTrue(condition.isConditionMet());
        condition.await();

        ramp.join();
        device.stop();
    }


    @Test(timeout=5000)
    public void testStaticConditions() throws Exception
    {
        // Device with local PV, updated by test
        final PVDevice device = getDemoDevice();
        device.start();
        device.write(1.0);
        synchronized (device) { device.wait(500); }

        // EQUALS
        {
            final DeviceValueCondition equals = new DeviceValueCondition(device, Comparison.EQUALS, 2.0, 0.001, null);
            assertFalse(equals.isConditionMet());
            device.write(2.0);
            synchronized (device) { device.wait(500); }
            assertTrue(equals.isConditionMet());

            equals.setDesiredValue(3.0);
            assertFalse(equals.isConditionMet());
            device.write(3.0);
            synchronized (device) { device.wait(500); }
            assertTrue(equals.isConditionMet());
        }

        // AT_LEAST
        {
            device.write(0.0);
            synchronized (device) { device.wait(500); }
            final DeviceValueCondition above = new DeviceValueCondition(device, Comparison.AT_LEAST, 2.0, 10.0, null);
            assertFalse(above.isConditionMet());
            device.write(1.0);
            synchronized (device) { device.wait(500); }
            assertFalse(above.isConditionMet());
            device.write(2.0);
            synchronized (device) { device.wait(500); }
            assertTrue(above.isConditionMet()); // 2 >= 2

            above.setDesiredValue(3.0);
            assertFalse(above.isConditionMet());
            device.write(3.0);
            synchronized (device) { device.wait(500); }
            assertTrue(above.isConditionMet());
            device.write(4.0);
            synchronized (device) { device.wait(500); }
            assertTrue(above.isConditionMet());
        }

        // BELOW
        {
            device.write(4.0);
            synchronized (device) { device.wait(500); }
            final DeviceValueCondition below = new DeviceValueCondition(device, Comparison.BELOW, 2.0, 10.0, null);
            assertFalse(below.isConditionMet());
            device.write(2.0);
            synchronized (device) { device.wait(500); }
            assertFalse(below.isConditionMet()); // ! (2.0 < 2.0)
            device.write(1.8);
            synchronized (device) { device.wait(500); }
            assertTrue(below.isConditionMet());

            below.setDesiredValue(1.5);
            assertFalse(below.isConditionMet());
            device.write(1.0);
            synchronized (device) { device.wait(500); }
            assertTrue(below.isConditionMet());
            device.write(-4.0);
            synchronized (device) { device.wait(500); }
            assertTrue(below.isConditionMet());
        }

        device.stop();
    }


    @Test(timeout=5000)
    public void testIncreasedByConditionWithRampPV() throws Exception
    {
        // Device with local PV, updated by test
        final PVDevice device = getDemoDevice();
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
        final DeviceValueCondition condition =
                new DeviceValueCondition(device, Comparison.INCREASE_BY, 3.0, 0.0, null);
        assertFalse(condition.isConditionMet());
        System.out.println("Initial value: " + device.readDouble());
        condition.await();
        System.out.println("Value increased by 3!");

        ramp.join();
        device.stop();
    }


    @Test(timeout=4000)
    public void testTimeout() throws Exception
    {
        // Device with local PV, updated by test
        final PVDevice device = getDemoDevice();
        device.start();
        device.write(1.0);
        do
        {
        	synchronized (device) { device.wait(100); }
        }
        while (device.readDouble() != 1.0);

        // Wait for 1 second, never happens
        DeviceValueCondition condition =
            new DeviceValueCondition(device, Comparison.INCREASE_BY, 3.0, 0.0, ofSeconds(1.0));
        try
        {
            condition.await();
            fail("Consition did not time out");
        }
        catch (Exception ex)
        {
            assertTrue(ex.getMessage().contains("Timeout"));
            System.out.println("Received correct timeout for INCREASE_BY");
        }

        condition =
            new DeviceValueCondition(device, Comparison.EQUALS, 3.0, 0.1, ofSeconds(1.0));
        try
        {
            condition.await();
            fail("Consition did not time out");
        }
        catch (Exception ex)
        {
            assertTrue(ex.getMessage().contains("Timeout"));
            System.out.println("Received correct timeout for EQUALS");
        }

        device.stop();
    }
}
