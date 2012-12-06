/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.data.ValueFactory;
import org.epics.util.time.Timestamp;

/** Unit-test helper for creating samples
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto added makeWaveform() method
 */
@SuppressWarnings("nls")
public class TestSampleBuilder
{
    /** @param i Numeric value as well as pseudo-timestamp
     *  @return Sample that has value and time based on input parameter
     */
    public static VType makeValue(final int i)
    {
        return ValueFactory.newVDouble(Double.valueOf(i), ValueFactory.newTime(Timestamp.of(i, 0)));
    }
    
    /**@param ts timestamp
     * @param vals array
     * @return Sample that has waveform and time based on input parameter
     */
    public static VType makeWaveform(final int ts, final double array[])
    {
        return ValueFactory.newVDoubleArray(array,
                ValueFactory.alarmNone(),
                ValueFactory.timeNow(),
                ValueFactory.displayNone());
    }

    /** @param i Pseudo-timestamp
     *  @return Sample that has error text with time based on input parameter
     */
    public static VType makeError(final int i, final String error)
    {
        return ValueFactory.newVDouble(Double.NaN,
                ValueFactory.newAlarm(AlarmSeverity.UNDEFINED, error),
                ValueFactory.newTime(Timestamp.of(i, 0)),
                ValueFactory.displayNone());
    }


    /** @param i Numeric value as well as pseudo-timestamp
     *  @return IValue sample that has value and time based on input parameter
     */
    public static PlotSample makePlotSample(int i)
    {
        return new PlotSample("Test", makeValue(i));
    }

    /** Create array of samples
     *  @param start First value/time stamp
     *  @param end   Last value/time stamp (exclusive)
     */
    public static PlotSample[] makePlotSamples(final int start, final int end)
    {
        int N = end - start;
        final PlotSample result[] = new PlotSample[N];
        for (int i=0; i<N; ++i)
            result[i] = makePlotSample(start + i);
        return result;
    }
}
