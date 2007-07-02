package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.swt.chart.Range;

/** An array-based implementation of <code>IModelSamples</code>.
 *  @author Kay Kasemir
 */
public class ModelSampleArray extends ArrayList<ModelSample>
      implements IModelSamples
{
    /** Required ID for serialization... */
    private static final long serialVersionUID = 1L;
    
    ModelSampleArray()
    {
        super();
    }

    ModelSampleArray(int capacity)
    {
        super(capacity);
    }
    
    /** Create ModelSampleArray from ArchiveSamples, stopping at the border.
     *  @param source Name of the data source
     *  @param samples Samples obtained from source
     *  @param border Timestamp where to end, may be <code>null</code>.
     *  @return New ModelSampleArray or <code>null</code> if there was no data.
     */
    @SuppressWarnings("nls")
    static ModelSampleArray fromArchivedSamples(
                    final String source,
                    final IValue samples[],
                    ITimestamp border)
    {
        // Anything?
        if (samples == null)
            throw new IllegalArgumentException("null samples");
        // Any samples?
        if (samples.length < 1)
            throw new IllegalArgumentException("only " + samples.length + " samples");
        final ModelSampleArray model_samples =
            new ModelSampleArray(samples.length);
        for (IValue sample : samples)
        {   
            // Stop at border
            if (border != null  &&  sample.getTime().isGreaterThan(border))
                break;
            model_samples.add(new ModelSample(sample, source)); 
        }
        if (model_samples.size() <= 0)
            return null;
        return model_samples;
    }
    
    // ChartSampleSequence.get(int i) and size() are 
    // implemented by ArrayList<ModelSample> ...
    
    // @see ChartSampleSequence
    public Range getDefaultRange()
    {   // Not really a useful implementation,
        // but what counts is the one in the ModelSamples class.
        return null;
    }
}
