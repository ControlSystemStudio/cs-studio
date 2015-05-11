package org.csstudio.diag.postanalyser.model;

import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.TraceType;

/** Output of an <code>Algorithm</code>
 *  @author Kay Kasemir
 */
public class AlgorithmOutput
{
    final String name;
    final ChartSampleSequence samples;
    final TraceType trace_type;
    
    /** Construct output
     *  @param name Short description of this output
     *  @param samples X/Y samples
     *  @param trace_type How to display
     */
    AlgorithmOutput(final String name, final ChartSampleSequence samples,
            final TraceType trace_type)
    {
        this.name = name;
        this.samples = samples;
        this.trace_type = trace_type;
    }

    /** @return Short description of this output */
    public String getName()
    {
        return name;
    }
    
    /** @return samples X/Y samples */
    public ChartSampleSequence getSamples()
    {
        return samples;
    }
    
    /** @return How to display */
    public TraceType getTraceType()
    {
        return trace_type;
    }
}
