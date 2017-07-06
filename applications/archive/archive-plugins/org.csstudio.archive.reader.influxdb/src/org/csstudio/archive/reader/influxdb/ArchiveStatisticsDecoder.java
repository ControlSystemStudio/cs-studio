package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.List;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueDecoder;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueLookup;
import org.csstudio.archive.reader.influxdb.raw.Preferences;
import org.csstudio.archive.vtype.ArchiveVEnum;
import org.csstudio.archive.vtype.ArchiveVStatistics;
import org.csstudio.archive.vtype.ArchiveVString;
import org.csstudio.archive.vtype.ArchiveVType;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Display;
import org.diirt.vtype.Statistics;
import org.diirt.vtype.VType;

/**
 * Decode statistical values (mean/average, max, min, etc.) into VType
 * @author Amanda Carpenter
 *
 */
public class ArchiveStatisticsDecoder extends ArchiveDecoder
{
	//lookup names of statistics fields; used for iteration
	private static final String STATS_NAMES [] = {"mean", "max", "min"};
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
    		mean = mean_max_min_stddev[0];
    		max = mean_max_min_stddev[1];
    		min = mean_max_min_stddev[2];
    		stddev = mean_max_min_stddev[3];
    		this.count = count;
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
    protected VType decodeLongSample(final Instant time, final AlarmSeverity severity, final String status, Display display) throws Exception
    {
    	//first, check if count is zero
    	Object count_val = vals.getValue("count_long.0");
    	int count = fieldToInt(count_val);
    	if (count == 0)
    		return NULL_SAMPLE;
    	
    	//mean, max, min, stddev
    	Double stats_vals [] = new Double [4];
    	for (int i = 0; i < 3; ++i)
    	{
    		Object val = vals.getValue(STATS_NAMES[i] + "_long.0");
    		if (val == null)
    			throw new Exception("Did not find "+STATS_NAMES[i]+"_long.0 field where expected");
			stats_vals[i] = fieldToLong(val).doubleValue();
    	}
    	if (useStdDev)
    	{
    		Object val = vals.getValue("stddev_long.0");
    		if (val == null)
    			throw new Exception("Did not find stddev_long.0 field where expected");
			stats_vals[3] = fieldToLong(val).doubleValue();
    	}
    	else
    		stats_vals[3] = Double.NaN;
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }

    protected VType decodeDoubleSamples(final Instant time, final AlarmSeverity severity, final String status, Display display) throws Exception
    {
    	//first, check if count is zero
    	Object count_val = vals.getValue("count_double.0");
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return NULL_SAMPLE;
    	
    	//mean, max, min, stddev
    	Double stats_vals [] = new Double [4];
    	for (int i = 0; i < 3; ++i)
    	{
    		Object val = vals.getValue(STATS_NAMES[i] + "_double.0");
    		if (val == null)
    			throw new Exception("Did not find "+STATS_NAMES[i]+"_double.0 field where expected");
			stats_vals[i] = fieldToLong(val).doubleValue();
    	}
    	if (useStdDev)
    	{
    		Object val = vals.getValue("stddev_double.0");
    		if (val == null)
    			throw new Exception("Did not find stddev_double.0 field where expected");
			stats_vals[3] = fieldToLong(val).doubleValue();
    	}
    	else
    		stats_vals[3] = Double.NaN;
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }
    
    @Override
    protected VType decodeEnumSample(final Instant time, final AlarmSeverity severity, final String status, List<String> labels) throws Exception
    {
    	//first, check if count is zero
    	Object count_val = vals.getValue("count_long.0");
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return NULL_SAMPLE;
    	
        Object val = vals.getValue("first_long.0");
        if (val == null)
        {
            throw new Exception ("Did not find first_long.0 field where expected");
        }
        return new ArchiveVEnum(time, severity, status, labels, fieldToLong(val).intValue());
    }
    
    @Override
    protected VType decodeStringSample(Instant time, AlarmSeverity severity, String status) throws Exception
    {
    	//first, check if count is zero
    	Object count_val = vals.getValue("count_string.0");
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return NULL_SAMPLE;
    	
        Object val = vals.getValue("first_string.0");
        if (val == null)
        {
            throw new Exception ("Did not find first_string.0 field where expected");
        }
        return new ArchiveVString(time, severity, status, val.toString());
	}
    
    private Integer fieldToInt(Object val) throws Exception
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
