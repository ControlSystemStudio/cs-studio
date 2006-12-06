package org.csstudio.value;

/** The numeric version of MetaData
 *  @see MetaData
 *  @author Kay Kasemir 
 */
public class NumericMetaData implements MetaData
{
	private final double disp_high;
	private final double disp_low;
	private final double alarm_high;
	private final double alarm_low;
	private final double warn_high;
	private final double warn_low;
	private final int prec;
	private final String units;

	/** Constructor for meta data from pieces. */
	public NumericMetaData(double disp_high, double disp_low,
			double alarm_high, double alarm_low, double warn_high,
			double warn_low, int prec, String units)
	{
		this.disp_high = disp_high;
		this.disp_low = disp_low;
		this.alarm_high = alarm_high;
		this.alarm_low = alarm_low;
		this.warn_high = warn_high;
		this.warn_low = warn_low;
		this.prec = prec;
		this.units = units;
	}

	/** @return High alarm limit. */
	public double getAlarmHigh()
	{	return alarm_high;	}

	/** @return Low alarm limit. */
	public double getAlarmLow()
	{	return alarm_low;	}

    /** @return High warning limit. */
    public double getWarnHigh()
    {   return warn_high;   }

    /** @return Low warning limit. */
    public double getWarnLow()
    {   return warn_low;    }
    
	/** @return Suggested upper display limit. */
	public double getDisplayHigh()
	{	return disp_high;	}

	/** @return Suggested lower display limit. */
	public double getDisplayLow()
	{	return disp_low;	}

	/** @return Suggested display precision (fractional digits). */
	public int getPrecision()
	{	return prec;	}

	/** @return The engineering units string. */
	public String getUnits()
	{	return units;	}

    @Override
	@SuppressWarnings("nls")
    public String toString()
	{
		return "NumericMetaData:\n"
		+ "    disp_high  :" + disp_high + "\n"
		+ "    disp_low   :" + disp_low + "\n"
        + "    warn_high  :" + warn_high + "\n"
        + "    warn_low   :" + warn_low + "\n"
		+ "    alarm_high :" + alarm_high + "\n"
		+ "    alarm_low  :" + alarm_low + "\n"
		+ "    prec       :" + prec + "\n"
		+ "    units      :" + units + "\n";		
	}
}
