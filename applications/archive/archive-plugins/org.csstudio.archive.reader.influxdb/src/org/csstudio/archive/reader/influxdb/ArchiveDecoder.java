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

    //         When using continuous queries to resample archived data, the column
    // names should be in one of two categories: either the default (raw) type
    // (e.g. "double.0", "string.0"), or the  prefixed (resampled) type
    // ("average_double.0", "average_string.0"). Results with default-type
    // samples should be given preference over prefix-type results. Once
    // the default type is found, prefix-type samples are ignored.
    /**
     * Whether or not to skip samples with prefix-type column names
     * (e.g. average_double.0 instead of double.0)
     */
    protected boolean ignore_prefix_samples = false;
    //        Indicates that the sample currently in vals should be ignored.
    private static final Object IGNORE_SAMPLE = new Object();

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
        //    In order to support using continuous queries to downsample data
        //(for example, keep raw data for 1 month, and daily averages forever),
        //we can no longer enforce non-null "status" and "severity" values.
        String status = vals.hasValue("status") ? (String) vals.getValue("status") : "";
        if (status == null)
        {
            status = "";
        }
        String severity_string = vals.hasValue("severity") ? (String) vals.getValue("severity") : "NONE";
        final AlarmSeverity severity = filterSeverity(severity_string != null ? severity_string : "NONE", status);

        switch (meta.storeas)
        {
        case ARCHIVE_DOUBLE:
        case ARCHIVE_DOUBLE_ARRAY:
        {
            final Display display = Display.class.cast(meta.object);
            return decodeDoubleSamples(time, severity, status, display, "average_");
        }
        case ARCHIVE_LONG:
        {
            final Display display = Display.class.cast(meta.object);
            return decodeLongSample(time, severity, status != null ? status : "", display, "average_");
        }
        case ARCHIVE_ENUM:
        {
            final List<String> labels = (List<String>)meta.object;
            return decodeEnumSample(time, severity, status != null ? status : "", labels, "average_");
        }
        case ARCHIVE_STRING:
        case ARCHIVE_UNKNOWN:
        {
            return decodeStringSample(time, severity, status != null ? status : "", "average_");
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

    protected VType decodeStringSample(Instant time, AlarmSeverity severity, String status, String prefix) throws Exception
    {
        //    In order to support using continuous queries to downsample/decimate data over time
        //(e.g., drop data after 1 month, but keep a daily average forever), we have to consider
        //the possibility that there may be no "string.0" field in the sample. That is, it might
        //not be exceptional if the value for "string.0" or "average_string.0" is null or if there
        //simply is no value. This might happen because the continuous query ignores fields with
        //a string datatype, or maybe because metadata and sample timestamps are mis-aligned due
        //to a group by time() clause, as with the ArchiveStatisticsDecoder.
        Object val = getSingleValue("string.0", prefix);
        if (val == null)
        {
            //    Activator.getLogger().log(Level.INFO, "Value with string-like datatype missing '"+fieldname+
            //            "' field.");
            val = "";
        }
        else if (val == IGNORE_SAMPLE)
            return null;
        return new ArchiveVString(time, severity, status, val.toString());
    }

    protected VType decodeEnumSample(final Instant time, final AlarmSeverity severity, final String status, List<String> labels, String prefix) throws Exception
    {
        final Object val = getSingleValue("long.0", prefix);
        if (val == null)
        {
            Activator.getLogger().log(Level.SEVERE, this.toString());
            throw new Exception ("Did not find long.0 or "+prefix+"long.0 field where expected");
        }
        else if (val == IGNORE_SAMPLE)
            return null;
        return new ArchiveVEnum(time, severity, status, labels, fieldToLong(val).intValue());
    }

    protected VType decodeLongSample(final Instant time, final AlarmSeverity severity, final String status, Display display, String prefix) throws Exception
    {
        final Object val = getSingleValue("long.0", prefix);
        if (val == null)
        {
            Activator.getLogger().log(Level.SEVERE, this.toString());
            throw new Exception ("Did not find long.0 or "+prefix+"long.0 field where expected");
        }
        else if (val == IGNORE_SAMPLE)
            return null;
        return new ArchiveVNumber(time, severity, status, display, fieldToLong(val));
    }

    private Object getSingleValue(final String colname, final String prefix) throws Exception
    {    // First, try to get value from "plain" field key
        Object val;
        if ( !vals.hasValue(colname) || (val = vals.getValue(colname)) == null )
            //Failed ==> This is a prefix-type sample
            if (ignore_prefix_samples)
            {
                //Check: Is valid prefix-type sample?
                if (!vals.hasValue(prefix + colname) || vals.getValue(prefix + colname) == null)
                    return null;
                // Ignore
                return IGNORE_SAMPLE;
            }
            else
                // Try to get value from prefix-type field key
                val = vals.hasValue(prefix + colname) ? vals.getValue(prefix + colname) : null;
        else
            //Succeeded ==> ignore prefix-type samples
            ignore_prefix_samples = true;
        return val;
    }

    protected VType decodeDoubleSamples(final Instant time, final AlarmSeverity severity, final String status, Display display, String prefix) throws Exception
    {    // First, try to get value from "plain" field key
        String part_name = "double."; //partial column name
        Object val = vals.hasValue(part_name + "0") ? vals.getValue(part_name + "0") : null;
        if (val == null)
        {    //Failed ==> This is a prefix-type sample
            part_name = prefix + part_name;
            if (ignore_prefix_samples)
            {
                // Check: Is valid prefix-type sample?
                if (!vals.hasValue(part_name+"0") || vals.getValue(part_name+"0") == null)
                    throw new Exception ("Did not find double.0 or "+part_name+"0 field where expected");
                // Ignore
                return null;
            }
            else
            {
                val = vals.getValue(part_name + "0");
                if (val == null)
                    throw new Exception ("Did not find double.0 or "+part_name+"0 field where expected");
            }
        }
        else
            //Succeeded ==> ignore prefix-type samples
            ignore_prefix_samples = true;

        List<Double> data = new ArrayList<Double>();
        if (status.equals(NOT_A_NUMBER_STATUS))
            data.add(Double.NaN);
        else
            data.add(fieldToDouble(val));

        int len = 1;
        while (val != null)
        {
            String fname = part_name + Integer.toString(len);
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
