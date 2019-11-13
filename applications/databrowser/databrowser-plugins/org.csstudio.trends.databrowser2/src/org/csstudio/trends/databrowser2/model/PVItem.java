/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.diirt.datasource.ExpressionLanguage.newValuesOf;
import static org.diirt.datasource.vtype.ExpressionLanguage.vType;
import static org.diirt.util.time.TimeDuration.ofSeconds;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.archive.vtype.VTypeHelper;
import org.csstudio.swt.rtplot.util.NamedThreadFactory;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.imports.ImportArchiveReaderFactory;
import org.csstudio.trends.databrowser2.persistence.XMLPersistence;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.diirt.datasource.PVManager;
import org.diirt.datasource.PVReader;
import org.diirt.datasource.PVReaderEvent;
import org.diirt.datasource.PVReaderListener;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.w3c.dom.Element;


/** Data Browser Model Item for 'live' PV.
 *  <p>
 *  Holds both historic and live data in PVSamples.
 *  Performs the periodic scans of a control system PV.
 *  <p>
 *  Also implements IProcessVariable so that context menus
 *  can link to related CSS tools.
 *
 *  @author Kay Kasemir
 *  @author Takashi Nakamoto changed PVItem to handle waveform index.
 */
@SuppressWarnings("nls")
public class PVItem extends ModelItem implements PVReaderListener<List<VType>>
{
    /** Waveform Index */
    final private AtomicInteger waveform_index = new AtomicInteger(0);

    /** Historic and 'live' samples for this PV */
    private PVSamples samples = new PVSamples(waveform_index);

    /** Where to get archived data for this item. */
    private ArrayList<ArchiveDataSource> archives
        = new ArrayList<ArchiveDataSource>();

    /** Control system PV, set when running */
    private PVReader<List<VType>> pv = null;

    /** Most recently received value */
    private volatile VType current_value;

    /** Scan period in seconds, &le;0 to 'monitor' */
    private double period;

    /** Timer that was used to schedule the scanner */
    final private static ScheduledExecutorService scan_timer =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("DataBrowserScanner"));

    /** For a period &gt;0, this timer task performs the scanning */
    private ScheduledFuture<?> scanner = null;

    /** Archive data request type */
    private RequestType request_type = RequestType.OPTIMIZED;

    /** Indicating if the history data is automatically refreshed, whenever
     * the live buffer is too small to show all the data */
    private boolean automaticRefresh = Preferences.isAutomaticHistoryRefresh();

    /** Initialize
     *  @param name PV name
     *  @param period Scan period in seconds, &le;0 to 'monitor'
     *  @throws Exception on error
     */
    public PVItem(final String name, final double period) throws Exception
    {
        super(name);
        this.period = period;
    }

    /** @return Waveform index */
    @Override
    public int getWaveformIndex()
    {
        return waveform_index.get();
    }

    /** @param index New waveform index */
    @Override
    public void setWaveformIndex(int index)
    {
        if (index < 0)
            index = 0;
        if (waveform_index.getAndSet(index) != index)
            fireItemDataConfigChanged();
    }

    /** Set new item name, which changes the underlying PV name
     *  {@inheritDoc}
     */
    @Override
    public boolean setName(final String new_name) throws Exception
    {
        if (! super.setName(new_name))
            return false;
        // Stop PV, clear samples
        final boolean running = (pv != null);
        if (running)
            stop();
        samples.clear();
        // Create new PV, maybe start it
        if (running)
            start();
        return true;
    }

    /** @return Scan period in seconds, &le;0 to 'monitor' */
    public double getScanPeriod()
    {
        return period;
    }

    /** Update scan period.
     *  <p>
     *  When called on a running item, this stops and re-starts the PV.
     *  @param period New scan period in seconds, &le;0 to 'monitor'
     *  @throws Exception On error re-starting a running PVItem
     */
    public void setScanPeriod(double period) throws Exception
    {
        // Don't 'scan' faster than 1 Hz. Instead switch to on-change.
        if (period < 0.1)
            period = 0.0;
        final boolean running = (pv != null);
        if (running)
            stop();
        this.period = period;
        if (running)
            start();
        fireItemDataConfigChanged();
    }

    /** {@inheritDoc} */
    @Override
    void setModel(final Model model)
    {
        super.setModel(model);
        // Dis-associated from model? Then refresh is no longer required
        if (model == null)
            automaticRefresh = false;
        else // Otherwise use model's configuration
            automaticRefresh = model.isAutomaticHistoryRefresh();
    }

    /** @return Maximum number of live samples in ring buffer */
    public int getLiveCapacity()
    {
        return samples.getLiveCapacity();
    }

    /** Set new capacity for live sample ring buffer
     *  <p>
     *  @param new_capacity New sample count capacity
     *  @throws Exception on out-of-memory error
     */
    public void setLiveCapacity(final int new_capacity) throws Exception
    {
        samples.setLiveCapacity(new_capacity);
        fireItemDataConfigChanged();
    }

    /** @return Archive data sources for this item */
    public ArchiveDataSource[] getArchiveDataSources()
    {
        return (ArchiveDataSource[]) archives.toArray(new ArchiveDataSource[archives.size()]);
    }

    /** Replace archives with settings from preferences */
    public void useDefaultArchiveDataSources()
    {
        archives.clear();
        for (ArchiveDataSource arch : Preferences.getArchives())
            archives.add(arch);
        fireItemDataConfigChanged();
    }

    /** @param archive Archive data source
     *  @return <code>true</code> if PV uses given data source
     */
    public boolean hasArchiveDataSource(final ArchiveDataSource archive)
    {
        for (ArchiveDataSource arch : archives)
            if (arch.equals(archive))
                return true;
        return false;
    }

    /** @param archive Archive to add as a source to this item
     *  @throws Error when archive is already used
     */
    public void addArchiveDataSource(final ArchiveDataSource archive)
    {
        if (hasArchiveDataSource(archive))
            throw new Error("Duplicate archive " + archive);
        archives.add(archive);
        fireItemDataConfigChanged();
    }

    /** @param archive Archives to add as a source to this item. Duplicates are ignored */
    public void addArchiveDataSource(final ArchiveDataSource archs[])
    {
        boolean change = false;
        for (ArchiveDataSource archive : archs)
            if (! archives.contains(archive))
            {
                change = true;
                archives.add(archive);
            }
        if (change)
            fireItemDataConfigChanged();
    }

    /** @param archive Archive to remove as a source from this item. */
    public void removeArchiveDataSource(final ArchiveDataSource archive)
    {
        if (archives.remove(archive))
            fireItemDataConfigChanged();
    }

    /** @param archive Archives to remove as a source from this item. Ignored when not used. */
    public void removeArchiveDataSource(final ArchiveDataSource archs[])
    {
        boolean change = false;
        for (ArchiveDataSource archive : archs)
            if (archives.remove(archive))
                change = true;
        if (change)
            fireItemDataConfigChanged();
    }

    /** Replace existing archive data sources with given archives
     *  @param archs ArchiveDataSources to use for this item
     */
    public void setArchiveDataSource(final ArchiveDataSource archs[])
    {
        // Check if they are the same, i.e. count AND order match
        if (archs.length == archives.size())
        {
            boolean same = true;
            for (int i=0; i<archs.length; ++i)
                if (! archs[i].equals(archives.get(i)))
                {
                    same = false;
                    break;
                }
            if (same)
                return;
        }
        // Different archives
        archives.clear();
        for (ArchiveDataSource arch : archs)
            archives.add(arch);
        fireItemDataConfigChanged();
    }

    /** @return Archive data request type */
    public RequestType getRequestType()
    {
        return request_type;
    }

    /** @param request_type New request type */
    public void setRequestType(final RequestType request_type)
    {
        if (this.request_type == request_type)
            return;
        this.request_type = request_type;
        fireItemDataConfigChanged();
    }

    /** Notify listeners */
    private void fireItemDataConfigChanged()
    {
        if (model.isPresent())
            model.get().fireItemDataConfigChanged(this);
    }

    /** Connect control system PV, start scanning, ...
     *  @throws Exception on error
     */
    public void start() throws Exception
    {
        if (pv != null)
            throw new RuntimeException("Already started " + getName());
        pv = PVManager.read(newValuesOf(vType(getResolvedName()))).timeout(ofSeconds(30.0)).readListener(this).maxRate(ofSeconds(0.1));
        // Log every received value?
        if (period <= 0.0)
            return;
        // Start scanner for periodic log
        final long delay = (long) (period*1000);
        scanner = scan_timer.scheduleAtFixedRate(this::doScan, delay, delay, TimeUnit.MILLISECONDS);
    }

    /** Disconnect from control system PV, stop scanning, ... */
    public void stop()
    {
        if (pv == null)
            throw new RuntimeException("Not running " + getName());
        if (scanner != null)
        {
            scanner.cancel(true);
            scanner = null;
        }
        pv.close();
        pv = null;
    }

    /** {@inheritDoc} */
    @Override
    public PVSamples getSamples()
    {
        return samples;
    }

    /** {@inheritDoc} */
    @Override
    public void pvChanged(final PVReaderEvent<List<VType>> event)
    {
        final PVReader<List<VType>> pv = event.getPvReader();
        // Check for error
        final Exception error = pv.lastException();
        if (error != null)
            Activator.getLogger().log(Level.FINE, "PV " + pv.getName() + " error", error);

        final List<VType> values = pv.getValue();
        if (values == null)
        {   // No current value
            current_value = null;
            // In 'monitor' mode, mark in live sample buffer
            if (period <= 0)
                logDisconnected();
            return;
        }
        else
        {
            boolean added = false;
            for (VType value : values)
            {
                // Cache most recent for 'scanned' operation
                current_value = value;
                // In 'monitor' mode, add to live sample buffer
                if (period <= 0)
                {
                    Activator.getLogger().log(Level.FINE, "PV {0} received {1}", new Object[] { getName(), value });
                    samples.addLiveSample(value);
                    added = true;
                }
            }
            // Set units unless already defined
            if (getUnits() == null)
                updateUnits(current_value);
            if (automaticRefresh && added &&
                model.isPresent() &&
                samples.isHistoryRefreshNeeded(model.get().getStartTime(), model.get().getEndTime()))
                model.get().fireItemRefreshRequested(this);
        }
    }

    private void updateUnits(final VType value)
    {
        if (! (value instanceof Display))
            return;
        final Display display = (Display) value;
        setUnits(display.getUnits());
    }

    /** Scan, i.e. add 'current' value to live samples */
    private void doScan()
    {
        final VType value = current_value;
        Activator.getLogger().log(Level.FINE, "PV {0} scans {1}", new Object[] { getName(), value });
        if (value == null)
            logDisconnected();
        else
            // Transform value to have 'now' as time stamp
            samples.addLiveSample(VTypeHelper.transformTimestampToNow(value));
    }

    /** Add one(!) 'disconnected' sample */
    private void logDisconnected()
    {
        if (! samples.lockForWriting())
            return;
        try
        {
            final int size = samples.size();
            if (size > 0)
            {
                final String last =
                    VTypeHelper.getMessage(samples.get(size - 1).getVType());
                // Does last sample already have 'disconnected' status?
                if (Messages.Model_Disconnected.equals(last))
                    return;
            }
            samples.addLiveSample(new PlotSample(Messages.LiveData, Messages.Model_Disconnected));
        }
        finally
        {
            samples.unlockForWriting();
        }
    }

    /** Add data retrieved from an archive to the 'historic' section
     *  @param server_name Archive server that provided these samples
     *  @param new_samples Historic data
     */
    public void mergeArchivedSamples(final String server_name,
            final List<VType> new_samples)
    {
        final boolean need_refresh;
        if (! samples.lockForWriting())
            return;
        try
        {
            samples.mergeArchivedData(server_name, new_samples);
            need_refresh = automaticRefresh && model.isPresent() &&
                           samples.isHistoryRefreshNeeded(model.get().getStartTime(), model.get().getEndTime());
        }
        finally
        {
            samples.unlockForWriting();
        }
        if (need_refresh)
            model.get().fireItemRefreshRequested(this);
    }

    /** Write XML formatted PV configuration
     *  @param writer PrintWriter
     */
    @Override
    public void write(final PrintWriter writer)
    {
        XMLWriter.start(writer, 2, XMLPersistence.TAG_PV);
        writer.println();
        writeCommonConfig(writer);
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_SCAN_PERIOD, getScanPeriod());
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_LIVE_SAMPLE_BUFFER_SIZE, getLiveCapacity());
        XMLWriter.XML(writer, 3, XMLPersistence.TAG_REQUEST, getRequestType().name());
        for (ArchiveDataSource archive : archives)
        {
            XMLWriter.start(writer, 3, XMLPersistence.TAG_ARCHIVE);
            writer.println();
            XMLWriter.XML(writer, 4, XMLPersistence.TAG_NAME, archive.getName());
            XMLWriter.XML(writer, 4, XMLPersistence.TAG_URL, archive.getUrl());
            XMLWriter.XML(writer, 4, XMLPersistence.TAG_KEY, archive.getKey());
            XMLWriter.end(writer, 3, XMLPersistence.TAG_ARCHIVE);
            writer.println();
        }
        XMLWriter.end(writer, 2, XMLPersistence.TAG_PV);
        writer.println();
    }

    /** Create PVItem from XML document
     *  @param model Model to which this item will belong (but doesn't, yet)
     *  @param node XML node with item configuration
     *  @return PVItem
     *  @throws Exception on error
     */
    public static PVItem fromDocument(final Model model, final Element node) throws Exception
    {
        final String name = DOMHelper.getSubelementString(node, XMLPersistence.TAG_NAME);
        final double period = DOMHelper.getSubelementDouble(node, XMLPersistence.TAG_SCAN_PERIOD, 0.0);

        final PVItem item = new PVItem(name, period);
        final int buffer_size = DOMHelper.getSubelementInt(node, XMLPersistence.TAG_LIVE_SAMPLE_BUFFER_SIZE, Preferences.getLiveSampleBufferSize());
        item.setLiveCapacity(buffer_size);

        final String req_txt = DOMHelper.getSubelementString(node, XMLPersistence.TAG_REQUEST, RequestType.OPTIMIZED.name());
        try
        {
            final RequestType request = RequestType.valueOf(req_txt);
            item.setRequestType(request);
        }
        catch (Throwable ex)
        {
            // Ignore
        }

        item.configureFromDocument(model, node);

        // Load archives from saved configuration
        boolean have_imported_data = false;
        Element archive = DOMHelper.findFirstElementNode(node.getFirstChild(), XMLPersistence.TAG_ARCHIVE);
        while (archive != null)
        {
            final String url = DOMHelper.getSubelementString(archive, XMLPersistence.TAG_URL);
            final int key = DOMHelper.getSubelementInt(archive, XMLPersistence.TAG_KEY);
            final String arch = DOMHelper.getSubelementString(archive, XMLPersistence.TAG_NAME);

            if (url.startsWith(ImportArchiveReaderFactory.PREFIX))
                have_imported_data = true;

            item.addArchiveDataSource(new ArchiveDataSource(url, key, arch));
            archive = DOMHelper.findNextElementNode(archive, XMLPersistence.TAG_ARCHIVE);
        }

        // When requested, use default archive sources for 'real' archives (RDB, ...)
        // Do not clobber an imported archive data source, a specific file which was
        // probably not meant to be replaced by a default.
        if (Preferences.useDefaultArchives()  &&  !have_imported_data)
            item.useDefaultArchiveDataSources();

        return item;
    }
}
