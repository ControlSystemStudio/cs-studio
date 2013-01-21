/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.common.trendplotter.Activator;
import org.csstudio.common.trendplotter.preferences.Preferences;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.IValue;
import org.csstudio.domain.common.collection.LimitedArrayCircularQueue;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.csstudio.domain.desy.epics.name.EpicsNameSupport;
import org.csstudio.domain.desy.epics.name.RecordField;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/** Holder for 'historic' orgSamples.
 *  <p>
 *  In addition to holding 'all' historic orgSamples, this class
 *  allows for a 'border' time beyond which no orgSamples will
 *  be provided.
 *  When setting this border to the start of the 'live' orgSamples,
 *  this class will thus assert that the live orgSamples have
 *  precedence because no 'historic' sample is provided
 *  for the 'live' time range.
 *  When the start of the 'live' time range moves because
 *  the live data ring buffer rolls around, the 'border' time adjustments
 *  might then uncover historic orgSamples that were previously
 *  hidden below the 'live' time range.
 *
 *  @author Kay Kasemir
 */
public class HistoricSamples extends PlotSamples
{
    /** "All" historic orgSamples */
//    private PlotSample[] orgSamples = new PlotSample[0];
//    private PlotSample[] intSamples = new PlotSample[0];

    private static final Logger LOG = LoggerFactory.getLogger(HistoricSamples.class);
    
    private final Map<RequestType, PlotSample[]> sample_map =
        Maps.newEnumMap(RequestType.class);

    /** If non-null, orgSamples beyond this time are hidden from access */
    private ITimestamp border_time = null;

    /**
     * Subset of orgSamples.length that's below border_time
     *  @see #computeVisibleSize()
     */
    private int visible_size = 0;
    
    /** Waveform index */
    private int waveform_index = 0;

    /**
     * Indicates that the historic samples are invalid and should be deleted.
     * Otherwise a large number of samples make draw2d inperformant.
     */
    private boolean _histSamplesInvalid = false;

    private final IIntervalProvider _prov;

    private final LiveSamplesCompressor _liveSamplesCompressor;

    /**
     * Constructor.
     * @param prov 
     * @param liveSamplesCompressor 
     */
    public HistoricSamples(@Nonnull final RequestType request_type, IIntervalProvider prov, LiveSamplesCompressor liveSamplesCompressor)
    {
        _prov = prov;
        _liveSamplesCompressor = liveSamplesCompressor;
        for (final RequestType type : RequestType.values()) {
            sample_map.put(type, new PlotSample[0]);
        }
        updateRequestType(request_type);
    }


    /** Define a new 'border' time beyond which no orgSamples
     *  are returned from the history
     *  @param border_time New time or <code>null</code> to access all orgSamples
     */
    public void setBorderTime(final ITimestamp border_time)
    {   // Anything new?
        if (border_time == null)
        {
            if (this.border_time == null) {
                return;
            }
        }
        else if (border_time.equals(this.border_time)) {
            return;
        }
        // New border, recompute, mark as 'new data'
        this.border_time = border_time;
        computeVisibleSize(sample_map.get(request_type));
        synchronized (this) {
            have_new_samples = true;
        }
    }

    /** Update visible size */
    synchronized private void computeVisibleSize(final PlotSample[] samples)
    {
        if (border_time == null) {
            visible_size = samples.length;
        } else
        {
            final int last_index =
                PlotSampleSearch.findSampleLessThan(samples, border_time);
            visible_size = last_index < 0   ?   0   :   last_index + 1;
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    synchronized public PlotSample getSample(final int i)
    {
        if (i >= visible_size) {
            throw new IndexOutOfBoundsException("Index " + i + " exceeds visible size " + visible_size);
        }
        return sample_map.get(request_type)[i];
    }

    /** {@inheritDoc} */
    @Override
    synchronized public int getSize()
    {
        return visible_size;
    }

    /** Merge newly received archive data into historic orgSamples
     * @param channel_name
     * @param source Info about data source
     * @param requestType
     * @param result Samples to add/merge
     * @throws ArchiveServiceException
     * @throws OsgiServiceUnavailableException
     */
    synchronized public void mergeArchivedData(final String channel_name,
                                               final ArchiveReader source,
                                               final RequestType requestType,
                                               final List<IValue> result)
                                               throws OsgiServiceUnavailableException,
                                                      ArchiveServiceException
    {
        // Anything new at all?
        if (result.size() <= 0) {
            return;
        }
        if (_histSamplesInvalid) {
            clear();
        }
        // Turn IValues into PlotSamples
        final PlotSample new_samples[] = new PlotSample[result.size()];
        for (int i=0; i<new_samples.length; ++i) {
            new_samples[i] = new PlotSample(source.getServerName(), result.get(i));
        }
        if (isArchiveServiceforADELPresent()) {
            findAndSetArchiveDeadBandForNewSamples(channel_name, new_samples);
        }

        // Merge with existing samples
        final PlotSample[] ext_samples = sample_map.get(requestType);
        final PlotSample[] merged_result = PlotSampleMerger.merge(ext_samples, new_samples);

        sample_map.put(requestType, merged_result);
        if (Preferences.getCompressHistSamples()) {
            compressSamples();
        }
            
        computeVisibleSize(merged_result);
        updateRequestType(requestType);

        have_new_samples = true;
    }

    private void compressSamples() {
        final int histBuffer = Preferences.getHistSampleBuffer();
        final Long[] windowsMS = determinePerfectWindowForCompressedSamples(histBuffer,
                                                                            _prov.getTimeInterval());
        _liveSamplesCompressor.setCompressionWindows(windowsMS);
    
        
        PlotSample[] plotSamples = sample_map.get(RequestType.OPTIMIZED);
        LimitedArrayCircularQueue<PlotSample> samples = new LimitedArrayCircularQueue<PlotSample>(plotSamples.length);
        samples.addAll(Arrays.asList(plotSamples));
        LimitedArrayCircularQueue<PlotSample> transform = _liveSamplesCompressor.transform(samples, plotSamples);
        PlotSample[] array = transform.toArray(new PlotSample[0]);
        if (transform.size()==plotSamples.length) {
            return;
        }
        sample_map.put(RequestType.OPTIMIZED, removeNotConnectedValues(array));
    }
    
    /**
     * @param array
     * @return 
     */
    private PlotSample[] removeNotConnectedValues(PlotSample[] array) {
        List<PlotSample> samplesWithoutNA = new ArrayList<PlotSample>(array.length);
        for (PlotSample sample : array) {
            if (!sample.getValue().format().startsWith("#")) {
                samplesWithoutNA.add(sample);
            }
        }
        return samplesWithoutNA.toArray(new PlotSample[0]);
    }


    /**
     * Simple strategy to determine a good compression rate.
     * The 'perfect' window length is the displayed time interval divided by the capacity (that
     * has been already diminished by the number of permanently uncompressed samples).
     * And the last modification is due to that we expect two samples per window, min and max.
     *
     * @param cap the capacity for compressed samples (cap - uncompressed)
     * @param intvlMS the length of the interval wherein the compressed samples shall be displayed
     * @param the buffer reserve not considered for the window calculation
     * @return
     */
    @Nonnull
    private Long[] determinePerfectWindowForCompressedSamples(final int cap,
                                                              final Interval intvl) {
        final long windowLengthMS = (int) ((intvl.getEndMillis() - intvl.getStartMillis()) / cap); // perfect
        return new Long[] {windowLengthMS*4}; // double - and don't forget - min and max are 2 samples per window
    }


    /**
     * In case the service is present, ADEL info can be retrieved otherwise not
     * @return if there is a service offering ADEL info
     */
    private boolean isArchiveServiceforADELPresent() {
        try {
            Activator.getDefault().getArchiveReaderService();
        } catch (final OsgiServiceUnavailableException e) {
            return false;
        }
        return true;
    }


    /** Delete all orgSamples */
    synchronized public void clear()
    {
        visible_size = 0;
        for (final RequestType type : RequestType.values()) {
            sample_map.put(type, new PlotSample[0]);
        }
        have_new_samples = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void updateRequestType(@Nonnull final RequestType type) {
        super.updateRequestType(type);
        computeVisibleSize(sample_map.get(request_type));
    }

    private void findAndSetArchiveDeadBandForNewSamples(final String channel_name,
                                                        final PlotSample[] new_samples) throws OsgiServiceUnavailableException,
                                                                                       ArchiveServiceException {
        if (new_samples.length > 0) {
            try {
                final Collection<IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>>> adels = retrieveAdelSamples(channel_name,
                                                                                                                               new_samples[0]
                                                                                                                                       .getTime(),
                                                                                                                               new_samples[new_samples.length - 1]
                                                                                                                                       .getTime());
                if (!adels.isEmpty()) {
                    final Iterator<IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>>> iter = adels
                            .iterator();
                    final IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>> curAdel = iter
                            .next();
                    
                    for (final PlotSample new_sample : new_samples) {
                        final PlotSample sample = new_sample;
                        if (!sample.hasDeadband()) {
                            findAndSetAdelValueForPlotSample(sample, iter, curAdel);
                        }
                    }
                }
            } catch (ArchiveServiceException e) {
                LOG.info("Dead beand info not available for " + channel_name);
            }
        }
    }

    private void findAndSetAdelValueForPlotSample(@Nonnull final PlotSample sample,
                                                  @Nonnull final Iterator<IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>>> iter,
                                                  @Nonnull final IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>> curAdel) {

        final TimeInstant sampleTs = BaseTypeConversionSupport.toTimeInstant(sample.getTime());

        TimeInstant curAdelTs = curAdel.getSystemVariable().getTimestamp();
        if (curAdelTs.isAfter(sampleTs)) {
            sample.setDeadband(null); // no adel info for this sample
            return;
        }

        IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>> nextAdel = iter.hasNext() ?
                                                                        iter.next() :
                                                                        null;
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
                sample.setDeadband(null);
                return;
            }
        }

        sample.setDeadband((Number) curAdel.getValue());
    }

    private Collection<IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>>>
    retrieveAdelSamples(final String channel_name,
                        final ITimestamp start,
                        final ITimestamp end)
                        throws OsgiServiceUnavailableException, ArchiveServiceException
    {
        final IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        final TimeInstant s = BaseTypeConversionSupport.toTimeInstant(start);
        final TimeInstant e = BaseTypeConversionSupport.toTimeInstant(end);

        final String adelChannelName =
            EpicsNameSupport.parseBaseName(channel_name) +
            EpicsChannelName.FIELD_SEP +
            RecordField.ADEL.getFieldName();

        final Collection samples =
            service.readSamples(adelChannelName,
                                s,
                                e);
        final LinkedList<IArchiveSample<Serializable, IAlarmSystemVariable<Serializable>>> allSamples = Lists.newLinkedList(samples);

        final IArchiveSample lastBefore = service.readLastSampleBefore(adelChannelName, s);
        if (lastBefore != null) {
            allSamples.addFirst(lastBefore);
        }
        return allSamples;
    }


    public void invalidateHistoricSamples() {
        _histSamplesInvalid = true;
    }


    /**
     * @return 
     * 
     */
    public ArrayList<PlotSample> getAllSamples() {
        ArrayList<PlotSample> historicSampleList = new ArrayList<PlotSample>(); 
        Set<RequestType> keySet = sample_map.keySet();
        for (RequestType requestType : keySet) {
            PlotSample[] plotSamples = sample_map.get(requestType);
            if (plotSamples!=null && plotSamples.length>0) {
                historicSampleList.addAll(Arrays.asList(plotSamples));
            }
        }
        return historicSampleList;
    }
    
    /** @param index Waveform index to show */
    public synchronized void setWaveformIndex(int index)
    {
        waveform_index = index;
        // change the index of all samples in this instance
        Set<RequestType> keySet = sample_map.keySet();
        for (RequestType requestType : keySet) {
            PlotSample[] samples = sample_map.get(request_type);
            for (PlotSample sample: samples) {
                sample.setWaveformIndex(waveform_index);
            }
        }
    }
}
