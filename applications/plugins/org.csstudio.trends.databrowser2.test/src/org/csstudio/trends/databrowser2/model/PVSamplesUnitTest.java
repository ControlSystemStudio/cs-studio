/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.epics.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;
import org.junit.Test;

/** JUnit test for PVSamples
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto added test case for waveform index
 */
@SuppressWarnings("nls")
public class PVSamplesUnitTest
{
    @Test
    public void testPVSamples()
    {
        // Start w/ empty PVSamples
        final PVSamples samples = new PVSamples();
        assertEquals(0, samples.getSize());
        assertNull(samples.getXDataMinMax());
        assertNull(samples.getYDataMinMax());

        // Add 'historic' samples
        final List<VType> history = new ArrayList<VType>();
        for (int i=0; i<10; ++i)
            history.add(TestHelper.makeValue(i));
        samples.mergeArchivedData("Test", history);
        // PVSamples include continuation until 'now'
        System.out.println(samples.toString());
        assertEquals(history.size()+1, samples.getSize());

        // Add 2 'live' samples
        samples.addLiveSample(TestHelper.makeValue(samples.getSize()));
        samples.addLiveSample(TestHelper.makeValue(samples.getSize()));
        // PVSamples include history, live, continuation until 'now'
        System.out.println(samples.toString());
        assertEquals(history.size()+3, samples.getSize());

        // Add a non-numeric sample
        samples.addLiveSample(TestHelper.makeError(samples.getSize(), "Disconnected"));
        // PVSamples include history, live, NO continuation
        System.out.println(samples.toString());
        assertEquals(history.size()+3, samples.getSize());

        // Check if the history.setBorderTime() update works
        // Create 'history' data from 0 to 20.
        history.clear();
        for (int i=0; i<21; ++i)
            history.add(TestHelper.makeValue(i));
        samples.mergeArchivedData("Test", history);

        // Since 'live' data starts at 11, history is only visible up to there,
        // i.e. 0..10 = 11 in history plus 3 'live' samples
        assertEquals(11 + 3, samples.getSize());
        System.out.println(samples.toString());
    }

    /** When 'monitoring' a PV, IOCs will send data with zero time stamps
     *  for records that have never been processed.
     *  Check that time stamps are patched to host time.
     */
    @Test
    public void testUndefinedLiveData()
    {
        // Start w/ empty samples
        final PVSamples samples = new PVSamples();
        assertEquals(0, samples.getSize());

        // Add sample w/ null time stamp, INVALID/UDF
        final Timestamp null_time = Timestamp.of(0, 0);
        VType value = new ArchiveVNumber(null_time, AlarmSeverity.NONE, "", null, 0.0);
        assertThat(ValueUtil.timeOf(value).isTimeValid(), equalTo(false));
        
        samples.addLiveSample(value);
        System.out.println("Original: " + value);

        // Should have that sample, plus copy that's extrapolated to 'now'
        assertEquals(2, samples.getSize());

        value = samples.getSample(0).getValue();
        System.out.println("Sampled : " + value);
        assertThat(ValueUtil.timeOf(value).isTimeValid(), equalTo(true));
    }
    
    @Test
    public void testWaveformIndex()
    {
        // Start w/ empty PVSamples
        final PVSamples samples = new PVSamples();
        assertEquals(0, samples.getSize());
        assertNull(samples.getXDataMinMax());
        assertNull(samples.getYDataMinMax());

        // Add 'historic' samples
        final List<VType> history = new ArrayList<VType>();
        history.add(TestHelper.makeWaveform(0, new double[] {0.0, 0.1, 0.2}));
        history.add(TestHelper.makeWaveform(1, new double[] {1.0, 1.1, 1.2, 1.3}));
        samples.mergeArchivedData("Test", history);
        System.out.println(samples.toString());
        
        // PVSamples include continuation until 'now'
        System.out.println(samples.toString());
        assertEquals(0.0, samples.getSample(0).getYValue(), 0.000001);
        assertEquals(1.0, samples.getSample(1).getYValue(), 0.000001);
        assertEquals(new Range(0.0, 1.0), samples.getYDataMinMax());
        
        // Change the waveform index to 1
        samples.setWaveformIndex(1);
        assertEquals(0.1, samples.getSample(0).getYValue(), 0.000001);
        assertEquals(1.1, samples.getSample(1).getYValue(), 0.000001);
        assertEquals(new Range(0.1, 1.1), samples.getYDataMinMax());
        
        // Change the waveform index to 2
        samples.setWaveformIndex(2);
        assertEquals(0.2, samples.getSample(0).getYValue(), 0.000001);
        assertEquals(1.2, samples.getSample(1).getYValue(), 0.000001);
        assertEquals(new Range(0.2, 1.2), samples.getYDataMinMax());

        // Add more 'historic' samples with non-zero waveform index
        final List<VType> history2 = new ArrayList<VType>();
        history2.add(TestHelper.makeWaveform(2, new double[] {2.0, 2.1, 2.2}));
        history2.add(TestHelper.makeWaveform(3, new double[] {3.0, 3.1, 3.2, 3.3}));
        samples.mergeArchivedData("Test2", history2);
        System.out.println(samples.toString());
        
        // Check if Y values indicate the third element
        assertEquals(2.2, samples.getSample(2).getYValue(), 0.000001);
        assertEquals(3.2, samples.getSample(3).getYValue(), 0.000001);
        assertEquals(new Range(0.2, 3.2), samples.getYDataMinMax());
        
        // Add 2 'live' samples
        samples.addLiveSample(TestHelper.makeWaveform(4, new double[] {4.0, 4.1, 4.2}));
        samples.addLiveSample(TestHelper.makeWaveform(5, new double[] {5.0, 5.1, 5.2, 5.3}));
        System.out.println(samples.toString());
        
        // Check if Y values indicate the third element
        assertEquals(4.2, samples.getSample(4).getYValue(), 0.000001);
        assertEquals(5.2, samples.getSample(5).getYValue(), 0.000001);
        assertEquals(new Range(0.2, 5.2), samples.getYDataMinMax());
        
        // Change all waveform index at once
        samples.setWaveformIndex(1);
        assertEquals(0.1, samples.getSample(0).getYValue(), 0.000001);
        assertEquals(1.1, samples.getSample(1).getYValue(), 0.000001);
        assertEquals(2.1, samples.getSample(2).getYValue(), 0.000001);
        assertEquals(3.1, samples.getSample(3).getYValue(), 0.000001);
        assertEquals(4.1, samples.getSample(4).getYValue(), 0.000001);
        assertEquals(5.1, samples.getSample(5).getYValue(), 0.000001);
        assertEquals(new Range(0.1, 5.1), samples.getYDataMinMax());

        // Check if Y values indicate NaN when waveform index is out of range
        samples.setWaveformIndex(3);
        assertEquals(Double.NaN, samples.getSample(0).getYValue(), 0.000001);
        assertEquals(1.3, samples.getSample(1).getYValue(), 0.000001);
        assertEquals(Double.NaN, samples.getSample(2).getYValue(), 0.000001);
        assertEquals(3.3, samples.getSample(3).getYValue(), 0.000001);
        assertEquals(Double.NaN, samples.getSample(4).getYValue(), 0.000001);
        assertEquals(5.3, samples.getSample(5).getYValue(), 0.000001);
        assertEquals(new Range(1.3, 5.3), samples.getYDataMinMax());

        samples.setWaveformIndex(4);
        assertEquals(Double.NaN, samples.getSample(0).getYValue(), 0.000001);
        assertEquals(Double.NaN, samples.getSample(1).getYValue(), 0.000001);
        assertEquals(Double.NaN, samples.getSample(2).getYValue(), 0.000001);
        assertEquals(Double.NaN, samples.getSample(3).getYValue(), 0.000001);
        assertEquals(Double.NaN, samples.getSample(4).getYValue(), 0.000001);
        assertEquals(Double.NaN, samples.getSample(5).getYValue(), 0.000001);
        assertNull(samples.getYDataMinMax());
    }
}
