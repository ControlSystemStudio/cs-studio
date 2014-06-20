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

import static org.epics.util.time.TimeDuration.ofSeconds;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.condition.NumericValueCondition;
import org.csstudio.scan.condition.TextValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceInfo;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.PVDevice;
import org.csstudio.scan.device.VTypeHelper;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link NumericValueCondition}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceValueConditionHeadlessTest
{
    @SuppressWarnings("unused")
    private long updates = 0;
    
    private PVDevice getDemoDevice() throws Exception
    {
    	final PVDevice device = new PVDevice(new DeviceInfo("loc://my_pv(0)", "demo"));
        
        // Register listener that notifies device so test code can wait for a device update
        device.addListener(new DeviceListener()
        {
			@Override
			public void deviceChanged(final Device device) 
			{
				synchronized (device)
				{
				    // Update something to avoid FindBugs "Naked Notify" warning
				    ++updates;
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
        final NumericValueCondition condition =
                new NumericValueCondition(device, Comparison.EQUALS, 3.0, 0.5, null);
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
        assertThat(condition.isConditionMet(), equalTo(true));
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

        // EQUALS
        {
            final NumericValueCondition equals = new NumericValueCondition(device, Comparison.EQUALS, 2.0, 0.001, null);
            assertThat(equals.isConditionMet(), equalTo(false));
            device.write(2.0);
            assertThat(equals.isConditionMet(), equalTo(true));

            equals.setDesiredValue(3.0);
            assertThat(equals.isConditionMet(), equalTo(false));
            device.write(3.0);
            assertThat(equals.isConditionMet(), equalTo(true));
        }

        // AT_LEAST
        {
            device.write(0.0);
            final NumericValueCondition above = new NumericValueCondition(device, Comparison.AT_LEAST, 2.0, 10.0, null);
            assertThat(above.isConditionMet(), equalTo(false));
            device.write(1.0);
            assertThat(above.isConditionMet(), equalTo(false));
            device.write(2.0);
            assertThat(above.isConditionMet(), equalTo(true)); // 2 >= 2

            above.setDesiredValue(3.0);
            assertThat(above.isConditionMet(), equalTo(false));
            device.write(3.0);
            assertThat(above.isConditionMet(), equalTo(true));
            device.write(4.0);
            assertThat(above.isConditionMet(), equalTo(true));
        }

        // BELOW
        {
            device.write(4.0);
            final NumericValueCondition below = new NumericValueCondition(device, Comparison.BELOW, 2.0, 10.0, null);
            assertThat(below.isConditionMet(), equalTo(false));
            device.write(2.0);
            assertThat(below.isConditionMet(), equalTo(false)); // ! (2.0 < 2.0)
            device.write(1.8);
            assertThat(below.isConditionMet(), equalTo(true));

            below.setDesiredValue(1.5);
            assertThat(below.isConditionMet(), equalTo(false));
            device.write(1.0);
            assertThat(below.isConditionMet(), equalTo(true));
            device.write(-4.0);
            assertThat(below.isConditionMet(), equalTo(true));
        }

        device.stop();
    }

    
    @Test(timeout=5000)
    public void testStaticTextConditions() throws Exception
    {
        // Device with local PV, updated by test
        final PVDevice device = getDemoDevice();
        device.start();
        device.write("Hello");
        
        do
        {
            synchronized (device) { device.wait(500); }
        }
        while (! VTypeHelper.toString(device.read()).equals("Hello"));

        // EQUALS
        {
            final TextValueCondition equals = new TextValueCondition(device, Comparison.EQUALS, "World", null);
            assertThat(equals.isConditionMet(), equalTo(false));
            device.write("World");
            synchronized (device) { device.wait(500); }
            assertThat(equals.isConditionMet(), equalTo(true));
        }

        // ABOVE
        {
            final TextValueCondition equals = new TextValueCondition(device, Comparison.ABOVE, "World", null);
            assertThat(equals.isConditionMet(), equalTo(false));
            device.write("Xenon");
            synchronized (device) { device.wait(500); }
            assertThat(equals.isConditionMet(), equalTo(true));
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
        final NumericValueCondition condition =
                new NumericValueCondition(device, Comparison.INCREASE_BY, 3.0, 0.0, null);
        assertThat(condition.isConditionMet(), equalTo(false));
        System.out.println("Initial value: " + VTypeHelper.toDouble(device.read()));
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
        while (VTypeHelper.toDouble(device.read()) != 1.0);

        // Wait for 1 second, never happens
        NumericValueCondition condition =
            new NumericValueCondition(device, Comparison.INCREASE_BY, 3.0, 0.0, ofSeconds(1.0));
        try
        {
            condition.await();
            fail("Condition did not time out");
        }
        catch (Exception ex)
        {
            assertThat(ex.getMessage().contains("Timeout"), equalTo(true));
            System.out.println("Received correct timeout for INCREASE_BY");
        }

        condition =
            new NumericValueCondition(device, Comparison.EQUALS, 3.0, 0.1, ofSeconds(1.0));
        try
        {
            condition.await();
            fail("Condition did not time out");
        }
        catch (Exception ex)
        {
            assertThat(ex.getMessage().contains("Timeout"), equalTo(true));
            System.out.println("Received correct timeout for EQUALS");
        }

        device.stop();
    }
}
