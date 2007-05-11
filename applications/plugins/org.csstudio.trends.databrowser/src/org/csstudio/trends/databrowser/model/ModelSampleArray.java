package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveValues;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Range;
import org.csstudio.value.Value;

/** An array of <code>ModelSample</code>.
 *  @author Kay Kasemir
 */
public class ModelSampleArray extends ArrayList<ModelSample>
      implements ChartSampleSequence
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
     *  @param arch_samples Samples obtained from ArchiveServer
     *  @param border Timestamp where to end, may be <code>null</code>.
     *  @return New ModelSampleArray or <code>null</code> if there was no data.
     */
    static ModelSampleArray fromArchivedSamples(ArchiveValues arch_samples,
                    ITimestamp border)
    {
        // Anything?
        if (arch_samples == null)
            return null;
        Value as[] = arch_samples.getSamples();
        // Any samples?
        if (as.length < 1)
            return null;
        ModelSampleArray model_samples = new ModelSampleArray(as.length);
        for (int i=0; i<as.length; ++i)
        {   
            Value sample = as[i];
            // Stop at border
            if (border != null  &&  sample.getTime().isGreaterThan(border))
                break;
            model_samples.add(new ModelSample(sample)); 
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
