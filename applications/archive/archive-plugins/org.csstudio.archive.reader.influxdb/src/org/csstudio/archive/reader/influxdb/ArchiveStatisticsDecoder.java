package org.csstudio.archive.reader.influxdb;

import java.time.Instant;
import java.util.List;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueDecoder;
import org.csstudio.archive.reader.influxdb.raw.AbstractInfluxDBValueLookup;
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
	private static final String STATS_NAMES [] = {"mean", "max", "min", "stddev"};
	/**
	 * Sample for statistics with a null or 0 count (number of values)
	 */
	public static final VType NULL_SAMPLE = new ArchiveVType(null, AlarmSeverity.INVALID, "NULL");
	
	public ArchiveStatisticsDecoder(AbstractInfluxDBValueLookup vals)
	{
		super(vals);
	}
	
    public static class Factory extends AbstractInfluxDBValueDecoder.Factory
    {
        @Override
        public AbstractInfluxDBValueDecoder create(AbstractInfluxDBValueLookup vals) {
            return new ArchiveStatisticsDecoder(vals);
        }
    }
    
    private static class StatisticsImpl implements Statistics
    {
    	private Double mean_max_min_stddev [];
    	private Integer count;
    	
    	public StatisticsImpl(Double mean_max_min_stddev [], Integer count)
    	{
    		this.mean_max_min_stddev = mean_max_min_stddev;
    		this.count = count;
    	}
    	
		@Override
		public Double getAverage() {
			return mean_max_min_stddev[0];
		}
		@Override
		public Double getMax() {
			return mean_max_min_stddev[1];
		}
		@Override
		public Double getMin() {
			return mean_max_min_stddev[2];
		}
		@Override
		public Integer getNSamples() {
			return count;
		}
		@Override
		public Double getStdDev() {
			return mean_max_min_stddev[3];
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
    	Object stats_vals [] = new Object [4];
    	int x = 0;
    	for (String stat : STATS_NAMES)
    	{
    		stats_vals[x] = vals.getValue(stat + "_long.0");
    		if (stats_vals[x] == null)
    			throw new Exception("Did not find "+stat+"_long.0 field where expected");
    		++x;
    	}
    	
    	Double mean_max_min_stddev [] = new Double [4];
    	for (int i = 0; i < 4; ++i)
    	{
    		mean_max_min_stddev[i] = (double) fieldToLong(stats_vals[i]);
    	}
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(mean_max_min_stddev, count));
    }

    //TODO: two for-loops is inefficient
    protected VType decodeDoubleSamples(final Instant time, final AlarmSeverity severity, final String status, Display display) throws Exception
    {
    	//first, check if count is zero
    	Object count_val = vals.getValue("count_double.0");
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return NULL_SAMPLE;
    	
    	//mean, max, min, stddev
    	Object stats_vals [] = new Object [4];
    	int x = 0;
    	for (String stat : STATS_NAMES)
    	{
    		stats_vals[x] = vals.hasValue(stat + "_double.0") ? vals.getValue(stat + "_double.0") : null;
    		if (stats_vals[x] == null)
    			//throw new Exception("Did not find "+stat+"_double.0 field where expected");
    			stats_vals[x] = x < 4 ? Double.NaN : 0;
    		++x;
    	}
    	
    	Double mean_max_min_stddev [] = new Double [4];
    	for (int i = 0; i < 4; ++i)
    	{
    		mean_max_min_stddev[i] = fieldToDouble(stats_vals[i]);
    	}
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(mean_max_min_stddev, count));
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
