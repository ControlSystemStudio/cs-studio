/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.archive.writer.influxdb;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.influxdb.MetaTypes.StoreAs;
import org.csstudio.archive.vtype.VTypeHelper;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.influxdb.dto.Point;

/** Encode VType values into InfluxDB sample points
 *  @author Megan Grodowitz
 */
public class InfluxDBSampleEncoder
{

    final private static int MAX_TEXT_SAMPLE_LENGTH = Preferences.getMaxStringSampleLength();

    /** Status string for <code>Double.NaN</code> samples */
    final private static String NOT_A_NUMBER_STATUS = "NaN";


    /** Perform 'batched' insert for sample.
     *  <p>Needs eventual flush()
     *  @param channel Channel
     *  @param sample Sample to insert
     *  @throws Exception on error
     */
    public static Point encodeSample(final InfluxDBWriteChannel channel, final Instant stamp, final VType sample, final StoreAs storeas) throws Exception
    {
        final String severity = VTypeHelper.getSeverity(sample).toString();
        final String status = VTypeHelper.getMessage(sample);

        switch (storeas)
        {
        case ARCHIVE_DOUBLE :
        {
            final Number number = ((VNumber)sample).getValue();
            return encodeDoubleSamples(channel, stamp, severity, status, number.doubleValue(), null);
        }
        case ARCHIVE_DOUBLE_ARRAY:
        {
            final ListNumber data = ((VNumberArray)sample).getData();
            return encodeDoubleSamples(channel, stamp, severity, status, data.getDouble(0), data);
        }
        case ARCHIVE_LONG:
        {
            final Number number = ((VNumber)sample).getValue();
            return encodeLongSample(channel, stamp, severity, status, number.longValue());

        }
        case ARCHIVE_ENUM:
        {
            return encodeLongSample(channel, stamp, severity, status, ((VEnum)sample).getIndex());
        }
        case ARCHIVE_STRING:
        {
            return encodeTextSample(channel, stamp, severity, status, ((VString)sample).getValue());
        }
        case ARCHIVE_UNKNOWN:
        {
            return encodeTextSample(channel, stamp, severity, status, sample.toString());
        }
        default:
            throw new Exception ("Tried to encode sample with unhandled store type: " + storeas.name());
        }
    }

    /** Encode Double or array of doubles into database sample point
     * @param channel Information about the channel being written
     * @param stamp sample timestamp
     * @param severity alarm severity
     * @param status status string
     * @param dbl first value
     * @param additional list of values if this is an array (null if single number)
     * @return
     * @return
     * @throws Exception
     */
    private static Point encodeDoubleSamples(final InfluxDBWriteChannel channel,
            final Instant stamp, final String severity,
            final String status, final double dbl, final ListNumber additional) throws Exception
    {
        org.influxdb.dto.Point.Builder point;

        //TODO: Catch other number states than NaN (e.g. INF)? Add tags instead of status string?
        if (Double.isNaN(dbl))
        {
            //TODO: nano precision may be lost Long time field (library limitation)
            point = Point.measurement(channel.getName())
                    .time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                    .tag("severity", AlarmSeverity.UNDEFINED.name())
                    .tag("status", NOT_A_NUMBER_STATUS)
                    .addField("double.0", 0.0d);
        }
        else
        {
            point = Point.measurement(channel.getName())
                    .time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                    .tag("severity", severity)
                    .tag("status", status)
                    .addField("double.0", dbl);
        }

        if (additional != null)
        {
            //handle arrays (Recommended way is lots of fields)
            final int N = additional.size();
            for (int i = 1; i < N; i++)
            {
                String fname = "double." + Integer.toString(i);
                // Patch NaN.
                // Conundrum: Should we set the status/severity to indicate NaN?
                final double dbli = additional.getDouble(i);
                if (Double.isNaN(dbli))
                    point.addField(fname, 0.0);
                else
                    point.addField(fname, dbli);
            }
        }

        return point.build();
    }

    /** Encode a long value into database sample point  */
    private static Point encodeLongSample(final InfluxDBWriteChannel channel,
            final Instant stamp, final String severity,
            final String status, final long num) throws Exception
    {
        Point point = Point.measurement(channel.getName())
                .time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                .tag("severity", severity)
                .tag("status", status)
                .addField("long.0", num).
                build();

        return point;
    }

    /** Encode a string value into database sample point */
    private static Point encodeTextSample(final InfluxDBWriteChannel channel,
            final Instant stamp, final String severity,
            final String status, String txt) throws Exception
    {
        if (txt.length() > MAX_TEXT_SAMPLE_LENGTH)
        {
            Activator.getLogger().log(Level.INFO,
                    "Value of {0} exceeds {1} chars: {2}",
                    new Object[] { channel.getName(), MAX_TEXT_SAMPLE_LENGTH, txt });
            txt = txt.substring(0, MAX_TEXT_SAMPLE_LENGTH);
        }
        Point point = Point.measurement(channel.getName())
                .time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                .tag("severity", severity)
                .tag("status", status)
                .addField("string.0", txt).
                build();

        return point;
    }

}
