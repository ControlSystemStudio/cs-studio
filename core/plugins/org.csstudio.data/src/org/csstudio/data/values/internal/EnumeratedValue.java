/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.data.values.internal;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.Messages;

/** Implementation of {@link IEnumeratedValue}.
 *  @see IEnumeratedValue
 *  @author Kay Kasemir, Xihui Chen
 */
public class EnumeratedValue extends Value implements IEnumeratedValue
{
    private static final long serialVersionUID = 1L;

    final private int values[];

    /** Constructor from pieces. */
	public EnumeratedValue(final ITimestamp time, final ISeverity severity, final String status,
                    final IEnumeratedMetaData meta_data, final Quality quality,
                    final int values[])
	{
		super(time, severity, status, meta_data, quality);
		this.values = values;
	}

    /** {@inheritDoc} */
	@Override
    final public int[] getValues()
	{	return values;	}

    /** {@inheritDoc} */
	@Override
    final public int getValue()
	{	return values[0];  }

    /** {@inheritDoc} */
	@Override
	final public String format(final Format how, final int precision)
	{
	    final IEnumeratedMetaData enum_meta = getMetaData();
	    final StringBuffer buf = new StringBuffer();
		if (getSeverity().hasValue())
		{
			if(how == Format.Default || how == Format.String){
				buf.append(enum_meta.getState(values[0]));
				for (int i = 1; i < values.length; i++)
				{
					buf.append(Messages.ArrayElementSeparator);
		            buf.append(enum_meta.getState(values[i]));
				}
			}else if (how == Format.Decimal){
				buf.append(values[0]);
				for (int i = 1; i < values.length; i++)
				{
					buf.append(Messages.ArrayElementSeparator);
		            buf.append(values[i]);
				}
			}else if (how == Format.Exponential){
				// Is there a better way to get this silly format?
			 	NumberFormat fmt;
                final StringBuffer pattern = new StringBuffer(10);
                pattern.append("0."); //$NON-NLS-1$
                for (int i=0; i<precision; ++i) {
                    pattern.append('0');
                }
                pattern.append("E0"); //$NON-NLS-1$
                fmt = new DecimalFormat(pattern.toString());
                buf.append(fmt.format(values[0]));
                for (int i = 1; i < values.length; i++)
	        	 {
	        		 buf.append(Messages.ArrayElementSeparator);
	        		 buf.append(buf.append(fmt.format(values[i])));
	        	 }
			}

		} else {
            buf.append(Messages.NoValue);
        }
		return buf.toString();
	}

    /** {@inheritDoc} */
	@Override
	final public boolean equals(final Object obj)
	{
		if (! (obj instanceof EnumeratedValue)) {
            return false;
        }
		final EnumeratedValue rhs = (EnumeratedValue) obj;
		if (rhs.values.length != values.length) {
            return false;
        }
		for (int i=0; i<values.length; ++i) {
            if (rhs.values[i] != values[i]) {
                return false;
            }
        }
		return super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	final public int hashCode()
	{
		int h = super.hashCode();
		for (int i=0; i<values.length; ++i) {
            h += values[i];
        }
		return h;
	}


	/** {@inheritDoc} */
	@Override
	public IEnumeratedMetaData getMetaData() {
	    return (IEnumeratedMetaData) super.getMetaData();
	}
}
