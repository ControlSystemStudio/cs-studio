/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.archive.influxdb;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVNumber;
import org.csstudio.archive.vtype.ArchiveVNumberArray;
import org.csstudio.archive.vtype.ArchiveVString;
import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.Display;
import org.diirt.vtype.VDouble;
import org.diirt.vtype.VEnum;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VString;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;

public class MetaTypes
{
    // WARNING: DO NOT CHANGE THESE ENUM NAMES
    // Names are used as DB meta data value strings
    // It is fine to add more as long as the old ones still work
    public enum StoreAs {
        ARCHIVE_UNKNOWN (ArchiveVString.class),
        ARCHIVE_STRING (ArchiveVString.class),
        ARCHIVE_ENUM (ArchiveVEnum.class),
        ARCHIVE_DOUBLE (ArchiveVNumber.class),
        ARCHIVE_LONG (ArchiveVNumber.class),
        ARCHIVE_DOUBLE_ARRAY (ArchiveVNumberArray.class),
        ARCHIVE_LONG_ARRAY (ArchiveVNumberArray.class);

        final public Class<?> objclass;

        StoreAs(Class<?> objclass)
        {
            this.objclass = objclass;
        }
    }

    public static class MetaObject {
        public final StoreAs storeas;
        public final Object object;
        public final Instant timestamp;

        MetaObject(Object object, StoreAs storeas, Instant timestamp)
        {
            this.object = object;
            this.storeas = storeas;
            this.timestamp = timestamp;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("MetaObject[").append(storeas.name())
            .append('@').append(timestamp).append(':');
            if (object == null)
                sb.append("null]");
            else
                sb.append(object.getClass().getName()).append(']');
            return sb.toString();
        }
    }

    public static StoreAs writeVtypeAs(VType sample)
    {
        // Start with most likely cases and highest precision: Double, ...
        // Then going down in precision to integers, finally strings...
        if (sample instanceof VDouble)
            return StoreAs.ARCHIVE_DOUBLE;
        else if (sample instanceof VNumber)
        {
            final Number number = ((VNumber)sample).getValue();
            if (number instanceof Double)
                return StoreAs.ARCHIVE_DOUBLE;
            else
                return StoreAs.ARCHIVE_LONG;
        }
        else if (sample instanceof VNumberArray)
        {
            //TODO: Detect arrays of long?
            return StoreAs.ARCHIVE_DOUBLE_ARRAY;
        }
        else if (sample instanceof VEnum)
            return StoreAs.ARCHIVE_ENUM;
        else if (sample instanceof VString)
            return StoreAs.ARCHIVE_STRING;

        return StoreAs.ARCHIVE_UNKNOWN;
    }

    public static StoreAs storedTypeIs(final String str)
    {
        try
        {
            return StoreAs.valueOf(str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static Object checkAssign (final Map<String, Object> map, final String key, Class<?> dest, final Object dflt)
    {
        Object src = map.get(key);

        if (src == null) {
            Activator.getLogger().log(Level.WARNING, "No object stored in metadata for key " + key);
            return dest.cast(dflt);
        }

        try
        {
            return dest.cast(src);
        }
        catch (Exception e)
        {
            Activator.getLogger().log(Level.WARNING, "Got object type " + src.getClass().getName() + " could not be cast to " + dest.getName());
            return dest.cast(dflt);
        }
    }

    public static class cleanDisplayMeta {
        public final double low_disp_rng, high_disp_rng, low_warn_lmt, high_warn_lmt;
        public final double low_alarm_lmt, high_alarm_lmt, low_ctrl_lmt, high_ctrl_lmt;
        public final int precision;
        public final String units;

        private double numericDouble(Double val) {
            if (Double.isFinite(val))
                return val.doubleValue();
            return 0;
        }

        public cleanDisplayMeta(Display meta) {
            // TODO: do not add fields for non-numeric
            low_disp_rng = numericDouble(meta.getLowerDisplayLimit());
            high_disp_rng = numericDouble(meta.getUpperDisplayLimit());
            low_warn_lmt = numericDouble(meta.getLowerWarningLimit());
            high_warn_lmt = numericDouble(meta.getUpperWarningLimit());
            low_alarm_lmt = numericDouble(meta.getLowerAlarmLimit());
            high_alarm_lmt = numericDouble(meta.getUpperAlarmLimit());
            low_ctrl_lmt = numericDouble(meta.getLowerCtrlLimit());
            high_ctrl_lmt = numericDouble(meta.getUpperCtrlLimit());

            String munits = meta.getUnits();
            if (munits == null || munits.length() < 1)
                units = " "; //$NON-NLS-1$
            else
                units = munits.replaceAll("^\"+|\"+$", "");

            final NumberFormat format = meta.getFormat();
            if (format != null)
                precision = format.getMinimumFractionDigits();
            else
                precision = 0;
        }

    }

    public static Point toDisplayMetaPoint(final Display disp, final String channel_name,
            final Instant stamp, final StoreAs storeas)
    {

        final cleanDisplayMeta meta = new cleanDisplayMeta(disp);

        return Point.measurement(channel_name)
                .time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                .tag("datatype", storeas.name())
                .addField("low_disp_rng", meta.low_disp_rng).addField("high_disp_rng", meta.high_disp_rng)
                .addField("low_warn_lmt", meta.low_warn_lmt).addField("high_warn_lmt", meta.high_warn_lmt)
                .addField("low_alarm_lmt", meta.low_alarm_lmt).addField("high_alarm_lmt", meta.high_alarm_lmt)
                .addField("low_ctrl_lmt", meta.low_ctrl_lmt).addField("high_ctrl_lmt", meta.high_ctrl_lmt)
                .addField("precision", meta.precision).addField("units", meta.units)
                .build();
    }

    private static Display mapToDisplay(final Map<String, Object> map) throws Exception
    {
        Double lowerDisplayLimit, lowerAlarmLimit, lowerWarningLimit;
        String units;
        Integer precision;
        Double upperWarningLimit, upperAlarmLimit, upperDisplayLimit;
        Double lowerCtrlLimit, upperCtrlLimit;

        lowerDisplayLimit = (Double) checkAssign(map, "low_disp_rng", Double.class, 0.0);
        upperDisplayLimit = (Double) checkAssign(map, "high_disp_rng", Double.class, 0.0);

        lowerAlarmLimit = (Double) checkAssign(map, "low_alarm_lmt", Double.class, 0.0);
        upperAlarmLimit = (Double) checkAssign(map, "high_alarm_lmt", Double.class, 0.0);

        lowerWarningLimit = (Double) checkAssign(map, "low_warn_lmt", Double.class, 0.0);
        upperWarningLimit = (Double) checkAssign(map, "high_warn_lmt", Double.class, 0.0);

        lowerCtrlLimit = (Double) checkAssign(map, "low_ctrl_lmt", Double.class, 0.0);
        upperCtrlLimit = (Double) checkAssign(map, "high_ctrl_lmt", Double.class, 0.0);

        units = (String) checkAssign(map, "units", String.class, "");
        precision = ((Double) checkAssign(map, "precision", Double.class, 1)).intValue();

        final Display display = ValueFactory.newDisplay(
                lowerDisplayLimit, lowerAlarmLimit, lowerWarningLimit,
                units, NumberFormats.format(precision),
                upperWarningLimit, upperAlarmLimit, upperDisplayLimit,
                lowerCtrlLimit, upperCtrlLimit);

        return display;
    }

    public static Point toEnumMetaPoint(final List<String> enum_states, final String channel_name,
            final Instant stamp, final StoreAs storeas)
    {
        org.influxdb.dto.Point.Builder point;

        point = Point.measurement(channel_name)
                .time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                .tag("datatype", storeas.name());

        //handle arrays (Recommended way is lots of fields)
        final int N = enum_states.size();
        if (N == 0)
        {
            point.addField("null_metadata", true);
        }
        else
        {
            for (int i = 0; i < N; i++)
            {
                String fname = "state." + Integer.toString(i);
                point.addField(fname, enum_states.get(i));
            }
        }

        return point.build();
    }

    private static List<String> mapToEnumList(Map<String, Object> map )
    {
        int i = 0;
        String fname = "state." + Integer.toString(i);
        Object obj = map.get(fname);
        List<String> enum_states = new ArrayList<String>();
        while (obj != null)
        {
            if (obj instanceof String)
                enum_states.add((String)obj);
            else
            {
                Activator.getLogger().log(Level.WARNING, "Got non string enum state? " + obj.getClass().getName());
                enum_states.add(obj.toString());
            }
            i++;
            fname = "state." + Integer.toString(i);
            obj = map.get(fname);
        }
        return enum_states;
    }

    public static Point toNullMetaPoint(final String channel_name,
            final Instant stamp, final StoreAs storeas)
    {
        return Point.measurement(channel_name)
                .time(InfluxDBUtil.toNanoLong(stamp), TimeUnit.NANOSECONDS)
                .tag("datatype", storeas.name())
                .addField("null_metadata", true)
                .build();
    }

    private static Object mapToNull(Map<String, Object> map)
    {
        if (map.get("null_metadata") == null)
        {
            Activator.getLogger().log(Level.WARNING, "Expected null_metadata field in results. Not found.");
        }
        return null;
    }

    public static MetaObject toMetaObject(final List<String> cols, final List<Object> vals) throws Exception
    {
        Map<String, Object> map = new HashMap<String, Object>();

        final int N = cols.size();
        for (int i = 0; i < N; i++)
        {
            map.put(cols.get(i), vals.get(i));
        }

        final StoreAs storeas = storedTypeIs((String) map.get("datatype"));
        if (storeas == null)
        {
            throw new Exception ("Could not extract meta object from Query Results. Bad/No datatype tag: " + map.get("datatype"));
        }

        Instant ts = InfluxDBUtil.fromInfluxDBTimeFormat(map.get("time"));

        switch(storeas)
        {
        case ARCHIVE_DOUBLE :
        case ARCHIVE_LONG :
        case ARCHIVE_DOUBLE_ARRAY :
        case ARCHIVE_LONG_ARRAY :
            return new MetaObject(mapToDisplay(map), storeas, ts);
        case ARCHIVE_ENUM :
            return new MetaObject(mapToEnumList(map), storeas, ts);
        case ARCHIVE_STRING :
        case ARCHIVE_UNKNOWN :
            return new MetaObject(mapToNull(map), storeas, ts);
        default:
            throw new Exception ("Could not extract meta object from Query Results. Unhandled stored type: " + storeas.name());
        }
    }

    public static List<MetaObject> toMetaObjects(QueryResult results) throws Exception
    {
        if (InfluxDBResults.getValueCount(results) < 1)
        {
            throw new Exception ("Could not extract meta objects from Query Results. No values: " + results.toString());
        }

        final List<MetaObject> ret = new ArrayList<MetaObject>();
        final List<Series> all_series = InfluxDBResults.getNonEmptySeries(results);

        for (Series series : all_series)
        {
            final List<String> cols = series.getColumns();
            for (final List<Object> vals : series.getValues())
            {
                ret.add(toMetaObject(cols, vals));
            }
        }
        return ret;
    }


}
