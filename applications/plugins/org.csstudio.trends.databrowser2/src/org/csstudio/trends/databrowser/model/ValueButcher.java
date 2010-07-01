/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IEnumeratedMetaData;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.ILongValue;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.data.IValue.Quality;

/** Helper for transforming samples/values
 *  @author Kay Kasemir
 */
public class ValueButcher
{
    /** Create new value with specific time stamp
     *  @param value Original Value
     *  @param time Desired time stamp
     *  @return New value with given time stamp
     */
    public static IValue changeTimestamp(final IValue value,
            final ITimestamp time)
    {
        final ISeverity severity = value.getSeverity();
        final String status = value.getStatus();
        final Quality quality = value.getQuality();
        final IMetaData meta = value.getMetaData();
        if (value instanceof IDoubleValue)
            return ValueFactory.createDoubleValue(time , severity, status,
                            (INumericMetaData)meta, quality,
                            ((IDoubleValue)value).getValues());
        else if (value instanceof ILongValue)
            return ValueFactory.createLongValue(time, severity, status,
                            (INumericMetaData)meta, quality,
                            ((ILongValue)value).getValues());
        else if (value instanceof IEnumeratedValue)
            return ValueFactory.createEnumeratedValue(time, severity, status,
                            (IEnumeratedMetaData)meta, quality,
                            ((IEnumeratedValue)value).getValues());
        else if (value instanceof IStringValue)
            return ValueFactory.createStringValue(time, severity, status,
                            quality, ((IStringValue)value).getValues());
        // Else: Log unknown data type as text
        return ValueFactory.createStringValue(time, severity, status,
                quality, new String[] { value.toString() });
    }
    
    /** Create new value with 'now' as time stamp
     *  @param value Original Value
     *  @return New value with 'now' as time stamp
     */
    public static IValue changeTimestampToNow(final IValue value)
    {
        return changeTimestamp(value, TimestampFactory.now());
    }

    /** Create new sample with 'now' as time stamp
     *  @param value Original sample
     *  @return New sample with 'now' as time stamp
     */
    public static PlotSample changeTimestampToNow(final PlotSample sample)
    {
        return new PlotSample(sample.getSource(), changeTimestampToNow(sample.getValue()));
    }
}
