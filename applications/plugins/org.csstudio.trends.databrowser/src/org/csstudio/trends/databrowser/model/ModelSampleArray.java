package org.csstudio.trends.databrowser.model;

import java.util.ArrayList;

import org.csstudio.archive.ArchiveSamples;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.swt.chart.ChartSampleSequence;

/** An array of <code>ModelSample</code>.
 *  @author Kay Kasemir
 */
public class ModelSampleArray extends ArrayList<ModelSample> implements ChartSampleSequence
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
    static ModelSampleArray fromArchivedSamples(ArchiveSamples arch_samples,
                    ITimestamp border)
    {
        // Anything?
        if (arch_samples == null)
            return null;
        org.csstudio.archive.Sample as[] = arch_samples.getSamples();
        // Any samples?
        if (as.length < 1)
            return null;
        double border_x;
        if (border != null)
            border_x = border.toDouble();
        else
            border_x = Double.MAX_VALUE;
        ModelSampleArray model_samples = new ModelSampleArray(as.length);
        for (int i=0; i<as.length; ++i)
        {   // Convert 'archive' samples into 'model' samples
            ModelSample s = ModelSample.fromArchiveSample(as[i]);
            // .. until border
            if (s.getX() > border_x)
                break;
            model_samples.add(s); 
        }
        if (model_samples.size() <= 0)
            return null;
        return model_samples;
    }
    
    // SampleSequence.get(int i) and size() are implemented by the ArrayList...
}
