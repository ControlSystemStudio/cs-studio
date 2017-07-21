package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.List;
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
    	final String fieldname = prefix == null || vals.hasValue("count_long.0") && vals.getValue("count_long.0") != null ? "long.0" : prefix + "long.0";

    	//first, check if count is zero
    	Object count_val = vals.getValue("count_"+fieldname);
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return new ArchiveVStatistics(time, severity, status, display,
    				Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0);
    	
    	//mean, max, min, stddev
    	Double stats_vals [] = new Double [4];
    	for (int i = 0; i < 3; ++i)
    	{
    		Object val = vals.getValue(STATS_NAMES[i] + fieldname);
    		if (val == null)
    			throw new Exception("Did not find "+STATS_NAMES[i]+fieldname+" field where expected");
			stats_vals[i] = fieldToLong(val).doubleValue(); //TODO: if fieldToDouble is better, could extract a method decodeStatistics(fieldname)
    	}
    	if (useStdDev)
    	{
    		Object val = vals.getValue("stddev_"+fieldname);
    		if (val == null)
    			throw new Exception("Did not find stddev_"+fieldname+" field where expected");
			stats_vals[3] = fieldToLong(val).doubleValue();
    	}
    	else
    		stats_vals[3] = Double.NaN;
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }

    @Override
    protected VType decodeDoubleSamples(final Instant time, final AlarmSeverity severity, final String status, Display display, String prefix) throws Exception
    {
    	final String fieldname = prefix == null || vals.hasValue("count_double.0") && vals.getValue("count_double.0") != null ? "double.0" : prefix + "double.0";

    	//first, check if count is zero
    	Object count_val = vals.getValue("count_"+fieldname);
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return new ArchiveVStatistics(time, severity, status, display,
    				Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0);
    	
    	//mean, max, min, stddev
    	Double stats_vals [] = new Double [4];
    	for (int i = 0; i < 3; ++i)
    	{
    		Object val = vals.getValue(STATS_NAMES[i] + fieldname);
    		if (val == null)
    			throw new Exception("Did not find "+STATS_NAMES[i]+fieldname+" field where expected");
			stats_vals[i] = fieldToDouble(val);
    	}
    	if (useStdDev)
    	{
    		Object val = vals.getValue("stddev_"+fieldname);
    		if (val == null)
    			throw new Exception("Did not find stddev_"+fieldname+" field where expected");
			stats_vals[3] = fieldToDouble(val);
    	}
    	else
    		stats_vals[3] = Double.NaN;
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }
    
    @Override
    protected VType decodeEnumSample(final Instant time, final AlarmSeverity severity, final String status, List<String> labels, String prefix) throws Exception
    {
    	final String fieldname = prefix == null || vals.hasValue("count_long.0") ? "count_long.0" : "count_" + prefix + "long.0";
        Object count_val = vals.getValue(fieldname);
	    if (count_val == null)
	        throw new Exception ("Did not find "+fieldname+" field where expected");
    	final int count = fieldToInt(count_val);
    	final Display display = ValueFactory.displayNone();
		return new ArchiveVStatistics(time, severity, status, display, Double.NaN, Double.NaN, Double.NaN, Double.NaN, count);
    }
    
    @Override
    protected VType decodeStringSample(Instant time, AlarmSeverity severity, String status, String prefix) throws Exception
    {
    	//TODO: For supporting continuous queries, fix handling of missing String fields
    	final String fieldname = prefix == null || vals.hasValue("count_string.0") ? "count_string.0" : "count_" + prefix + "string.0";
        final Object count_val = vals.getValue(fieldname);
        //	The value of the count field might be null if the channel has string (a) sample(s) before the beginning of the
        //time interval (metadata's datatype indicates string), but the time interval itself contains only
        //non-string data. (The metadata entry indicating a non-string type is not applied, since it occurs after
        //the timestamp of the time interval).
        //	A channel with non-string datatype can have string samples for "WriteError", "Disconnected", etc.
    	final int count = count_val == null ? 0 : fieldToInt(count_val);
    	final Display display = ValueFactory.displayNone();
		return new ArchiveVStatistics(time, severity, status, display, Double.NaN, Double.NaN, Double.NaN, Double.NaN, count);
	}
    
    private static Integer fieldToInt(Object val) throws Exception
    {
        Integer integer;
        //try
        //{
        //	integer = Integer.class.cast(val);
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
