package org.csstudio.platform.data;

/** Base class for all control system values.
 *  <p>
 *  The <code>Value</code> handles all the other 'stuff' that comes with
 *  a control system value except for the actual value itself:
 *  <ul>
 *  <li>Time stamp
 *  <li>A severity code that indicates if the data reflects some sort of warning
 *      or error state.
 *  <li>A status string that explains the severity.
 *  <li>Meta data that might be useful for displaying the data.
 *  </ul>
 *  
 *  It also offers convenience routines for displaying values.
 *  In most cases, however, access to the actual data requires the specific
 *  subtypes <code>DoubleValue</code>, <code>StringValue</code> etc.
 *  
 *  @see DoubleValue
 *  @see IntegerValue
 *  @see StringValue
 *  @see EnumValue
 *  @author Kay Kasemir
 */
abstract public class Value
{
    /** Describe the data quality. 
     *  <p>
     *  Control system data can originate directly from a front-end controller,
     *  or from a data history archive that stored such front-end controller
     *  values.
     *  We consider this the 'original' data.
     *  <p>
     *  Mid-level data servers or history data servers might also offer
     *  processed data, which reduces several 'original' samples to for example
     *  an 'averaged' sample. For those processed values, the time stamp
     *  actually no longer matches one specific instance in time when the
     *  front-end controller obtained a sample. A plotting tool might therefore
     *  display those processed samples in a different way.
     */
    public enum Quality
    {
        /** This is a raw, original value. */
        Original,
        
        /** This value is the result of averaging over 'original' values. */
        Averaged,
        
        /** This is the minimum over several 'original' values */
        Minimum,
        
        /** This is the maximum over several 'original' values */
        Maximum,
    };
    
    /** Time stamp of this value. */
	private final ITimestamp time;
    
    /** Severity code of this value. */
    private final Severity severity;
    
    /** Status text for this value's severity. */
	private final String status;
    
    /** Meta data (may be null). */
    private final MetaData meta_data;
    
    private final Quality quality;

	/** Construct a new value.
	 *  @param time
	 *  @param status
	 *  @param severity
     *  @param meta_data
	 */
	public Value(ITimestamp time, Severity severity,
                 String status, MetaData meta_data)
	{
        this(time, severity, status, meta_data, Quality.Original);
	}
	
    /** Construct a new value.
     *  @param time
     *  @param status
     *  @param severity
     *  @param meta_data
     *  @param quality
     */
    public Value(ITimestamp time, Severity severity,
                 String status, MetaData meta_data,
                 Quality quality)
    {
        this.time = time;
        this.severity = severity;
        this.status = status;
        this.meta_data = meta_data;
        this.quality = quality;
    }

    
    /** Get the time stamp.
     *  @return The time stamp.
     */
    final public ITimestamp getTime()
    {   return time;   }   

	/** Get the severity info.
     *  @see Severity
     *  @see #getStatus()
     *  @return The severity info.
     */
	final public Severity getSeverity()
	{	return severity;	}
    
	/** Get the status text that might describe the severity.
     *  @see #getSeverity()
     *  @return The status string.
     */
	final public String getStatus()
	{	return status; 	 }
    
    /** Get the quality of this value.
     *  @see Quality
     *  @return The quality.
     */
    final public Quality getQuality()
    {   return quality; }

    /** Meta Data that helps with using the value, mostly for formatting.
     *  <p>
     *  It might be OK for some value types to only have <code>null</code>
     *  MetaData, while others might require a specific one like
     *  <code>NumericMetaData</code>.
     *  @return The Meta Data.
     */
    final public MetaData getMetaData()
    {   return meta_data;    }
    
    /** @see #format(Format, int) */
    public enum Format
    {
        /** Use all the MetaData information. */
        Default,
        
        /** If possible, use decimal representation. */
        Decimal,
        
        /** If possible, use exponential notation. */
        Exponential
    };

    /** Format the value as a string.
     *  <p>
     *  This means only the numeric or string value.
     *  Not the timestamp, not the severity and status.
     *  <p>
     *  @param how Detail on how to format.
     *  @param precision Might be used by some format types to select for example
     *                   the number of digits after the decimal point.
     *                   A precision of '-1' might select the default-precision
     *                   obtained from the MetaData.
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
