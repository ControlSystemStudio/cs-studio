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
    	//first, check if count is zero
    	Object count_val = vals.getValue("count_"+prefix+"long.0");
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return new ArchiveVStatistics(time, severity, status, display,
    				Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0);
    	
    	//mean, max, min, stddev
    	Double stats_vals [] = new Double [4];
    	for (int i = 0; i < 3; ++i)
    	{
    		Object val = vals.getValue(STATS_NAMES[i] + prefix + "_long.0");
    		if (val == null)
    			throw new Exception("Did not find "+STATS_NAMES[i]+prefix+"_long.0 field where expected");
			stats_vals[i] = fieldToLong(val).doubleValue();
    	}
    	if (useStdDev)
    	{
    		Object val = vals.getValue("stddev"+prefix+"_long.0");
    		if (val == null)
    			throw new Exception("Did not find stddev"+prefix+"_long.0 field where expected");
			stats_vals[3] = fieldToLong(val).doubleValue();
    	}
    	else
    		stats_vals[3] = Double.NaN;
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }

    @Override
    protected VType decodeDoubleSamples(final Instant time, final AlarmSeverity severity, final String status, Display display, String prefix) throws Exception
    {
    	//first, check if count is zero
    	Object count_val = vals.getValue("count_"+prefix+"double.0");
    	int count = count_val != null ? fieldToInt(count_val) : 0;
    	if (count == 0)
    		return new ArchiveVStatistics(time, severity, status, display,
    				Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0);
    	
    	//mean, max, min, stddev
    	Double stats_vals [] = new Double [4];
    	for (int i = 0; i < 3; ++i)
    	{
    		Object val = vals.getValue(STATS_NAMES[i] + prefix + "_double.0");
    		if (val == null)
    			throw new Exception("Did not find "+STATS_NAMES[i]+prefix+"_double.0 field where expected");
			stats_vals[i] = fieldToDouble(val);
    	}
    	if (useStdDev)
    	{
    		Object val = vals.getValue("stddev"+prefix+"_double.0");
    		if (val == null)
    			throw new Exception("Did not find stddev_"+prefix+"double.0 field where expected");
			stats_vals[3] = fieldToDouble(val);
    	}
    	else
    		stats_vals[3] = Double.NaN;
    	
    	return new ArchiveVStatistics(time, severity, status, display, new StatisticsImpl(stats_vals, count));
    }
    
    @Override
    protected VType decodeEnumSample(final Instant time, final AlarmSeverity severity, final String status, List<String> labels, String prefix) throws Exception
    {
    	Object count_val = vals.getValue("count_"+prefix+"long.0");
    	if (count_val == null)
			throw new Exception("Did not find count_"+prefix+"long.0 field where expected");
    	final int count = fieldToInt(count_val);
    	final Display display = ValueFactory.displayNone();
		return new ArchiveVStatistics(time, severity, status, display, Double.NaN, Double.NaN, Double.NaN, Double.NaN, count);
    }
    
    @Override
    protected VType decodeStringSample(Instant time, AlarmSeverity severity, String status, String prefix) throws Exception
    {
    	Object count_val = vals.getValue("count_"+prefix+"string.0");
    	if (count_val == null)
			throw new Exception("Did not find count_"+prefix+"string.0 field where expected");
    	final int count = fieldToInt(count_val);
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
