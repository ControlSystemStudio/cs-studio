package org.csstudio.diag.probe;

import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;

/** Info about the most recent value.
 *  <p>
 *  Since the data is updated in a protocol thread,
 *  but displayed in a GUI thread,
 *  this class holds the data and handles the synchronization.
 *  @author Kay Kasemir
 */
public class ValueInfo
{
    /** The most recent value of the PV. */
    private String value_txt = "";
    
    /** The most recent numeric meta data of the PV, or <code>null</code> */
    private INumericMetaData numeric_metadata = null;

    /** The most recent numeric value of the PV.
     *  <p>
     *  Only valid if numeric_metatdata != null.
     */
    private double value_dbl;
    
    /** The most recent time stamp of the PV. */
    private ITimestamp time = null;
    
    /** Smoothed period in seconds between received values. */
    private SmoothedDouble value_period = new SmoothedDouble();

    synchronized public String getValueText()
    {
        return value_txt == null ? "" : value_txt; //$NON-NLS-1$
    }
    
    synchronized INumericMetaData getNumericMetaData()
    {
        return numeric_metadata;
    }

    public double getDouble()
    {
        return value_dbl;
    }

    synchronized public String getTimeText()
    {
        return time == null ? "" : time.toString(); //$NON-NLS-1$
    }
    
    synchronized double getUpdatePeriod()
    {
        return value_period.get();
    }

    synchronized void reset()
    {
        value_txt = ""; //$NON-NLS-1$
        time = null;
    }
    
    synchronized public void update(IValue value)
    {
        value_txt = ValueUtil.formatValueAndSeverity(value);
        
        final IMetaData meta = value.getMetaData();
        if (meta instanceof INumericMetaData)
        {
            numeric_metadata = (INumericMetaData) meta;
            value_dbl = ValueUtil.getDouble(value);
        }
        else
            numeric_metadata = null;
        
        final ITimestamp new_time = value.getTime();
        if (time != null)
        {
            final double period = new_time.toDouble() - time.toDouble();
            value_period.add(period);
        }
        else
            value_period.reset();
        time = new_time;
    }
}
