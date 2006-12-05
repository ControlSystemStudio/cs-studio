package org.csstudio.trends.databrowser.exportview;

import org.csstudio.archive.Sample;
import org.csstudio.archive.crawl.SampleIterator;
import org.csstudio.trends.databrowser.model.ModelSamples;

/** Facade that turns ModelSamples into a Sample Iterator.
 *  <p>
 *  Access to the ModelSamples must be synchronized.
 *  But we don't want to lock the samples for the whole export process.
 *  We assume that the samples will only be extended with new 'live' samples
 *  during export, so we access them with minimal synchronization,
 *  only locking within the hasNext() and next() calls, not for the whole
 *  iteration, hoping that the worst that can happen: You miss a few of the
 *  very latest samples.
 *  <p>
 *  This approach will fail if an iteration runs while new archived data is
 *  added!
 *  
 *  @author Kay Kasemir
 */
public class ModelSampleIterator implements SampleIterator
{
    private final ModelSamples samples;
    private int i;
    
    ModelSampleIterator(ModelSamples samples)
    {
        this.samples = samples;
        i = 0;
    }
    
    public boolean hasNext()
    {
        synchronized (samples)
        {
            return i < samples.size();
        }
    }

    /** @return Next sample.
     *  In the hopefully unlikely case that the ModelSample array
     *  got smaller, we will get <code>null</code>.
     */
    public Sample next()
    {
        Sample sample;
        synchronized (samples)
        {
            if (i < samples.size())
                sample = samples.get(i++).getSample();
            else
                sample = null;
        }
        return sample;
    }

    /** @throws UnsupportedOperationException */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
