package org.csstudio.platform.data;

/** The numeric version of {@link IMetaData}.
 *  <p>
 *  Contains information that might be useful for displaying numeric values.
 *  
 *  @see IMetaData
 *  @see IValue
 *  @author Kay Kasemir 
 */
public interface INumericMetaData extends IMetaData
{
    /** @return Suggested lower display limit. */
    public double getDisplayLow();

    /** @return Suggested upper display limit. */
    public double getDisplayHigh();

    /** @return Low warning limit. */
    public double getWarnLow();

    /** @return High warning limit. */
    public double getWarnHigh();

    /** @return Low alarm limit. */
    public double getAlarmLow();

    /** @return High alarm limit. */
    public double getAlarmHigh();

    /** @return Suggested display precision (fractional digits). */
    public int getPrecision();

    /** @return The engineering units string. */
    public String getUnits();
}