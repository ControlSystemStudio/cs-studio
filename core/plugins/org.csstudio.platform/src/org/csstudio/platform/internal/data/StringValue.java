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

import org.csstudio.platform.data.IStringValue;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.ISeverity;

/** Implementation of {@link IStringValue}.
 *  @see IStringValue
 *  @author Kay Kasemir
 */
public class StringValue extends Value implements IStringValue
{
    // Slight inconsistency, because that's the way EPICS works right now:
    // There is no array of Strings as there would be arrays of
    // the other types, so we only handle a scalar string as well....
	private final String values[];
	
	public StringValue(ITimestamp time, ISeverity severity, String status,
                       Quality quality,
                       String values[])
    {   // String has no meta data!
		super(time, severity, status, null, quality);
		this.values = values;
		if (values == null  ||  values.length < 1)
		    throw new java.lang.IllegalArgumentException("Values"); //$NON-NLS-1$
	}

    /** {@inheritDoc} */
    public final String[] getValues()
    {
        return values;
    }

    /** {@inheritDoc} */
	public final String getValue()
	{
		return values[0];
	}
	
    /** {@inheritDoc} */
	@Override
    public final String format(Format how, int precision)
	{
		if (getSeverity().hasValue() == false)
	        return Messages.NoValue;
		if (values.length == 1)
            return values[0];
		final StringBuffer result = new StringBuffer();
        result.append(values[0]);
        for (int i = 1; i < values.length; i++)
        {
            result.append(Messages.ArrayElementSeparator);
            result.append(values[i]);
        }
        return result.toString();
	}
	
    /** {@inheritDoc} */
	@Override
	public final boolean equals(final Object obj)
	{
		if (! (obj instanceof StringValue))
			return false;
		final StringValue rhs = (StringValue) obj;
		// compare strings
		if (values.length != rhs.values.length)
		    return false;
		for (int i = 0; i < values.length; ++i)
        {
            if (! values[i].equals( rhs.values[i]))
                return false;
        }
		return super.equals(obj);
	}

    /** {@inheritDoc} */
	@Override
	public final int hashCode()
	{
		return values.hashCode() + super.hashCode();
	}
}
