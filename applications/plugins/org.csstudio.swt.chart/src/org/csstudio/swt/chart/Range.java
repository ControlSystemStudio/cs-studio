/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

/** A value range, low...high.
 *  <p>
 *  In principle, it's obviously often nicer to pass a min...max range
 *  of values around as a Range object instead of having to deal with
 *  individual doubles.
 *  But there is one catch:
 *  When some object hands out a reference to its range, something like
 *  axis.getValueRange(), then there is the danger that the caller might
 *  change that range and thus affect the internal state of the axis,
 *  instead of going though a designated setValueRange() or setValueMin()
 *  method which would allow the axis to react to the change...
 *  
 *  @author Kay Kasemir
 */
public class Range
{
    /** The range */
    private double low, high;

    /** Construct range with given low and high ends. */
    public Range(double low, double high)
    {
        this.low = low;
        this.high = high;
    }

    /** @return low end of range */
    public double getLow()
    {
        return low;
    }

    /** @param low New low end. */
    public void setLow(double low)
    {
        this.low = low;
    }

    /** @return high end of range */
    public double getHigh()
    {
        return high;
    }

    /** @param high New high end. */
    public void setHigh(double high)
    {
        this.high = high;
    }

    /** Compare range to other range
     *  @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        Range other = (Range) obj;
        return other.low == low  &&  other.high == high;
    }

    /** Convert range to printable string. */
    @Override
    public String toString()
    {
        return low + " ... " + high; //$NON-NLS-1$
    }
}
