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

import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.ITimestamp;
import org.eclipse.osgi.util.NLS;

/** Implementation of {@link IMinMaxDoubleValue}.
 *  @author Kay Kasemir
 */
public class MinMaxDoubleValue extends DoubleValue implements IMinMaxDoubleValue
{
    /** The minimum resp. maximum. */
	final private double minimum, maximum;
	
    /** Constructor from pieces. */
	@SuppressWarnings("nls")
    public MinMaxDoubleValue(final ITimestamp time, final ISeverity severity,
                       final String status, final INumericMetaData meta_data,
                       final Quality quality,
                       final double values[],
                       final double minimum,
                       final double maximum)
	{
		super(time, severity, status, meta_data, quality, values);
        if (minimum > maximum)
            throw new IllegalArgumentException("Minimum " + minimum +
                                               " > Maximum " + maximum);
		this.minimum = minimum;
        this.maximum = maximum;
	}

    /** {@inheritDoc} */
    final public double getMinimum()
    {
        return minimum;
    }

    /** {@inheritDoc} */
    final public double getMaximum()
    {
        return maximum;
    }

    /** {@inheritDoc} */
	@Override
    final public String format(Format how, int precision)
	{
		if (getSeverity().hasValue())
            return super.format(how, precision)
                   + NLS.bind(Messages.MiniMaxiFormat,
                              new Double(minimum), new Double(maximum));
		// else
        return Messages.NoValue;
	}
	
    /** {@inheritDoc} */
	@Override
	final public boolean equals(final Object obj)
	{
		if (! (obj instanceof MinMaxDoubleValue))
			return false;
		final MinMaxDoubleValue rhs = (MinMaxDoubleValue) obj;
        return minimum == rhs.minimum &&
               maximum == rhs.maximum &&
               super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	final public int hashCode()
	{
        return super.hashCode() + (int) (minimum + maximum);
	}
}
