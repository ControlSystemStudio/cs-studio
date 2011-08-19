/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.junit.Test;

/** JUnit test for PVSamples
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVSamplesUnitTest
{
    @Test
    public void testPVSamples() throws OsgiServiceUnavailableException, ArchiveServiceException
    {
        // Start w/ empty PVSamples
        final PVSamples samples = new PVSamples(null);
        assertEquals(0, samples.getSize());
        assertNull(samples.getXDataMinMax());
        assertNull(samples.getYDataMinMax());

        // Add 'historic' samples
        final ArrayList<IValue> history = new ArrayList<IValue>();
        for (int i=0; i<10; ++i)
            history.add(TestSampleBuilder.makeValue(i));
        samples.mergeArchivedData("TestChannel", "Test", history);
        // PVSamples include continuation until 'now'
        System.out.println(samples.toString());
        assertEquals(history.size()+1, samples.getSize());

        // Add 2 'live' samples
        samples.addLiveSample(TestSampleBuilder.makeValue(samples.getSize()));
        samples.addLiveSample(TestSampleBuilder.makeValue(samples.getSize()));
        // PVSamples include history, live, continuation until 'now'
        System.out.println(samples.toString());
        assertEquals(history.size()+3, samples.getSize());

        // Add a non-numeric sample
        samples.addLiveSample(TestSampleBuilder.makeError(samples.getSize(), "Disconnected"));
        // PVSamples include history, live, NO continuation
        System.out.println(samples.toString());
        assertEquals(history.size()+3, samples.getSize());

        // Check if the history.setBorderTime() update works
        // Create 'history' data from 0 to 20.
        history.clear();
        for (int i=0; i<21; ++i)
            history.add(TestSampleBuilder.makeValue(i));
        samples.mergeArchivedData("TestChannel", "Test", history);

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
        final PVSamples samples = new PVSamples(null);
        assertEquals(0, samples.getSize());

        // Add sample w/ null time stamp, INVALID/UDF
        final ITimestamp null_time = TimestampFactory.createTimestamp(0, 0);
        IValue value = ValueFactory.createDoubleValue(null_time,
                    ValueFactory.createInvalidSeverity(),
                    "UDF",
                    null, IValue.Quality.Original, new double[] { 0 });
        samples.addLiveSample(value);
        System.out.println("Original: " + value);

        // Should have that sample, plus copy that's extrapolated to 'now'
        assertEquals(2, samples.getSize());

        value = samples.getSample(0).getValue();
        System.out.println("Sampled : " + value);
        final ITimestamp patched_time = value.getTime();
        assertTrue(patched_time.isValid());
    }
}
