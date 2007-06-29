package org.csstudio.platform.internal.data;

import org.csstudio.platform.data.INumericMetaData;

/** Implementation of {@link INumericMetaData}.
 *  @see INumericMetaData
 *  @author Kay Kasemir 
 */
public class NumericMetaData implements INumericMetaData
{
    private final double disp_low;
	private final double disp_high;
    private final double warn_low;
    private final double warn_high;
    private final double alarm_low;
	private final double alarm_high;
	private final int prec;
	private final String units;

	/** Constructor for meta data from pieces. */
	public NumericMetaData(double disp_low, double disp_high,
                    double warn_low, double warn_high,
                    double alarm_low, double alarm_high,
                    int prec, String units)
	{
        this.disp_low = disp_low;
		this.disp_high = disp_high;
        this.warn_low = warn_low;
        this.warn_high = warn_high;
        this.alarm_low = alarm_low;
		this.alarm_high = alarm_high;
		this.prec = prec;
		this.units = units;
	}
    
    /** {@inheritDoc} */
    public double getDisplayLow()
    {   return disp_low;    }

    /** {@inheritDoc} */
    public double getDisplayHigh()
    {   return disp_high;   }

    /** {@inheritDoc} */
    public double getWarnLow()
    {   return warn_low;    }

    /** {@inheritDoc} */
    public double getWarnHigh()
    {   return warn_high;   }

    /** {@inheritDoc} */
    public double getAlarmLow()
    {   return alarm_low;   }

    /** {@inheritDoc} */
    public double getAlarmHigh()
    {   return alarm_high;  }

    /** {@inheritDoc} */
	public int getPrecision()
	{	return prec;	}

    /** {@inheritDoc} */
	public String getUnits()
	{	return units;	}
    
    /** @return <code>true</code> if given meta data equals this */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
            return true;
        if (! (obj instanceof INumericMetaData))
            return false;
        final INumericMetaData other = (INumericMetaData) obj;
        return other.getDisplayLow() == disp_low &&
               other.getDisplayHigh() == disp_high &&
               other.getWarnLow() == warn_low &&
               other.getWarnHigh() == warn_high &&
               other.getAlarmHigh() == alarm_high &&
               other.getAlarmLow() == alarm_low &&
               other.getPrecision() == prec &&
               other.getUnits().equals(units);
    }

    /** {@inheritDoc} */
    @Override
	@SuppressWarnings("nls")
    public String toString()
	{
		return "NumericMetaData:\n"
        + "    disp_low   :" + disp_low + "\n"
		+ "    disp_high  :" + disp_high + "\n"
        + "    warn_low   :" + warn_low + "\n"
        + "    warn_high  :" + warn_high + "\n"
        + "    alarm_low  :" + alarm_low + "\n"
		+ "    alarm_high :" + alarm_high + "\n"
		+ "    prec       :" + prec + "\n"
		+ "    units      :" + units + "\n";		
	}
}
