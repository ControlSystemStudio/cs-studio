package org.csstudio.trends.databrowser.model;

import org.csstudio.platform.data.IDoubleValue;
import org.csstudio.platform.data.IMetaData;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Range;

/** Samples of a model item, combination of archived and live samples,
 *  appears as one long ChartSampleSequence.
 *  <p>
 *  (IModelSamples includes ChartSampleSequence)
 *  <p>
 *  <b>Note the synchronize comments of the ChartSampleSequence class.</b>
 *  Most ModelSamples methods synchronize themselves, but user code
 *  that iterates over samples should still lock the ModelSamples
 *  to prevent changes to the data while calling size() and get(i).
 *  @author Kay Kasemir
 *  @see ChartSampleSequence
 */
public class ModelSamples implements IModelSamples // , ChartSampleSequence
{
    /** The 'archived' samples for this item.
     *  Read from the GUI thread, but updated from an archive reader thread.
     *  <p>
     *  Never null.
     */
    private ModelSampleArray archive_samples;

    /** All the 'live' samples we collected for this item.
     *  Usually accessed from the UI thread for plotting,
     *  but also archive reader when getting the 'border'.
     *  <p>
     *  Never null.
     */
    private ModelSampleRing live_samples;

    private static final INumericMetaData dummy_numeric_meta =
        ValueFactory.createNumericMetaData(0, 0, 0, 0, 0, 0, 0, ""); //$NON-NLS-1$

    /** Construct with given initial 'live' buffer size */
    ModelSamples(int ring_size)
    {
        archive_samples = new ModelSampleArray();
        live_samples = new ModelSampleRing(ring_size);
    }
    
    /** Change the number of 'live' samples.
     *  @param size Requested sample count size
     *  @throws Exception on error
     */
    void setLiveCapacity(int size) throws Exception
    {
        synchronized (this)
        {
            live_samples.setCapacity(size);
        }
    }
    
    /** Remove all samples */
    void clear()
    {
        synchronized (this)
        {
            live_samples.clear();
            archive_samples.clear();
        }
    }
    
    /** Add samples from an archive. */
    void addArchiveSamples(final String source, final IValue samples[])
    {
        // Prevent archived samples from overlapping 'live' data;
        // only archived samples only until reaching the 'border'.
        ITimestamp border;
        synchronized (this)
        {
            if (live_samples.size() > 0)
                border = 
                    TimestampFactory.fromDouble(live_samples.get(0).getX());
            else
                border = TimestampFactory.now();
        }
        // Unlock while converting new samples to ModelSampleArray
        final ModelSampleArray new_samples =
            ModelSampleArray.fromArchivedSamples(source, samples, border);
        synchronized (this)
        {
            archive_samples =
                        ModelSampleMerger.merge(archive_samples, new_samples);
        }
    }
    
    /** Marks the end of the 'live' buffer as currently disconnected. */
    void markCurrentlyDisconnected(final ITimestamp now)
    {
        // Add one(!) last 'end' sample.
        synchronized (this)
        {
            final int size = live_samples.size();
            if (size > 0)
            {
                final String last =
                    live_samples.get(size - 1).getSample().getStatus();
                // Does last sample already have 'disconnected' status?
                if (last != null && last.equals(Messages.LivePVDisconnected))
                    return;
            }
            final IDoubleValue disconnected = ValueFactory.createDoubleValue(
                            now,
                            ValueFactory.createInvalidSeverity(),
                            Messages.LivePVDisconnected,
                            dummy_numeric_meta,
                            IValue.Quality.Original,
                            new double[] { Double.NEGATIVE_INFINITY });
            addLiveSample(disconnected);
        }
    }
    
    /** Add most recent timestamp/value */
    void addLiveSample(final IValue value)
    {
        synchronized (this)
        {
            // Prevent live data with old time stamps from going back
            // into archived data.
            final int N = archive_samples.size();
            if (N > 0)
            {
                final IValue last_arch = archive_samples.get(N-1).getSample();
                if (last_arch.getTime().isGreaterThan(value.getTime()))
                        return;
            }
            live_samples.add(value, Messages.LiveSample);
        }
    }
    
    /** {@inheritDoc} */
    public ModelSample get(int i)
    {
        synchronized (this)
        {
            final int arch_size = archive_samples.size();
            if (i < arch_size)
            {
                final ModelSample sample = archive_samples.get(i);
                // Patch the last 'archive' sample to indicate
                // that it's the end of historic samples.
                // Note that the original sample is kept unchanged,
                // since after the next archive request, it might no
                // longer be the 'last' one.
                // We only morph it right here and now, temporarily.
                if (i == arch_size - 1)
                    return new ModelSampleMorpher(sample,
                                    ChartSample.Type.Point,
                                    Messages.LastArchivedSample);
                return sample;
            }
            i -= arch_size;
            return live_samples.get(i);
        }
    }

    /** {@inheritDoc} */
    public int size()
    {
        synchronized (this)
        {
            return archive_samples.size() + live_samples.size();
        }
    }
    
    /** {@inheritDoc} */
    public Range getDefaultRange()
    {
        // Get the default display range as suggested by the sample's meta data.
        // Simply checks the most recent sample.
        // If nothing is available (no samples, or non-numeric samples),
        // null is returned.
        synchronized (this)
        {
            final int len = size();
            if (len > 0)
            {
                IMetaData meta = get(len-1).getSample().getMetaData();
                if (meta instanceof INumericMetaData)
                {
                    INumericMetaData numeric = (INumericMetaData)meta;
                    return new Range(numeric.getDisplayLow(),
                                     numeric.getDisplayHigh());
                }
            }
        }
        return null;
    }
}