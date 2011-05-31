/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

/** Interface for one sample, i.e. one "point" on the chart.
 *  <p>
 *  The user of the chart must provide each sample via this interface.
 *  
 *  @see ChartSampleContainer
 *  @see ChartSampleSequence
 *  
 *  @author Kay Kasemir
 */
public interface ChartSample
{
    enum Type
    {
        /** A normal sample, plot it together with the rest. */
        Normal,
        
        /** A gap, terminating a "line", displayed as a single point.
         *  <p>
         *  Can be used with <code>y == Double.NEGATIVE_INFINITY</code>
         *  to mark a point on the x axis.
         */
        Point
    }
    
    /** @return One of the Type values. */
    public Type getType();
    
    /** @return The x value. */
    public double getX();

    /** Value for the vertical (Y) axis.
     *  <p>
     *  <code>Double.NEGATIVE_INFINITY</code> is used to indicate
     *  a value that should be on the horizontal axis,
     *  typically used together with a <code>Point</code> type
     *  and some Info to describe the sample.
     *  
     *  @return The y value.
     */
    public double getY();

    /** @return <code>true</code> if this sample has Y error (range) info.
     *  @see #getMinY()
     *  @see #getMaxY()
     */
    public boolean haveMinMax();
    
    /** @return Minimum y value in case there is an Y error (range).
     *  @see #haveMinMax()
     */
    public double getMinY();

    /** @return Maximum y value in case there is an Y error (range).
     *  @see #haveMinMax()
     */
    public double getMaxY();

    /** @return Any informational string that might work as e.g. a Tooltip. */
    public String getInfo();
}
