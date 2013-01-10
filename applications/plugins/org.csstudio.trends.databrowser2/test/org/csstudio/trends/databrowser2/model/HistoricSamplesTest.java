/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.linearscale.Range;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;
import org.junit.Test;

/** JUnit test of HistoricSamples
 *  @author Kay Kasemir
 *
 *  FIXME (kasemir) : remove sysos, use assertions
 */
@SuppressWarnings("nls")
public class HistoricSamplesTest
{
    @Test
    public void addArchivedData()
    {
        final HistoricSamples history = new HistoricSamples();
        final int N = 10;
        // Initial data, time 10..19
        final List<VType> samples = new ArrayList<VType>();
        for (int i=0; i<N; ++i) {
            samples.add(TestHelper.makeValue(N+i));
        }

        final String source = "Test";
        history.mergeArchivedData(source, samples);
        //System.out.println(history);

        assertEquals(N, history.getSize());
        assertEquals(samples.get(0), history.getSample(0).getValue());
        assertEquals(samples.get(N-1), history.getSample(N-1).getValue());

        Range range = history.getYDataMinMax();
        //System.out.println(range);
        assertEquals(new Range(10, 19), range);

        // Check subset
        history.setBorderTime(Timestamp.of(12, 0));
        assertEquals(2, history.getSize());
        assertEquals(new Range(10, 11), history.getYDataMinMax());

        history.setBorderTime(null);
        assertEquals(N, history.getSize());
        assertEquals(new Range(10, 19), history.getYDataMinMax());

        // Pre-pend time 0..9
        for (int i=0; i<N; ++i) {
            samples.set(i, TestHelper.makeValue(i));
        }
        history.mergeArchivedData(source, samples);
        assertEquals(2*N, history.getSize());
        //System.out.println(history);
        range = history.getYDataMinMax();
        //System.out.println(range);
        assertEquals(new Range(0, 19), range);

        history.setBorderTime(Timestamp.of(12, 0));
        assertEquals(12, history.getSize());

        history.setBorderTime(Timestamp.of(100, 0));
        assertEquals(20, history.getSize());

        history.setBorderTime(Timestamp.of(0, 0));
        assertEquals(0, history.getSize());

        history.setBorderTime(null);
        assertEquals(2*N, history.getSize());
    }
}
