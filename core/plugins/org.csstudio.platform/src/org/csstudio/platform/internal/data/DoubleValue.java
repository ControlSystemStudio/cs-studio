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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

/** Implementation of {@link IDoubleValue}.
 *  @author Kay Kasemir
 */
public class DoubleValue extends Value implements IDoubleValue
{
    /** The values. */
	final private double values[];
	
    /** Constructor from pieces. */
	public DoubleValue(final ITimestamp time, final ISeverity severity,
                       final String status, final INumericMetaData meta_data,
                       final Quality quality,
                       final double values[])
	{
		super(time, severity, status, meta_data, quality);
		this.values = values;
	}

    /** {@inheritDoc} */
	final public double[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
	final public double getValue()
	{	return values[0];	}
	
    /** {@inheritDoc} */
	@Override
    public String format(Format how, int precision)
	{
		StringBuffer buf = new StringBuffer();
		if (getSeverity().hasValue())
		{
            NumberFormat fmt;
            if (how == Format.Exponential)
            {   // Is there a better way to get this silly format?
                StringBuffer pattern = new StringBuffer(10);
                pattern.append("0."); //$NON-NLS-1$
                for (int i=0; i<precision; ++i)
                    pattern.append('0');
                pattern.append("E0"); //$NON-NLS-1$
                fmt = new DecimalFormat(pattern.toString());
            }
            else
            {
                fmt = NumberFormat.getNumberInstance();
                if (how == Format.Default)
                {
                    INumericMetaData num_meta = (INumericMetaData)getMetaData();
                    precision = num_meta.getPrecision();
                }
                fmt.setMinimumFractionDigits(precision);
                fmt.setMaximumFractionDigits(precision);
            }
            buf.append(formatDouble(fmt, values[0]));
            for (int i = 1; i < values.length; i++)
            {
                buf.append(Messages.ArrayElementSeparator);
                buf.append(formatDouble(fmt, values[i]));
            }
        }
		else
			buf.append(Messages.NoValue);
		return buf.toString();
	}

    private String formatDouble(final NumberFormat fmt, double d)
    {
        if (Double.isInfinite(d))
            return Messages.Infinite;
        if (Double.isNaN(d))
            return Messages.NaN;
        return fmt.format(d);
    }
	
    /** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj)
	{
		if (! (obj instanceof DoubleValue))
			return false;
		final DoubleValue rhs = (DoubleValue) obj;
		if (rhs.values.length != values.length)
			return false;
        // Compare individual values, using the hint from
		// page 33 of the "Effective Java" book to handle
		// NaN and Infinity
        for (int i=0; i<values.length; ++i)
            if (Double.doubleToLongBits(values[i]) !=
                Double.doubleToLongBits(rhs.values[i]))
                return false;
		return super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		int h = super.hashCode();
		for (int i=0; i<values.length; ++i)
			h += values[i];
		return h;
	}
}
