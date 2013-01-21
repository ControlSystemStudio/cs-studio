/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.swt.xygraph.dataprovider.IDataProviderListener;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Samples of a {@link PVItem}.
 *  <p>
 *  Made up of two sections,
 *  {@link HistoricSamples} and {@link LiveSamples},
 *  and presenting them as one long stream of samples.
 *
 *  In addition, if the last sample is valid, it's
 *  extended to 'now' assuming no new data means
 *  that the last value is still valid.
 *
 *  @author Kay Kasemir
 */
public class PVSamples extends PlotSamples
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PVSamples.class);

    /** Historic samples */
    private final HistoricSamples historicSamples;

    /** Live samples. Should start after end of historic samples */
    private final CompressedLiveSamples liveSamples;

    boolean show_deadband = false;

    
    /**
     * Constructor.
     */
    public PVSamples(@Nonnull final RequestType request_type,
                     @Nullable final IIntervalProvider prov) {
        updateRequestType(request_type);

        final int liveSampleBufferSize = Preferences.getLiveSampleBufferSize(); // 5000
        final int uncompressedSamples = Preferences.getUncompressedLiveSampleSize();
        final int securityCompressedSamples = Preferences.getSecurityCompressedLiveSampleSize();
        liveSamples = new CompressedLiveSamples(new LiveSamplesCompressor(uncompressedSamples),
                                                liveSampleBufferSize,
                                                securityCompressedSamples,
                                                prov);
        liveSamples.setDynamicCompression(true);
        historicSamples = new HistoricSamples(request_type, prov, new LiveSamplesCompressor(0));
    }
    /**
     * Constructor.
     */
    public PVSamples(@Nonnull final RequestType request_type) {
        this (request_type, null);
    }

    private ArrayList<IDataProviderListener> listeners = new ArrayList<IDataProviderListener>();
    
    /** {@inheritDoc} */
    @Override
    public void addDataProviderListener(IDataProviderListener listener)
    {
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean removeDataProviderListener(IDataProviderListener listener)
    {
        synchronized (listeners)
        {
            return listeners.remove(listener);
        }
    }
    
    /** @param index Waveform index to show */
    public void setWaveformIndex(int index)
    {
        liveSamples.setWaveformIndex(index);
        historicSamples.setWaveformIndex(index);

        synchronized (listeners)
        {
            for (IDataProviderListener listener : listeners)
            {
                // Notify listeners of the change of the waveform index
                // mainly in order to update the position of snapped
                // annotations. For more details, see the comment in
                // Annotation.dataChanged(IDataProviderListener).
                listener.dataChanged(this);
            }
        }
    }

    /** @return Maximum number of liveSamples samples in ring buffer */
    public int getLiveCapacity()
    {
        return liveSamples.getCapacity();
    }

    /** Set new capacity for liveSamples sample ring buffer
     *  <p>
     *  @param new_capacity New sample count capacity
     *  @throws Exception on out-of-memory error
     */
    public void setLiveCapacity(final int new_capacity) throws Exception
    {
        liveSamples.setCapacity(new_capacity);
    }

    /** @return Combined count of historic and liveSamples samples */
    @Override
    synchronized public int getSize()
    {
        final int raw = getRawSize();
        if (raw <= 0) {
            return raw;
        }
        final PlotSample last = getSample(raw-1);
        if (! last.getValue().getSeverity().hasValue()) {
            return raw;
        }
        // Last sample is valid, so it should still apply 'now'
        return raw+1;
    }

    /** @return Size of the actual historic and liveSamples samples
     *          without the continuation to 'now'
     */
    private int getRawSize()
    {
        return historicSamples.getSize() + liveSamples.getSize();
    }

    /** @param index 0... getSize()-1
     *  @return Sample from historic or liveSamples sample subsection
     */
    @Override
    synchronized public PlotSample getSample(final int index)
    {
        final int raw_count = getRawSize();
        if (index < raw_count) {
            return getRawSample(index);
        }
        // Last sample is valid, so it should still apply 'now'
        final PlotSample sample = getRawSample(raw_count-1);
        return ValueButcher.changeTimestampToNow(sample);
    }

    /** Get 'raw' sample, no continuation until 'now'
     *  @param index 0... getRawSize()-1
     *  @return Sample from historic or liveSamples sample subsection
     */
    private PlotSample getRawSample(final int index)
    {
        final int num_old = historicSamples.getSize();
        if (index < num_old) {
            return historicSamples.getSample(index);
        }
        return liveSamples.getSample(index - num_old);
    }

    /** @return Overall time (x axis) range of historic and liveSamples samples */
    @Override
    synchronized public Range getXDataMinMax()
    {
        final Range old_range = historicSamples.getXDataMinMax();
        final Range new_range = liveSamples.getXDataMinMax();
        if (old_range == null) {
            return new_range;
        }
        if (new_range == null) {
            return old_range;
        }
        // Both are not-null
        return new Range(old_range.getLower(), new_range.getUpper());
    }

    /** @return Overall value (y axis) range of historic and liveSamples samples */
    @Override
    synchronized public Range getYDataMinMax()
    {
        final Range old_range = historicSamples.getYDataMinMax();
        final Range new_range = liveSamples.getYDataMinMax();
        if (old_range == null) {
            return new_range;
        }
        if (new_range == null) {
            return old_range;
        }
        // Both are not-null
        final double min = Math.min(old_range.getLower(), new_range.getLower());
        final double max = Math.max(old_range.getUpper(), new_range.getUpper());
        return new Range(min, max);
    }

    /** Test if samples changed since the last time
     *  <code>testAndClearNewSamplesFlag</code> was called.
     *  @return <code>true</code> if there were new samples
     */
    @Override
    synchronized public boolean hasNewSamples()
    {
        return historicSamples.hasNewSamples() || liveSamples.hasNewSamples();
    }

    /** Test if samples changed since the last time this method was called.
     *  @return <code>true</code> if there were new samples
     */
    @Override
    synchronized public boolean testAndClearNewSamplesFlag()
    {
        // Must check & __clear__ both subsections!
        // return hist.test() | liveSamples.test() would skip
        // the liveSamples.test if hist.test() was already true!
        final boolean hist_change = historicSamples.testAndClearNewSamplesFlag();
        final boolean live_change = liveSamples.testAndClearNewSamplesFlag();
        return hist_change || live_change;
    }

    /** Add data retrieved from an archive to the 'historic' section
     *  @param source Source of the samples
     * @param reader
     * @param requestType
     *  @param result Historic data
     * @throws ArchiveServiceException
     * @throws OsgiServiceUnavailableException
     */
    synchronized public void mergeArchivedData(final String channel_name,
                                               final ArchiveReader reader,
                                               final RequestType requestType,
                                               final List<IValue> result)
                                               throws OsgiServiceUnavailableException,
                                                      ArchiveServiceException
    {
        historicSamples.mergeArchivedData(channel_name, reader, requestType, result);
    }

    /** Add another 'liveSamples' sample
     *  @param value 'Live' sample
     */
    synchronized public void addLiveSample(IValue value)
    {
        if (! value.getTime().isValid()) {
            value = ValueButcher.changeTimestampToNow(value);
        }
        addLiveSample(new PlotSample(Messages.LiveData, value));
    }

    /** Add another 'liveSamples' sample
     *  @param value 'Live' sample
     */
    synchronized public void addLiveSample(final PlotSample sample)
    {
        liveSamples.add(sample);
        // History ends before the start of 'liveSamples' samples.
        // Adding a liveSamples sample might have moved the ring buffer,
        // so need to update whenever liveSamples data is extended.
        historicSamples.setBorderTime(liveSamples.getSample(0).getTime());
    }

    /** Delete all samples */
    synchronized public void clear()
    {
        historicSamples.clear();
        liveSamples.clear();
    }

    /** @return (Long) string representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("PV Samples\nHistory: ");
        buf.append(historicSamples.toString());
        buf.append("\nLive Buffer: ");
        buf.append(liveSamples.toString());

        final int count = getSize();
        if (count != getRawSize())
        {
            buf.append("\nContinuation to 'now':\n");
            buf.append("     " + getSample(count-1));
        }
        return buf.toString();
    }

    synchronized public void setLiveSamplesDeadband(final Number deadband) {
        liveSamples.setDeadband(deadband);
    }
    synchronized public void setHistorySamplesDeadband(final Number deadband) {
        historicSamples.setDeadband(deadband);
    }

    /**
     * @return sample size of liveSamples samples
     */
    @Nonnull
    public int getLiveSampleSize() {
        return liveSamples.getSize();
    }

    public void invalidateHistoricSamples() {
        historicSamples.invalidateHistoricSamples();
    }
    
    /**
     * 
     */
    public void moveHistoricSamplesToLiveSamples() {
        List<PlotSample> allHistoricSamples = historicSamples.getAllSamples();
        for (PlotSample plotSample : allHistoricSamples) {
            liveSamples.add(plotSample);
        }
        historicSamples.clear();
    }
}
