package org.csstudio.trends.databrowser.model;

import org.csstudio.archive.ArchiveSamples;
import org.csstudio.archive.DoubleSample;
import org.csstudio.archive.util.TimestampUtil;
import org.csstudio.platform.util.ITimestamp;
import org.csstudio.platform.util.TimestampFactory;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.utility.pv.PVValue;
import org.eclipse.swt.widgets.Display;

/** Samples of a model item, combination of archived and live samples,
 *  appears as one long ChartSampleSequence.
 *  <p>
 *  Note the synchronize comments of the ChartSampleSequence class.
 *  Users of ModelItemSamples must synchronize on it.
 *  @author Kay Kasemir
 *  @see ChartSampleSequence
 */
public class ModelSamples implements ChartSampleSequence
{
    /** The 'archived' samples for this item.
     *  Read from the GUI thread, but updated from an archive reader thread.
     */
    private volatile ModelSampleArray archive_samples;

    /** All the 'live' samples we collected for this item.
     *  Usually accessed from the UI thread for plotting,
     *  but also archive reader when getting the 'border'.
     */
    private volatile ModelSampleRing live_samples;

    /** Construct with given initial 'live' buffer size */
    ModelSamples(int ring_size)
    {
        archive_samples = null;
        live_samples = new ModelSampleRing(ring_size);
    }
    
    /** Should be called for cleanup. */
    synchronized void dispose()
    {
        live_samples.dispose();
    }

    /** Change the number of 'live' samples. */
    synchronized void setLiveCapacity(int size)
    {
        live_samples.setCapacity(size);
    }
    
    /** Add samples from an archive. */
    @SuppressWarnings("nls")
    synchronized void add(ArchiveSamples samples)
    {
        // To prevent archived samples from overlapping 'live' data,
        // use only archived samples only until reaching the 'border':
        ITimestamp border;
        if (live_samples.size() > 0)
            border = 
                TimestampUtil.fromDouble(live_samples.get(0).getX());
        else
            border = TimestampFactory.now();
        // One could consider unlocking 'this' here,
        // because the copy takes some time, so we don't have
        // to synchonize...
        ModelSampleArray new_samples =
            ModelSampleArray.fromArchivedSamples(samples, border);
        // ... and then sync again when we update the archive_samples:
        archive_samples =
            ModelSampleMerger.merge(archive_samples, new_samples);
    }
    
    /** Marks the end of the 'live' buffer as currently disconnected. */
    synchronized void markCurrentlyDisconnected(ITimestamp now)
    {
        /** Add one(!) last 'end' sample. */
        int size = live_samples.size();
        if (size > 0)
        {
            ChartSample last_sample = live_samples.get(size - 1);
            // If the last sample is already a 'disconnected' message, we're done.
            String info = last_sample.getInfo();
            if (info != null && info.equals(Messages.LivePVDisconnected))
                return;
        }
        
        DoubleSample disconnected = new DoubleSample(now,
                        SeverityUtil.getInvalid(Messages.Sevr_INVALID),
                        Messages.LivePVDisconnected,
                        MetaDataUtil.getNumeric(),
                        new double[] {Double.NEGATIVE_INFINITY });
        
        live_samples.add(disconnected);
    }
    
    /** Add most recent timestamp/value */
    synchronized void addLiveSample(ITimestamp now,
                    Object value,
                    int    severity_code,
                    String severity,
                    String status)
    {
        // We expect all access to this method from the UI thread.
        if (Display.getCurrent() == null)
            throw new Error("Accessed from non-UI thread"); //$NON-NLS-1$
        try
        {
            // TODO: Get the real MetaData
            double values[] = new double[] { PVValue.toDouble(value) };
            DoubleSample sample = new DoubleSample(now,
                            SeverityUtil.get(severity_code, severity),
                            status,
                            MetaDataUtil.getNumeric(),
                            values);
            live_samples.add(sample);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /** @see ChartSampleSequence */
    synchronized public ModelSample get(int i)
    {
        if (archive_samples != null)
        {
            if (i < archive_samples.size())
                return archive_samples.get(i);
            i -= archive_samples.size();
        }
        return live_samples.get(i);
    }

    /** @see ChartSampleSequence */
    synchronized public int size()
    {
        int size = live_samples.size();
        if (archive_samples != null)
            size += archive_samples.size();
        return size;
    }
}