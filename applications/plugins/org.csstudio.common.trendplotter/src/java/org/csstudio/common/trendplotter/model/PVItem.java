/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.imports.ImportArchiveReaderFactory;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.ILongValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.csstudio.domain.desy.epics.name.EpicsNameSupport;
import org.csstudio.domain.desy.epics.name.RecordField;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.swt.widgets.Display;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 */
public class PVItem extends ModelItem implements PVListener {
    private static final Logger LOG = LoggerFactory.getLogger(PVItem.class);

    /** Historic and 'live' samples for this PV */
    private final PVSamples samples;

    /** Where to get archived data for this item. */
    private final ArrayList<ArchiveDataSource> archives = new ArrayList<ArchiveDataSource>();

    /** Control system PV */
    private PV pv;
    private PV pv_deadband = null;

    /** Most recently received value */
    private volatile IValue current_value;

    /** Scan period in seconds, &le;0 to 'monitor' */
    private double _period;

    /** Timer that was used to schedule the scanner */
    private Timer scan_timer = null;

    /** For a period &gt;0, this timer task performs the scanning */
    private TimerTask scanner = null;

    /** Archive data request type */
    private RequestType request_type = RequestType.OPTIMIZED;

    /** Internal flag to store the 'on first connection/value update' info */
    private boolean first_pv_update = true;

    private boolean _minMaxFromFile;

    /** Waveform Index */
    private int waveform_index = 0;

    /** Initialize
     *  @param name PV name
     *  @param period Scan period in seconds, &le;0 to 'monitor'
     *  @throws Exception on error
     */
    public PVItem(@Nonnull final String name, final double period) throws Exception {
        super(name);
        samples = new PVSamples(request_type, new IIntervalProvider() {
            @Override
            @CheckForNull
            public Interval getTimeInterval() {
                final TimeInstant startTime = getModelStartTime();
                final TimeInstant endTime = getModelEndTime();
                if (startTime == null || endTime == null) {
                    return null;
                }
                return new Interval(startTime.getInstant(), endTime.getInstant());
            }

            private TimeInstant getModelStartTime() {
                final Model m = getModel();
                if (model != null) {
                    return BaseTypeConversionSupport.toTimeInstant(m.getStartTime());
                }
                return null;
            }

            private TimeInstant getModelEndTime() {
                final Model m = getModel();
                if (model != null) {
                    return BaseTypeConversionSupport.toTimeInstant(m.getEndTime());
                }
                return null;
            }
        });
        pv = PVFactory.createPV(name);
        _period = period;
    }

    /** @return Waveform index */
    @Override
    public int getWaveformIndex()
    {
        return waveform_index;
    }

    /** @param index New waveform index */
    @Override
    public void setWaveformIndex(int index)
    {
        if (index < 0)
            index = 0;
        if (index == waveform_index)
            return;
        waveform_index = index;

        // change all the index of samples in this instance
        samples.setWaveformIndex(waveform_index);

        fireItemLookChanged();
    }

    /** Set new item name, which changes the underlying PV name
     *  {@inheritDoc}
     */
    @Override
    public boolean setName(final String new_name) throws Exception {
        if (!super.setName(new_name)) {
            return false;
        }
        // Stop PV, clear samples
        final boolean running = pv.isRunning();
        if (running) {
            stop();
        }
        samples.clear();
        // Create new PV, maybe start it
        if (running) {
            start(scan_timer);
        }
        return true;
    }

    /** @return Scan period in seconds, &le;0 to 'monitor' */
    public double getScanPeriod() {
        return _period;
    }

    /** Update scan period.
     *  <p>
     *  When called on a running item, this stops and re-starts the PV.
     *  @param period New scan period in seconds, &le;0 to 'monitor'
     *  @throws Exception On error re-starting a running PVItem
     */
    public void setScanPeriod(double period) throws Exception {
        // Don't 'scan' faster than 1 Hz. Instead switch to on-change.
        if (period < 0.1) {
            period = 0.0;
        }
        final boolean running = pv.isRunning();
        if (running) {
            stop();
        }
        _period = period;
        if (running) {
            start(scan_timer);
        }
        fireItemLookChanged();
    }

    /** @return Maximum number of live samples in ring buffer */
    public int getLiveCapacity() {
        return samples.getLiveCapacity();
    }

    /** Set new capacity for live sample ring buffer
     *  <p>
     *  @param new_capacity New sample count capacity
     *  @throws Exception on out-of-memory error
     */
    public void setLiveCapacity(final int new_capacity) throws Exception {
        samples.setLiveCapacity(new_capacity);
        fireItemLookChanged();
    }

    /** @return Archive data sources for this item */
    public ArchiveDataSource[] getArchiveDataSources() {
        return archives.toArray(new ArchiveDataSource[archives.size()]);
    }

    /** Replace archives with settings from preferences */
    public void useDefaultArchiveDataSources() {
        archives.clear();
        for (final ArchiveDataSource arch : Preferences.getArchives()) {
            archives.add(arch);
        }
        fireItemDataConfigChanged();
    }

    /** @param archive Archive data source
     *  @return <code>true</code> if PV uses given data source
     */
    public boolean hasArchiveDataSource(final ArchiveDataSource archive) {
        for (final ArchiveDataSource arch : archives) {
            if (arch.equals(archive)) {
                return true;
            }
        }
        return false;
    }

    /** @param archive Archive to add as a source to this item
     *  @throws Error when archive is already used
     */
    @SuppressWarnings("nls")
    public void addArchiveDataSource(final ArchiveDataSource archive) {
        if (hasArchiveDataSource(archive)) {
            throw new Error("Duplicate archive " + archive);
        }
        archives.add(archive);
        fireItemDataConfigChanged();
    }

    /** @param archive Archives to add as a source to this item. Duplicates are ignored */
    public void addArchiveDataSource(final ArchiveDataSource archs[]) {
        boolean change = false;
        for (final ArchiveDataSource archive : archs) {
            if (!archives.contains(archive)) {
                change = true;
                archives.add(archive);
            }
        }
        if (change) {
            fireItemDataConfigChanged();
        }
    }

    /** @param archive Archive to remove as a source from this item. */
    public void removeArchiveDataSource(final ArchiveDataSource archive) {
        if (archives.remove(archive)) {
            fireItemDataConfigChanged();
        }
    }

    /** @param archive Archives to remove as a source from this item. Ignored when not used. */
    public void removeArchiveDataSource(final ArchiveDataSource archs[]) {
        boolean change = false;
        for (final ArchiveDataSource archive : archs) {
            if (archives.remove(archive)) {
                change = true;
            }
        }
        if (change) {
            fireItemDataConfigChanged();
        }
    }

    /** Replace existing archive data sources with given archives
     *  @param archs ArchiveDataSources to use for this item
     */
    public void setArchiveDataSource(final ArchiveDataSource archs[]) {
        // Check if they are the same, i.e. count AND order match
        if (archs.length == archives.size()) {
            boolean same = true;
            for (int i = 0; i < archs.length; ++i) {
                if (!archs[i].equals(archives.get(i))) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return;
            }
        }
        // Different archives
        archives.clear();
        for (final ArchiveDataSource arch : archs) {
            archives.add(arch);
        }
        fireItemDataConfigChanged();
    }

    /** @return Archive data request type */
    public RequestType getRequestType() {
        return request_type;
    }

    /** @param request_type New request type */
    public void setRequestType(final RequestType request_type) {
        if (this.request_type == request_type) {
            return;
        }
        this.request_type = request_type;

        samples.updateRequestType(request_type);

        fireItemDataConfigChanged();
    }

    /** Notify listeners */
    private void fireItemDataConfigChanged() {
        if (model != null) {
            model.fireItemDataConfigChanged(this);
        }
    }

    /** Connect control system PV, start scanning, ...
     *  @throws Exception on error
     */
    @SuppressWarnings("nls")
    public void start(final Timer timer) throws Exception {
        if (pv.isRunning()) {
            throw new RuntimeException("Already started " + getName());
        }

        pv_deadband = createAndStartMdelPV(samples);
        //has_deadband = retrieveDeadbandExistenceInfoFor(name);

        this.scan_timer = timer;
        pv.addListener(this);
        pv.start();
        // Log every received value?
        if (_period <= 0.0) {
            return;
        }
        // Start scanner for periodic log
        scanner = new TimerTask() {
            @Override
            public void run() {
                LOG.debug("PV {0} scans {1}", new Object[] { getName(), current_value });
                logCurrentValue();
            }
        };
        final long delay = (long) (_period * 1000);
        timer.schedule(scanner, delay, delay);
    }

    /** Disconnect from control system PV, stop scanning, ... */
    @SuppressWarnings("nls")
    public void stop() {
        LOG.debug("Stop PVItem: {}", pv.getName());
        if (pv == null) {
            throw new RuntimeException("Not running " + getName());
        }
        if (scanner != null) {
            scanner.cancel();
            scanner = null;
        }
        pv.removeListener(this);
        pv.stop();
        pv = null;
        if (pv_deadband != null && pv_deadband.isRunning()) {
            pv_deadband.stop();
        }
    }

    /** {@inheritDoc} */
    @Override
    public PVSamples getSamples() {
        return samples;
    }

    // PVListener
    @Override
    public void pvDisconnected(final PV pv) {
        current_value = null;
        // In 'monitor' mode, mark in live sample buffer
        if (_period <= 0) {
            logDisconnected();
        }
    }

    private void onConnect(@Nonnull final IValue value) {
        if (first_pv_update) {
            first_pv_update = false;
            if (!_minMaxFromFile) {
                if (value.getMetaData() instanceof INumericMetaData) {

                    final INumericMetaData meta = (INumericMetaData) value.getMetaData();
                    final double displayHigh = meta.getDisplayHigh();
                    final double displayLow = meta.getDisplayLow();
                    // Call into the ui thread
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            getAxis().setRange(displayLow, displayHigh);
                        }
                    });
                }
            }
        }
    }

    // PVListener
    @Override
    @SuppressWarnings("nls")
    public void pvValueUpdate(final PV new_pv) {
        final IValue value = new_pv.getValue();

        onConnect(value);

        // Cache most recent for 'scanned' operation
        current_value = value;

        // In 'monitor' mode, add to live sample buffer
        if (_period <= 0) {
            samples.addLiveSample(value);
            LOG.trace(pv.getName() + " : " + samples.getLiveSampleSize() + " live samples");
        }
    }

    /** Add 'current' value to the live sample ring buffer,
     *  using 'now' as time stamp.
     */
    private void logCurrentValue() {
        final IValue value = current_value;
        if (value == null) {
            logDisconnected();
            return;
        }
        // Transform value to have 'now' as time stamp
        samples.addLiveSample(ValueButcher.changeTimestampToNow(value));
    }

    /** Add one(!) 'disconnected' sample */
    private void logDisconnected() {
        synchronized (samples) {
            final int size = samples.getSize();
            if (size > 0) {
                final String last = samples.getSample(size - 1).getValue().getStatus();
                // Does last sample already have 'disconnected' status?
                if (last != null && last.equals(Messages.Model_Disconnected)) {
                    return;
                }
            }
            samples.addLiveSample(new PlotSample(Messages.LiveData, Messages.Model_Disconnected));
        }
    }

    /** Add data retrieved from an archive to the 'historic' section
     *  @param reader Archive server that provided these samples
     *  @param result Historic data
     * @param currentRequestType
     * @throws ArchiveServiceException
     * @throws OsgiServiceUnavailableException
     */
    synchronized public void mergeArchivedSamples(final ArchiveReader reader,
                                                  final List<IValue> result,
                                                  final RequestType requestType) throws OsgiServiceUnavailableException,
                                                                                ArchiveServiceException {
        samples.mergeArchivedData(getName(), reader, requestType, result);
    }

    /** Write XML formatted PV configuration
     *  @param writer PrintWriter
     */
    @Override
    public void write(final PrintWriter writer) {
        XMLWriter.start(writer, 2, Model.TAG_PV);
        writer.println();
        writeCommonConfig(writer);
        XMLWriter.XML(writer, 3, Model.TAG_SCAN_PERIOD, getScanPeriod());
        XMLWriter.XML(writer, 3, Model.TAG_LIVE_SAMPLE_BUFFER_SIZE, getLiveCapacity());
        XMLWriter.XML(writer, 3, Model.TAG_REQUEST, getRequestType().name());
        for (final ArchiveDataSource archive : archives) {
            XMLWriter.start(writer, 3, Model.TAG_ARCHIVE);
            writer.println();
            XMLWriter.XML(writer, 4, Model.TAG_NAME, archive.getName());
            XMLWriter.XML(writer, 4, Model.TAG_URL, archive.getUrl());
            XMLWriter.XML(writer, 4, Model.TAG_KEY, archive.getKey());
            XMLWriter.end(writer, 3, Model.TAG_ARCHIVE);
            writer.println();
        }
        XMLWriter.end(writer, 2, Model.TAG_PV);
        writer.println();
    }

    /** Create PVItem from XML document
     *  @param model Model to which this item will belong (but doesn't, yet)
     *  @param node XML node with item configuration
     *  @return PVItem
     *  @throws Exception on error
     */
    public static PVItem fromDocument(final Model model, final Element node) throws Exception {
        final String name = DOMHelper.getSubelementString(node, Model.TAG_NAME);
        final double period = DOMHelper.getSubelementDouble(node, Model.TAG_SCAN_PERIOD, 0.0);

        final PVItem item = new PVItem(name, period);
        final int buffer_size = DOMHelper.getSubelementInt(node,
                                                           Model.TAG_LIVE_SAMPLE_BUFFER_SIZE,
                                                           Preferences.getLiveSampleBufferSize());
        item.setLiveCapacity(buffer_size);

        final String req_txt = DOMHelper.getSubelementString(node,
                                                             Model.TAG_REQUEST,
                                                             RequestType.OPTIMIZED.name());
        try {

            final RequestType request = RequestType.valueOf(req_txt);
            item.setRequestType(request);
        } catch (final Throwable ex) {
            // Ignore
        }

        item.configureFromDocument(model, node);

        // Load archives from saved configuration
        boolean have_imported_data = false;
        Element archive = DOMHelper.findFirstElementNode(node.getFirstChild(), Model.TAG_ARCHIVE);
        while (archive != null)
        {
            final String url = DOMHelper.getSubelementString(archive, Model.TAG_URL);
            final int key = DOMHelper.getSubelementInt(archive, Model.TAG_KEY);
            final String arch = DOMHelper.getSubelementString(archive, Model.TAG_NAME);

            if (url.startsWith(ImportArchiveReaderFactory.PREFIX))
                have_imported_data = true;

            item.addArchiveDataSource(new ArchiveDataSource(url, key, arch));
            archive = DOMHelper.findNextElementNode(archive, Model.TAG_ARCHIVE);
        }

        // When requested, use default archive sources for 'real' archives (RDB, ...)
        // Do not clobber an imported archive data source, a specific file which was
        // probably not meant to be replaced by a default.
        if (Preferences.useDefaultArchives()  &&  !have_imported_data)
            item.useDefaultArchiveDataSources();

        return item;
    }

    private PV createAndStartMdelPV(final PVSamples pv_samples) throws Exception {
        final String mdelChannelName = EpicsNameSupport.parseBaseName(super.toString())
                + EpicsChannelName.FIELD_SEP + RecordField.MDEL.getFieldName();
        final PV mdel_pv = PVFactory.createPV(mdelChannelName);
        mdel_pv.addListener(new PVListener() {

            @Override
            public void pvValueUpdate(final PV newPV) {
                final IValue mdelValue = newPV.getValue();
                Number mdel;
                if (mdelValue instanceof IDoubleValue) {
                    mdel = Double.valueOf(((IDoubleValue) mdelValue).getValue());
                } else if (mdelValue instanceof ILongValue) {
                    mdel = Long.valueOf(((ILongValue) mdelValue).getValue());
                } else {
                    return;
                }
                pv_samples.setLiveSamplesDeadband(mdel);
            }

            @Override
            public void pvDisconnected(final PV newPV) {
                pv_samples.setLiveSamplesDeadband(null);
            }
        });
        mdel_pv.start();
        return mdel_pv;
    }

    /**
     * @param b
     */
    public void setMinMaxFromFile(final boolean minMaxFromFile) {
        _minMaxFromFile = minMaxFromFile;
    }

    /**
     * 
     */
    public void moveHistoricSamplesToLiveSamples() {
        samples.moveHistoricSamplesToLiveSamples();
    }
}
