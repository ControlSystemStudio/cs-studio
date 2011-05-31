/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

/** A series of samples. Basically one 'line' in the plot.
 *  <p>
 *  Random access to samples is an important design criterion
 *  of the plot library. User code needn't copy the data into
 *  a plot-specific array. Only random access via this interface
 *  is required.
 *  <p>
 *  Since the plot library iterates over the sample sequence whenever
 *  a redraw is required, while the application might receive new or
 *  changed samples at the same time, all users of the the sample sequence
 *  need to <b>synchronize</b> on the ChartSampleSequence instance they use.
 *  @see ChartSample
 *  @see ChartSampleSequenceContainer
 *  @author Kay Kasemir
 */
public interface ChartSampleSequence
{
    /** @return The number of samples in this sequence. */
    public int size();
    
    /** Random access to the samples of the sequence.
     *  <p>
     *  It is an error to use indices below 0 or 
     *  &gt;= size().
     *  @return The Sample of given index. */
    public ChartSample get(int i);
    
    /** In case the sample sequence has a default display range,
     *  return it, so that the plot can use it.
     *  Otherwise, return <code>null</code>, in which case the plot
     *  might auto-zoom.
     *  @return Default display range or null.
     */
    public Range getDefaultRange();
}
