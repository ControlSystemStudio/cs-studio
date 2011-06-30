/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.common.trendplotter.Activator;
import org.csstudio.common.trendplotter.Messages;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.csstudio.domain.desy.epics.name.EpicsNameSupport;
import org.csstudio.domain.desy.epics.name.RecordField;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

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
    
    final private PVItem item;
    
    /** Historic samples */
    final private HistoricSamples history;

    /** Live samples. Should start after end of historic samples */
    final private LiveSamples live = new LiveSamples();
    
    boolean show_adel = false;

    /**
     * Constructor.
     */
    public PVSamples(@Nonnull final PVItem pvItem) 
    {
        item = pvItem;
        history = new HistoricSamples(item);
        updateRequestType(item.getRequestType());
    }

    /** @return Maximum number of live samples in ring buffer */
    public int getLiveCapacity()
    {
        return live.getCapacity();
    }

    /** Set new capacity for live sample ring buffer
     *  <p>
     *  @param new_capacity New sample count capacity
     *  @throws Exception on out-of-memory error
     */
    public void setLiveCapacity(final int new_capacity) throws Exception
    {
        live.setCapacity(new_capacity);
    }

    /** @return Combined count of historic and live samples */
    @Override
    synchronized public int getSize()
    {
        final int raw = getRawSize();
        if (raw <= 0)
            return raw;
        final PlotSample last = getSample(raw-1);
        if (! last.getValue().getSeverity().hasValue())
            return raw;
        // Last sample is valid, so it should still apply 'now'
        return raw+1;
    }

    /** @return Size of the actual historic and live samples
     *          without the continuation to 'now'
     */
    private int getRawSize()
    {
        return history.getSize() + live.getSize();
    }

    /** @param index 0... getSize()-1
     *  @return Sample from historic or live sample subsection
     */
    @Override
    synchronized public PlotSample getSample(final int index)
    {
        final int raw_count = getRawSize();
        if (index < raw_count)
            return getRawSample(index);
        // Last sample is valid, so it should still apply 'now'
        final PlotSample sample = getRawSample(raw_count-1);
        return ValueButcher.changeTimestampToNow(sample);
    }

    /** Get 'raw' sample, no continuation until 'now'
     *  @param index 0... getRawSize()-1
     *  @return Sample from historic or live sample subsection
     */
    private PlotSample getRawSample(final int index)
    {
        final int num_old = history.getSize();
        if (index < num_old)
            return history.getSample(index);
        return live.getSample(index - num_old);
    }

    /** @return Overall time (x axis) range of historic and live samples */
    @Override
    synchronized public Range getXDataMinMax()
    {
        final Range old_range = history.getXDataMinMax();
        final Range new_range = live.getXDataMinMax();
        if (old_range == null)
            return new_range;
        if (new_range == null)
            return old_range;
        // Both are not-null
        return new Range(old_range.getLower(), new_range.getUpper());
    }

    /** @return Overall value (y axis) range of historic and live samples */
    @Override
    synchronized public Range getYDataMinMax()
    {
        final Range old_range = history.getYDataMinMax();
        final Range new_range = live.getYDataMinMax();
        if (old_range == null)
            return new_range;
        if (new_range == null)
            return old_range;
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
        return history.hasNewSamples() || live.hasNewSamples();
    }

    /** Test if samples changed since the last time this method was called.
     *  @return <code>true</code> if there were new samples
     */
    @Override
    synchronized public boolean testAndClearNewSamplesFlag()
    {
        // Must check & __clear__ both subsections!
        // return hist.test() | live.test() would skip
        // the live.test if hist.test() was already true!
        final boolean hist_change = history.testAndClearNewSamplesFlag();
        final boolean live_change = live.testAndClearNewSamplesFlag();
        return hist_change || live_change;
    }

    /** Add data retrieved from an archive to the 'historic' section
     *  @param source Source of the samples
     *  @param result Historic data
     */
    synchronized public void mergeArchivedData(final String source,
            final ArrayList<IValue> result)
    {
        history.mergeArchivedData(source, result);
    }

    /** Add another 'live' sample
     *  @param value 'Live' sample
     */
    synchronized public void addLiveSample(IValue value)
    {
        if (! value.getTime().isValid())
            value = ValueButcher.changeTimestampToNow(value);
        addLiveSample(new PlotSample(Messages.LiveData, value));
    }

    /** Add another 'live' sample
     *  @param value 'Live' sample
     */
    synchronized public void addLiveSample(final PlotSample sample)
    {
        live.add(sample);
        // History ends before the start of 'live' samples.
        // Adding a live sample might have moved the ring buffer,
        // so need to update whenever live data is extended.
        history.setBorderTime(live.getSample(0).getTime());
    }

    /** Delete all samples */
    synchronized public void clear()
    {
        history.clear();
        live.clear();
    }

    /** @return (Long) string representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("PV Samples\nHistory: ");
        buf.append(history.toString());
        buf.append("\nLive Buffer: ");
        buf.append(live.toString());

        final int count = getSize();
        if (count != getRawSize())
        {
            buf.append("\nContinuation to 'now':\n");
            buf.append("     " + getSample(count-1));
        }
        return buf.toString();
    }

    public void toggleShowAdel(String channelName) throws OsgiServiceUnavailableException, ArchiveServiceException {
        show_adel = !show_adel;
        
        int size = history.getSize();
        for (int i = 0; i < size; i++) {
            history.getSample(i).setShowAdel(show_adel);
        }

        if (size > 0 && !history.adelInfoComplete()) {
            Collection<IArchiveSample<Object, IAlarmSystemVariable<Object>>> adels =
                    retrieveAdelSamples(channelName);
            
            if (!adels.isEmpty()) {
                Iterator<IArchiveSample<Object, IAlarmSystemVariable<Object>>> iter = adels.iterator();
                IArchiveSample<Object, IAlarmSystemVariable<Object>> curAdel = iter.next();
                
                for (int i = 0; i < size; i++) {
                    PlotSample sample = history.getSample(i);
                    if (!sample.hasAdelValue()) {
                        findAndSetAdelValueForPlotSample(sample, iter, curAdel);
                    }
                }
            }
            history.setAdelInfoComplete(true);
        }
    }


    private void findAndSetAdelValueForPlotSample(@Nonnull PlotSample sample,
                                                  @Nonnull Iterator<IArchiveSample<Object, IAlarmSystemVariable<Object>>> iter, 
                                                  @Nonnull IArchiveSample<Object, IAlarmSystemVariable<Object>> curAdel) {
        
        TimeInstant sampleTs = BaseTypeConversionSupport.toTimeInstant(sample.getTime());
        TimeInstant curAdelTs = curAdel.getSystemVariable().getTimestamp();
        if (curAdelTs.isAfter(sampleTs)) {
            sample.setAdel(null); // no adel info for this sample
            return;
        }
        
        IArchiveSample<Object, IAlarmSystemVariable<Object>> nextAdel = iter.hasNext() ? 
                                                                        iter.next() : null;
        TimeInstant nextAdelTs = nextAdel != null ? 
                                 nextAdel.getSystemVariable().getTimestamp() :
                                 null;                                                                        

        // find the adel pair curAdel, nextAdel where 
        // curAdel.isBefore(ts) && (nextAdel.isAfter() || nextAdel == null)
        while (! (curAdelTs.isBefore(sampleTs) && (nextAdelTs == null || nextAdelTs.isAfter(sampleTs)))) {
            
            if (iter.hasNext()) {
                curAdelTs = nextAdelTs;
                nextAdel = iter.next();
                nextAdelTs = nextAdel.getSystemVariable().getTimestamp();
            } else { // no valid adel present, return with adel set to null 
                sample.setAdel(null);
                return;
            }
        }
        
        sample.setAdel((Number) curAdel.getValue());
    }

    private Collection<IArchiveSample<Object, IAlarmSystemVariable<Object>>> retrieveAdelSamples(String channelName) 
                                                                                                 throws OsgiServiceUnavailableException,
                                                                                                        ArchiveServiceException {
        IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        TimeInstant start = BaseTypeConversionSupport.toTimeInstant(history.getSample(0).getTime());
        TimeInstant end = BaseTypeConversionSupport.toTimeInstant(history.getSample(history.getSize() - 1).getTime());

        String adelChannelName = EpicsNameSupport.parseBaseName(channelName) + 
        EpicsChannelName.FIELD_SEP +
        RecordField.ADEL.getFieldName();
        
        IArchiveSample lastBefore = service.readLastSampleBefore(adelChannelName, start);
        
        Collection samples =
                service.readSamples(adelChannelName, 
                                    start, 
                                    end);
        LinkedList<IArchiveSample<Object, IAlarmSystemVariable<Object>>> allSamples = Lists.newLinkedList(samples);
        allSamples.addFirst(lastBefore);
        return allSamples;
    }
}
