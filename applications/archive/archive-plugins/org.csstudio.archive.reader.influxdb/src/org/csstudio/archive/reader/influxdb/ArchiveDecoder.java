/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.archive.influxdb.InfluxDBUtil;
import org.csstudio.archive.influxdb.MetaTypes.MetaObject;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueDecoder;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueLookup;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;

/** Decode values into VType
 *  @author Megan Grodowitz
 */
public class ArchiveDecoder extends AbstractInfluxDBValueDecoder
{
    /** Status string for <code>Double.NaN</code> samples */
    final private static String NOT_A_NUMBER_STATUS = "NaN";

    protected final AbstractInfluxDBValueLookup vals;

    public ArchiveDecoder(final AbstractInfluxDBValueLookup vals) {
        this.vals = vals;
    }

    public static class Factory extends AbstractInfluxDBValueDecoder.Factory {
        @Override
        public AbstractInfluxDBValueDecoder create(AbstractInfluxDBValueLookup vals) {
            return new ArchiveDecoder(vals);
        }
    }

    /** @param severity Original severity
     *  @param status Status text
     *  @return If the status indicates that there is no actual value,
     *          provide the special 'no value' severity
     * @throws Exception
     */
    protected AlarmSeverity filterSeverity(final String severity_str, final String status) throws Exception
    {
        if (severity_str == null)
        {
            throw new Exception ("Tried to set severity from null string.");
        }

        // Hard-coded knowledge:
        // When the status indicates
        // that the archive is off or channel was disconnected,
        // we use the special severity that marks a sample
        // without a value.
        if (status.equalsIgnoreCase("Archive_Off") ||
                status.equalsIgnoreCase("Disconnected") ||
                status.equalsIgnoreCase("Write_Error"))
            return AlarmSeverity.UNDEFINED;

        try
        {
            return AlarmSeverity.valueOf(severity_str);
        }
        catch (Exception e)
        {
            Activator.getLogger().log(Level.WARNING, "Undefined alarm severity {0}", severity_str);
            return AlarmSeverity.UNDEFINED;
        }
    }

    @Override
    public VType decodeSampleValue() throws Exception
    {
        final MetaObject meta = vals.getMeta();

        final Instant time = InfluxDBUtil.fromInfluxDBTimeFormat(vals.getValue("time"));
        final String status = (String) vals.getValue("status");
        if (status == null)
        {
            throw new Exception ("No status field found when decoding sample");
        }
        final AlarmSeverity severity = filterSeverity((String) vals.getValue("severity"), status);

        switch (meta.storeas)
        {
        case ARCHIVE_DOUBLE:
        case ARCHIVE_DOUBLE_ARRAY:
        {
            return decodeDoubleSamples(time, severity, status, Display.class.cast(meta.object));
        }
        case ARCHIVE_LONG:
        {
            return decodeLongSample(time, severity, status, Display.class.cast(meta.object));
        }
        case ARCHIVE_ENUM:
        {
            return decodeEnumSample(time, severity, status, (List<String>)meta.object);
        }
        case ARCHIVE_STRING:
        case ARCHIVE_UNKNOWN:
        {
        	return decodeStringSample(time, severity, status);
        }
        default:
            throw new Exception ("Tried to encode sample with unhandled store type: " + meta.storeas.name());
        }


    }

    protected final Double fieldToDouble(Object val) throws Exception
    {
        Double dbl;
        try
        {
            dbl = Double.class.cast(val);
        }
        catch (Exception e)
        {
            try
            {
                dbl = Double.valueOf(val.toString());
            }
            catch (Exception e1)
            {
                throw new Exception ("Could not transform object to Double: " + val.getClass().getName());
            }
        }
        return dbl;
    }

    protected final Long fieldToLong(Object val) throws Exception
    {
        Long lval;
        //        try
        //        {
        //            lval = Long.class.cast(val);
        //        }
        //        catch (Exception e)
        //        {
        try
        {
            lval = Double.valueOf(val.toString()).longValue();
        }
        catch (Exception e1)
        {
            throw new Exception ("Could not transform object to Long: " + val.getClass().getName());
        }
        //}
        return lval;
    }

    protected VType decodeStringSample(Instant time, AlarmSeverity severity, String status) throws Exception
    {
        Object val = vals.getValue("string.0");
        if (val == null)
        {
            throw new Exception ("Did not find string.0 field where expected");
        }
        return new ArchiveVString(time, severity, status, val.toString());
	}

    protected VType decodeEnumSample(final Instant time, final AlarmSeverity severity, final String status, List<String> labels) throws Exception
    {
        Object val = vals.getValue("long.0");
        if (val == null)
        {
            throw new Exception ("Did not find long.0 field where expected");
        }
        return new ArchiveVEnum(time, severity, status, labels, fieldToLong(val).intValue());
    }

    protected VType decodeLongSample(final Instant time, final AlarmSeverity severity, final String status, Display display) throws Exception
    {
        Object val = vals.getValue("long.0");
        if (val == null)
        {
            Activator.getLogger().log(Level.SEVERE, this.toString());
            throw new Exception ("Did not find long.0 field where expected");
        }
        return new ArchiveVNumber(time, severity, status, display, fieldToLong(val));
    }

    protected VType decodeDoubleSamples(final Instant time, final AlarmSeverity severity, final String status, Display display) throws Exception
    {
        Object val = vals.getValue("double.0");
        if (val == null)
        {
            throw new Exception ("Did not find double.0 field where expected");
        }

        List<Double> data = new ArrayList<Double>();
        if (status.equals(NOT_A_NUMBER_STATUS))
            data.add(Double.NaN);
        else
            data.add(fieldToDouble(val));

        int len = 1;
        while (val != null)
        {
            String fname = "double." + Integer.toString(len);
            if (vals.hasValue(fname))
                val = vals.getValue(fname);
            else
                val = null;
            if (val != null) {
                data.add(fieldToDouble(val));
                len++;
            }
        }

        if (data.size() == 1)
            return new ArchiveVNumber(time, severity, status, display, data.get(0));
        else
        {
            final double dd[] = new double[data.size()];
            return new ArchiveVNumberArray(time, severity, status, display, dd);
        }
    }


}
