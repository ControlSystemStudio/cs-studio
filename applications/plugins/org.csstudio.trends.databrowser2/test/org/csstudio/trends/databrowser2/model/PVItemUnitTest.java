/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.csstudio.utility.test.HamcrestMatchers.closeTo;
import static org.csstudio.utility.test.HamcrestMatchers.greaterThanOrEqualTo;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Timer;

import org.csstudio.swt.xygraph.dataprovider.IDataProvider;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVWriter;
import org.junit.Before;
import org.junit.Test;

/** JUnit test of the PVItem
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVItemUnitTest
{
    /** Time in seconds for each test */
    private static final double RUNTIME_SECS = 10.0;

    @Before
    public void setup()
    {
        TestHelper.setup();
    }
    
    /** Check if PVItem scans its PV */
    @Test
    public void testScannedPVItem() throws Exception
    {
        System.out.println("Scanned samples: (" + RUNTIME_SECS + " secs)");
        final Timer scan_timer = new Timer();
        final PVItem pv = new PVItem("sim://sine(0,10,10,1)", 1.0);
        pv.start(scan_timer);
        Thread.sleep((long) (RUNTIME_SECS * 1000));
        pv.stop();
        // Should have about 1 sample per second
        final IDataProvider samples = pv.getSamples();
        System.out.println(samples);
        assertThat(samples.getSize(), closeTo(RUNTIME_SECS, 2.0));
        checkMinMax(samples);
    }

    /** Check if PVItem logs received PV monitors (value updates) */
    @Test
    public void testMonitoredPVItem() throws Exception
    {
        System.out.println("Monitored samples: (" + RUNTIME_SECS + " secs)");
        final Timer unused_timer = null;
        final PVItem pv = new PVItem("sim://sine(0,10,10,1)", 0.0);
        pv.start(unused_timer);
        Thread.sleep((long) (RUNTIME_SECS * 1000));
        pv.stop();
        // Should have about 1 sample per second
        final IDataProvider samples = pv.getSamples();
        System.out.println(samples);
        assertThat(samples.getSize(), closeTo(RUNTIME_SECS, 2.0));
        checkMinMax(samples);
    }

    /** Check if scan period can be changed while running */
    @Test
    public void testScanPeriodChange() throws Exception
    {
        System.out.println("Scan time change: (" + 2 * RUNTIME_SECS + " secs)");
        final Timer scan_timer = new Timer();
        final PVItem pv = new PVItem("sim://sine(0,10,10,1)", 1.0);
        pv.start(scan_timer);
        Thread.sleep((long) (RUNTIME_SECS * 1000));
    
        // Leave PV running. Should have about 1 sample per second
        System.out.println("Samples while scanned at 1 second");
        IDataProvider samples = pv.getSamples();
        System.out.println(samples);
        assertThat(samples.getSize(), closeTo(RUNTIME_SECS, 2.0));
        
        // Change to 2 second scan
        System.out.println("Changing scan to 2 seconds...");
        pv.setScanPeriod(2.0);
        Thread.sleep((long) (RUNTIME_SECS * 1000));

        // Should have about 1 sample per second + 0.5 per second
        samples = pv.getSamples();
        System.out.println(samples);
        assertThat(samples.getSize(), closeTo(RUNTIME_SECS + RUNTIME_SECS/2, 4.0));

        pv.stop();
    }
    
    /** Check if value min..max is correct */
    private void checkMinMax(final IDataProvider samples)
    {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i=0; i<samples.getSize(); ++i)
        {
            final double value = samples.getSample(i).getYValue();
            if (value < min)
                min = value;
            if (value > max)
                max = value;
        }
        assertThat(samples.getYDataMinMax().getLower(), equalTo(min));
        assertThat(samples.getYDataMinMax().getUpper(), equalTo(max));
    }

    /** Check if PVItem correctly handles waveform index */
    @Test
    public void testWaveformIndex() throws Exception
    {
        System.out.println("Scanned waveform samples: (" + RUNTIME_SECS + " secs)");
        
        final PVWriter<Object> writer = PVManager.write(channel("loc://wave")).sync();
        writer.write(new double[] { 1.1, 2.2, 3.3 });
        
        final Timer scan_timer = new Timer();
        final PVItem pv = new PVItem("loc://wave", 1.0);
        pv.setWaveformIndex(1);
        pv.start(scan_timer);
        Thread.sleep((long) (RUNTIME_SECS * 1000));
        pv.stop();
        // Should have about 1 sample per second
        final IDataProvider samples = pv.getSamples();
        System.out.println(samples);
        assertThat(samples.getSize(), closeTo(RUNTIME_SECS, 2.0));
        assertThat(samples.getSize(), greaterThanOrEqualTo(2));
        
        // Check if the samples indicate the second element
        assertThat(samples.getSample(0).getYValue(), equalTo(2.2));
        assertThat(samples.getSample(1).getYValue(), equalTo(2.2));
        assertThat(samples.getYDataMinMax(), equalTo(new Range(2.2, 2.2)));

        // Check if the samples indicate the third element
        pv.setWaveformIndex(2);
        assertThat(samples.getSample(0).getYValue(), equalTo(3.3));
        assertThat(samples.getSample(1).getYValue(), equalTo(3.3));
        assertThat(samples.getYDataMinMax(), equalTo(new Range(3.3, 3.3)));

        // Check if the samples indicate the third element
        pv.setWaveformIndex(4);
        assertThat(samples.getSample(0).getYValue(), equalTo(Double.NaN));
        assertThat(samples.getSample(1).getYValue(), equalTo(Double.NaN));
        assertThat(samples.getYDataMinMax(), is(nullValue()));

        // Check if the samples indicate the first element
        pv.setWaveformIndex(-1);
        assertThat(pv.getWaveformIndex(), equalTo(0));
        assertThat(samples.getSample(0).getYValue(), equalTo(1.1));
        assertThat(samples.getSample(1).getYValue(), equalTo(1.1));
        assertThat(samples.getYDataMinMax(), equalTo(new Range(1.1, 1.1)));
    }
}
