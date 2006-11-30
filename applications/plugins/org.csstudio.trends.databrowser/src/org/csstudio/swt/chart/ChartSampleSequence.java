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
 *  need to <b>synchronize</b> on the SampleSequence instance they use.
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
}
