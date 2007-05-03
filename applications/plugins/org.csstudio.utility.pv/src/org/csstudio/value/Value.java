package org.csstudio.value;

import org.csstudio.platform.util.ITimestamp;

/** Base class for all values.
 *  @see DoubleValue
 *  @see IntegerValue
 *  @see StringValue
 *  @see EnumValue
 *  @author Kay Kasemir
 */
abstract public class Value
{
	private final ITimestamp time;
    private final Severity severity;
	private final String status;
    private final MetaData meta_data;

	/** Construct a new value.
	 *  @param time
	 *  @param status
	 *  @param severity
     *  @param meta_data
	 */
	public Value(ITimestamp time, Severity severity,
                  String status, MetaData meta_data)
	{
		this.time = time;
        this.severity = severity;
		this.status = status;
        this.meta_data = meta_data;
	}
	
    /** @return The time stamp. */
    final public ITimestamp getTime()
    {   return time;   }   

	/** @return The severity info. */
	final public Severity getSeverity()
	{	return severity;	}
    
	/** @return The status string. */
	final public String getStatus()
	{	return status; 	 }
    
    public enum Type
    {
        /** This is a plain value */
        Plain,
        /** This value is the result of averging over plain values. */
        Averaged,
        /** This is the minimum over several plain values */
        Minimum,
        /** This is the maximum over several plain values */
        Maximum,
    };
    // TODO Add a type
    // When a sample is obtained for example from an archive data server,
    // one might get reduced data.
    // The 'type' tells us if we're looking at a plain or 'raw' sample,
    // or such a pre-digested sample.
    // public Type getType();

    /** Meta Data that helps with using the value, mostly for formatting.
     *  <p>
     *  It might be OK for some value types to only have <code>null</code>
     *  MetaData, while others might require a specific one like
     *  <code>NumericMetaData</code>
     *  @return The Meta Data.
     */
    final public MetaData getMetaData()
    {   return meta_data;    }
    
    /** @see #format(Format, int) */
    public enum Format
    {
        /** Use all the MetaData information. */
        Default,
        /** Use decimal representation. */
        Decimal,
        /** Use exponential notation. */
        Exponential
    };

    /** Format the value as a string.
     *  <p>
     *  This means only the numeric or string value.
     *  Not the timestamp, not the severity and status.
     *  <p>
     *  @param how Detail on how to format.
     *  @param precision Might be used by some format types.
     *  @return This Value's value as a string.
     *  @see #toString() 
     */
    abstract public String format(Format how, int precision);
    
    /** Format the value via the Default format.
     *  Typically this means: using the meta data.
     *  <p>
     *  @return This Value's value as a string.
     *  @see #format(Format, int)
     *  @see #toString() 
     */
	final public String format()
    {   return format(Format.Default, -1); }
    
    /** Format the Value as a string.
     *  <p>
     *  This includes the time stamp, numeric or string value,
     *  severity and status.
     *  <p>
     *  @return This Value as a string.
     *  @see #format() 
     */
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

    /* @see Object#equals() */
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

	/* Who overrides equals() shall also provide hashCode...
     * @see Object#hashCode() 
     */
	@Override
	public int hashCode()
	{
		return time.hashCode() + status.hashCode() + severity.hashCode();
	}
}
