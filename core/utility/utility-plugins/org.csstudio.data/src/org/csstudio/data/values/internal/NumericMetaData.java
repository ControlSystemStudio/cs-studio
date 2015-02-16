/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import org.csstudio.data.values.INumericMetaData;

/** Implementation of {@link INumericMetaData}.
 *  @see INumericMetaData
 *  @author Kay Kasemir
 */
public class NumericMetaData implements INumericMetaData
{
    private static final long serialVersionUID = 1L;

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
    @Override
    public double getDisplayLow()
    {   return disp_low;    }

    /** {@inheritDoc} */
    @Override
    public double getDisplayHigh()
    {   return disp_high;   }

    /** {@inheritDoc} */
    @Override
    public double getWarnLow()
    {   return warn_low;    }

    /** {@inheritDoc} */
    @Override
    public double getWarnHigh()
    {   return warn_high;   }

    /** {@inheritDoc} */
    @Override
    public double getAlarmLow()
    {   return alarm_low;   }

    /** {@inheritDoc} */
    @Override
    public double getAlarmHigh()
    {   return alarm_high;  }

    /** {@inheritDoc} */
    @Override
	public int getPrecision()
	{	return prec;	}

    /** {@inheritDoc} */
    @Override
	public String getUnits()
	{	return units;	}

    @Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    long temp;
	    temp = Double.doubleToLongBits(alarm_high);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    temp = Double.doubleToLongBits(alarm_low);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    temp = Double.doubleToLongBits(disp_high);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    temp = Double.doubleToLongBits(disp_low);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    result = prime * result + prec;
	    result = prime * result + ((units == null) ? 0 : units.hashCode());
	    temp = Double.doubleToLongBits(warn_high);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    temp = Double.doubleToLongBits(warn_low);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    return result;
    }

    /** @return <code>true</code> if given meta data equals this */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == this)
            return true;
        if (! (obj instanceof INumericMetaData))
            return false;
        final INumericMetaData other = (INumericMetaData) obj;
        // Compare all the elements, w/ proper handling of double NaN/Inf.
        return Double.doubleToLongBits(other.getDisplayLow())
                == Double.doubleToLongBits(disp_low) &&
               Double.doubleToLongBits(other.getDisplayHigh())
                == Double.doubleToLongBits(disp_high) &&
               Double.doubleToLongBits(other.getWarnLow())
                == Double.doubleToLongBits(warn_low) &&
               Double.doubleToLongBits(other.getWarnHigh())
                == Double.doubleToLongBits(warn_high) &&
               Double.doubleToLongBits(other.getAlarmHigh())
                == Double.doubleToLongBits(alarm_high) &&
               Double.doubleToLongBits(other.getAlarmLow())
                == Double.doubleToLongBits(alarm_low) &&
               other.getPrecision() == prec &&
               other.getUnits().equals(units);
    }

    /** {@inheritDoc} */
    @Override
	@SuppressWarnings("nls")
    public String toString()
	{
		return "NumericMetaData:\n"
        + "    units      :" + units + "\n"
        + "    prec       :" + prec + "\n"
        + "    disp_low   :" + disp_low + "\n"
		+ "    disp_high  :" + disp_high + "\n"
        + "    alarm_low  :" + alarm_low + "\n"
        + "    warn_low   :" + warn_low + "\n"
        + "    warn_high  :" + warn_high + "\n"
		+ "    alarm_high :" + alarm_high + "\n";
	}
}
