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
	public boolean equals(Object obj)
	{
		if (! (obj instanceof Value))
			return false;
		Value rhs = (Value) obj;
		return rhs.time.equals(time) &&
			rhs.status.equals(status) &&
			rhs.severity == severity &&
            rhs.meta_data.equals(meta_data);
	}

    /** {@inheritDoc} */
	@Override
	public int hashCode()
	{
		return time.hashCode() + status.hashCode() + severity.hashCode();
	}
}
