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

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.ISeverity;

/** Implementation of the {@link IValue} interface.
 *  @author Kay Kasemir
 */
abstract public class Value implements IValue
{
    /** Time stamp of this value. */
	private final ITimestamp time;
    
    /** Severity code of this value. */
    private final ISeverity severity;
    
    /** Status text for this value's severity. */
	private final String status;
    
    /** Meta data (may be null). */
    private final IMetaData meta_data;
    
    /** The data quality. */
    private final Quality quality;

    /** Construct a new value from pieces. */
    public Value(ITimestamp time, ISeverity severity,
                 String status, IMetaData meta_data,
                 Quality quality)
    {
        this.time = time;
        this.severity = severity;
        this.status = status;
        this.meta_data = meta_data;
        this.quality = quality;
    }
    
    /** {@inheritDoc} */
    final public ITimestamp getTime()
    {   return time;   }   

    /** {@inheritDoc} */
	final public ISeverity getSeverity()
	{	return severity;	}
    
    /** {@inheritDoc} */
	final public String getStatus()
	{	return status; 	 }
    
    /** {@inheritDoc} */
    final public Quality getQuality()
    {   return quality; }

    /** {@inheritDoc} */
    final public IMetaData getMetaData()
    {   return meta_data;    }
    
    /** {@inheritDoc} */
    abstract public String format(Format how, int precision);
    
    /** {@inheritDoc} */
	final public String format()
    {   return format(Format.Default, -1); }
    
    /** {@inheritDoc} */
    @Override
    final public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getTime().toString());
        buffer.append(Messages.ColumnSeperator);
        buffer.append(format());
        String sevr = getSeverity().toString();
        String stat = getStatus();
        if (sevr.length() > 0 || stat.length() > 0)
        {
            buffer.append(Messages.ColumnSeperator);
            buffer.append(sevr);
            buffer.append(Messages.SevrStatSeparator);
            buffer.append(stat);
        }
        return buffer.toString();
    }

    /** {@inheritDoc} */
	@Override
	public boolean equals(final Object obj)
	{
		if (! (obj instanceof Value))
			return false;
		final Value rhs = (Value) obj;
		if (! (rhs.time.equals(time) &&
		       rhs.quality == quality &&
			   rhs.status.equals(status) &&
			   rhs.severity.toString().equals(severity.toString())))
			   return false;
        // Meta_data might be null
        final IMetaData rhs_meta = rhs.getMetaData();
		if (meta_data == null)
		{   // OK if both are null
		    return rhs_meta == null;
		}
		return rhs.meta_data.equals(meta_data);
	}

    /** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return time.hashCode() + status.hashCode() + severity.hashCode();
	}
}
