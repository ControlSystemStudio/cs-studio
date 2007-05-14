package org.csstudio.platform.data;

/** A double-typed value.
 *  @see IValue
 *  @author Kay Kasemir
 */
public interface IDoubleValue extends IValue
{
    /** @return Returns the whole array of values. */
    public double[] getValues();

    /** @return Returns the first array element.
     *  <p>
     *  Since most values are probably scalars, this is a convenient
     *  way to get that one and only element.
     *  @see #getValues
     */
    public double getValue();
}