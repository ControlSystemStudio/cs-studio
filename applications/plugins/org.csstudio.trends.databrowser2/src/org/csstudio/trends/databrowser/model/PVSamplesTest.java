/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.csstudio.platform.data.IValue;
import org.junit.Test;

/** JUnit test for PVSamples
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVSamplesTest
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
        final ArrayList<IValue> history = new ArrayList<IValue>();
        for (int i=0; i<10; ++i)
            history.add(TestSampleBuilder.makeValue(i));
        samples.mergeArchivedData("Test", history);
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
        samples.mergeArchivedData("Test", history);
        
        // Since 'live' data starts at 11, history is only visible up to there,
        // i.e. 0..10 = 11 in history plus 3 'live' samples
        assertEquals(11 + 3, samples.getSize());
        System.out.println(samples.toString());
    }
}
