package org.csstudio.platform.data;

/** A double-typed value that also has a minimum and maximum,
 *  usually as the result of averaging or otherwise interpolating
 *  over raw samples.
 *  @see IDoubleValue
 *  @author Kay Kasemir
 */
public interface IMinMaxDoubleValue extends IDoubleValue
{
    /** @return Minimum of the original values. */
    public double getMinimum();

    /** @return Maximum of the original values. */
    public double getMaximum();
}