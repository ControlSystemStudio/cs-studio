package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveValues;
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
        IValue as[] = arch_samples.getSamples();
        // Any samples?
        if (as.length < 1)
            return null;
        // Use server's name as sample source info
        final String source = arch_samples.getArchiveServer().getServerName();
        ModelSampleArray model_samples = new ModelSampleArray(as.length);
        for (int i=0; i<as.length; ++i)
        {   
            IValue sample = as[i];
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
