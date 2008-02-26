/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
