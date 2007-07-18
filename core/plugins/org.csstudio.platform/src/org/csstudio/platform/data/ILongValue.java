package org.csstudio.platform.data;

import org.csstudio.platform.internal.data.NumericMetaData;

/** A (long) integer value.
 *  <p>
 *  {@link ILongValue} values go with {@link NumericMetaData}
 *  @see IValue
 *  @see NumericMetaData
 *  @author Kay Kasemir
 */
public interface ILongValue extends IValue
{
    /** @return Returns the whole array of values. */
    public long[] getValues();

    /** @return Returns the first array element.
     *  <p>
     *  Since most samples are probably scalars, this is a convenient
     *  way to get that one and only element.
     *  @see #getValues
     */
    public long getValue();
}