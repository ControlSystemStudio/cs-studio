/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.csstudio.scan.command.ScanScript;
import org.csstudio.scan.command.ScanScriptContext;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.log.MemoryDataLog;
import org.csstudio.scan.server.JythonSupport;
import org.diirt.util.time.TimeDuration;
import org.junit.Test;

/** JUnit test of {@link JythonSupport}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class JythonSupportUnitTest
{
    @Test//(timeout=10000)
    public void testJythonSupport() throws Exception
    {
        TestSettings.init();

        // Load ScanScript
        final JythonSupport jython = new JythonSupport();
        final ScanScript script = jython.loadClass(ScanScript.class, "JythonTest");

        // Get device list
        final String[] devices = script.getDeviceNames();
        System.out.println(Arrays.toString(devices));
        assertEquals(2, devices.length);
        assertEquals("device2", devices[1]);

        // Run
        final MemoryDataLog log = new MemoryDataLog();
        final ScanScriptContext context = new ScanScriptContext()
        {
            @Override
            public ScanData getScanData() throws Exception
            {
                return null;
            }

            @Override
            public Object read(String device_name) throws Exception
            {
                // NOP
                return null;
            }

            @Override
            public void write(String device_name, Object value, String readback,
                    boolean wait, double tolerance, TimeDuration timeout) throws Exception
            {
                // NOP
            }

            @Override
            public void logData(final String device, final Object value) throws Exception
            {
                log.log(device, ScanSampleFactory.createSample(new Date(), 1, value));
            }
        };
        script.run(context);
        // Script should have logged one sample
        final List<ScanSample> samples = log.getScanData().getSamples("device1");
        System.out.println("Got samples: " + samples.size());
        assertEquals(1, samples.size());
        System.out.println("Value: " + samples.get(0));
        assertEquals(42.0, (Double) samples.get(0).getValues()[0], 0.01);
        log.close();

        jython.close();
    }
}
