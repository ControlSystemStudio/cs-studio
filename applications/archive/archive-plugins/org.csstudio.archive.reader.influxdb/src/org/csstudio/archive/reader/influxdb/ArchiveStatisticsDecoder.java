package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueDecoder;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueLookup;
import org.csstudio.archive.reader.influxdb.raw.Preferences;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Statistics;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/**
 * Decode statistical values (mean/average, max, min, etc.) into VType
 * @author Amanda Carpenter
 *
 */
public class ArchiveStatisticsDecoder extends ArchiveDecoder
{
    //lookup names (prefixes) of statistics fields; used for iteration
    private static final String STATS_NAMES [] = {"mean_", "max_", "min_"};
    /**
     * Sample for statistics with a null or 0 count (number of values)
     */
    public static final VType NULL_SAMPLE = new ArchiveVType(null, AlarmSeverity.INVALID, "NULL");

    private final boolean useStdDev;

    public ArchiveStatisticsDecoder(AbstractInfluxDBValueLookup vals, final boolean stdDev)
    {
        super(vals);
        useStdDev = stdDev;
    }

    public static class Factory extends AbstractInfluxDBValueDecoder.Factory
    {
        private final boolean useStdDev;
        public Factory()
        {
            this(Preferences.getUseStdDev());
        }
        public Factory(boolean stdDev)
        {
            useStdDev = stdDev;
        }
        @Override
        public AbstractInfluxDBValueDecoder create(AbstractInfluxDBValueLookup vals) {
            return new ArchiveStatisticsDecoder(vals, useStdDev);
        }
    }

    private static class StatisticsImpl implements Statistics
    {
        private final Double mean, max, min, stddev;
        private final Integer count;

        public StatisticsImpl(Double mean_max_min_stddev [], Integer count)
        {
            mean = valOrNaN(mean_max_min_stddev[0]);
            max = valOrNaN(mean_max_min_stddev[1]);
            min = valOrNaN(mean_max_min_stddev[2]);
            stddev = valOrNaN(mean_max_min_stddev[3]);
            this.count = count;
        }

        private Double valOrNaN(Double val)
        {
            return val != null ? val : Double.NaN;
        }

        @Override
        public Double getAverage() {
            return mean;
        }
        @Override
        public Double getMax() {
            return max;
        }
        @Override
        public Double getMin() {
            return min;
        }
        @Override
        public Integer getNSamples() {
            return count;
        }
        @Override
        public Double getStdDev() {
            return stddev;
        }
    }

    @Override
    protected VType decodeLongSample(final Instant time, final AlarmSeverity severity, final String status, Display display, String prefix) throws Exception
    {
        String part_name = "long.0"; //partial column name
        //First, check if count is zero
        // Try to get count using "plain" field key
        Object count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
        int count;
        if (count_val == null || (count = fieldToInt(count_val)) == 0)
        {    // Failed, so try to get value from prefix + field key
            part_name = prefix + part_name;
            count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
            if (count_val == null || (count = fieldToInt(count_val)) == 0)
                return new ArchiveVStatistics(time, severity, status, display,
                        Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0);
        }

        //Second, get other values
        //mean, max, min, stddev
        Double stats_vals [] = new Double [4];
        for (int i = 0; i < 3; ++i)
        {
            Object val = vals.getValue(STATS_NAMES[i] + part_name);
            if (val == null)
                throw new Exception("Did not find "+STATS_NAMES[i]+part_name+" field where expected");
            stats_vals[i] = fieldToLong(val).doubleValue(); //TODO: if fieldToDouble is better, could extract a method decodeStatistics(fieldname)
        }
        if (useStdDev)
        {
            Object val = vals.getValue("stddev_"+part_name);
            if (val == null)
                throw new Exception("Did not find stddev_"+part_name+" field where expected");
            stats_vals[3] = fieldToLong(val).doubleValue();
        }
        else
            stats_vals[3] = Double.NaN;

        return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }

    @Override
    protected VType decodeDoubleSamples(final Instant time, final AlarmSeverity severity, final String status, Display display, String prefix) throws Exception
    {
        String part_name = "double.0"; //partial column name
        //First, check if count is zero
        // Try to get count using "plain" field key
        Object count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
        int count;
        if (count_val == null || (count = fieldToInt(count_val)) == 0)
        {    // Failed, so try to get value from prefix + field key
            part_name = prefix + part_name;
            count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
            if (count_val == null || (count = fieldToInt(count_val)) == 0)
                return new ArchiveVStatistics(time, severity, status, display,
                        Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0);
        }

        //Second, get other values
        //mean, max, min, stddev
        Double stats_vals [] = new Double [4];
        for (int i = 0; i < 3; ++i)
        {
            Object val = vals.getValue(STATS_NAMES[i] + part_name);
            if (val == null)
                throw new Exception("Did not find "+STATS_NAMES[i]+part_name+" field where expected");
            stats_vals[i] = fieldToDouble(val);
        }
        if (useStdDev)
        {
            Object val = vals.getValue("stddev_"+part_name);
            if (val == null)
                throw new Exception("Did not find stddev_"+part_name+" field where expected");
            stats_vals[3] = fieldToDouble(val);
        }
        else
            stats_vals[3] = Double.NaN;

        return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }

    @Override
    protected VType decodeEnumSample(final Instant time, final AlarmSeverity severity, final String status, List<String> labels, String prefix) throws Exception
    {
        String part_name = "long.0"; //partial column name
        //First, check if count is zero
        // Try to get count using "plain" field key
        Object count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
        int count;
        if (count_val == null || (count = fieldToInt(count_val)) == 0)
        {    // Failed, so try to get value from prefix + field key
            part_name = prefix + part_name;
            count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
            if (count_val == null)
                count = 0;
            else
                count = fieldToInt(count_val);
        }

        //Second, get other values
        final Display display = ValueFactory.displayNone();
        return new ArchiveVStatistics(time, severity, status, display, Double.NaN, Double.NaN, Double.NaN, Double.NaN, count);
    }

    @Override
    protected VType decodeStringSample(Instant time, AlarmSeverity severity, String status, String prefix) throws Exception
    {
        //Regarding timestamps:
        //    The data in vals is expected to represent the results of a "group by time()" query.
        //The timestamp of data in the results of by a "group by time()" query is the start time for
        //each time interval, not the timestamp of the first datum in the interval.
        //Thus, the timestamps of the metadata used to determine how to decode the data (i.e. what datatype)
        //might not align with the timestamps of the data in vals.
        //    Suppose a string value (like "Disconnected") occurs before the start of a group by interval, but the
        //interval contains only double values. The decoder will use the datatype of the metadata before
        //the start of the interval (i.e. string), not the datatype of the metadata whose timestamp matches the
        //first datum in the interval (i.e. double). In other words, we expect a string value where there is none.
        //    Since it's impractical to recover the true start time of the data in the interval, there's no
        //way to get the true datatype of the data. So, if we get a null value from the "string.0" column,
        //we'll just have to ignore it and move on.
        String part_name = "string.0";
        //First, check if count is zero
        // Try to get count using "plain" field key
        Object count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
        int count;
        if (count_val == null || (count = fieldToInt(count_val)) == 0)
        {    // Failed, so try to get value from prefix + field key
            part_name = prefix + part_name;
            count_val = vals.hasValue("count_" + part_name) ? vals.getValue("count_" + part_name) : null;
            if (count_val == null)
                count = 0;
            else
                count = fieldToInt(count_val);
        }

        if (count == 0)
        {
            Activator.getLogger().log(Level.FINE, "Expected string value, but no strings in\n" + vals.toString());
        }

        //Second, get other values
        final Display display = ValueFactory.displayNone();
        return new ArchiveVStatistics(time, severity, status, display, Double.NaN, Double.NaN, Double.NaN, Double.NaN, count);
    }

    private static Integer fieldToInt(Object val) throws Exception
    {
        Integer integer;
        //try
        //{
        //    integer = Integer.class.cast(val);
        //}
        //catch (Exception e)
        //{
            try
            {
                integer = Double.valueOf(val.toString()).intValue();
            }
            catch (Exception e1)
            {
                throw new Exception ("Could not transform object to Integer: " + val.getClass().getName());
            }
        //}
        return integer;
    }
}
