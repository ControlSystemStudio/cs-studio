package org.csstudio.platform.data;

import org.csstudio.platform.internal.data.NumericMetaData;

/** An integer value.
 *  <p>
 *  {@link IIntegerValue} values go with {@link NumericMetaData}
 *  @see IValue
 *  @see NumericMetaData
 *  @author Kay Kasemir
 */
public interface IIntegerValue extends IValue
{
    /** @return Returns the whole array of values. */
    public int[] getValues();

    /** @return Returns the first array element.
     *  <p>
     *  Since most samples are probably scalars, this is a convenient
     *  way to get that one and only element.
     *  @see #getValues
     */
    public int getValue();
}