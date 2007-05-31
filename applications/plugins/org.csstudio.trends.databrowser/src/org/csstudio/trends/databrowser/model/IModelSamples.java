package org.csstudio.trends.databrowser.model;

import org.csstudio.swt.chart.ChartSampleSequence;

/** Interface to model samples.
 *  <p>
 *  Almost like the <code>ChartSampleSequence</code> interface,
 *  but providing each samples as a <code>ModelSample</code>,
 *  which has a bit more info than a <code>ChartSample</code>.
 *  @author Kay Kasemir
 */
public interface IModelSamples extends ChartSampleSequence
{
    /** Get one model sample.
     *  @see ChartSampleSequence#get(int)
     */
    public ModelSample get(int i);
}