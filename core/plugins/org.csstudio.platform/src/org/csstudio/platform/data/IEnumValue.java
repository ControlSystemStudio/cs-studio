package org.csstudio.platform.data;

import org.csstudio.platform.internal.data.EnumeratedMetaData;

/** An enumerated value.
 *  <p>
 *  Enumerated types carry a limited number of integer values,
 *  where each possible value represents a state with a string representation.
 *  <p>
 *  {@link IEnumValue} values go with {@link EnumeratedMetaData}
 *  @see IValue
 *  @see EnumeratedMetaData
 *  @author Kay Kasemir
 */
public interface IEnumValue extends IValue
{
    /** @return Returns the whole array of values. */
    public int[] getValues();

    /** @return Returns the first array element.
     *  <p>
     *  Since most values are probably scalars, this is a convenient
     *  way to get that one and only element.
     *  @see #getValues
     */
    public int getValue();
}