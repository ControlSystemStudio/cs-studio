/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.archive.common.reader;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.requesttypes.IArchiveRequestType;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.sample.SampleAggregator;
import org.csstudio.archive.common.service.util.ArchiveSampleToIValueFunction;
import org.csstudio.archivereader.Severity;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

/**
 * Generates an iterator for channels that are
 *
 * @author bknerr
 * @since Feb 24, 2011
 * @param <V> the base type of this channel
 */
public class EquidistantTimeBinsIterator<V> implements ValueIterator {

    private static final Logger LOG =
            CentralLogger.getInstance().getLogger(EquidistantTimeBinsIterator.class);

    @SuppressWarnings("rawtypes")
    private static final ArchiveSampleToIValueFunction ARCH_SAMPLE_2_IVALUE_FUNC =
        new ArchiveSampleToIValueFunction();


    private final String _channelName;
    private final Collection<IArchiveSample<V, ISystemVariable<V>>> _samples;

    private final TimeInstant _startTime;
    private final TimeInstant _endTime;
    private final ReadableDuration _windowLength;
    //private final TimeInstant _nextWindowEnd;

    private final int _numOfWindows;
    private int _currentWindow = 1;

    private final Iterator<IArchiveSample<V, ISystemVariable<V>>> _samplesIter;
    private IArchiveSample<V, ISystemVariable<V>> _firstSample;
    private final INumericMetaData _meta;
    private SampleAggregator _agg;
    private boolean _noMoreSamples = false;



    /**
     * Constructor.
     * @throws ArchiveServiceException
     * @throws OsgiServiceUnavailableException
     * @throws TypeSupportException
     */
    public EquidistantTimeBinsIterator(@Nonnull final String channelName,
                                       @Nonnull final TimeInstant start,
                                       @Nonnull final TimeInstant end,
                                       @Nullable final IArchiveRequestType type,
                                       final int timeBins) throws OsgiServiceUnavailableException,
                                                                  ArchiveServiceException,
                                                                  TypeSupportException {
        _channelName = channelName;
        _startTime = start;
        _endTime = end;
        _numOfWindows = timeBins;
        if (start.isAfter(end) || _numOfWindows <= 0) {
            throw new IllegalArgumentException("Start time is after end time or number of time bins less equal zero.");
        }

        _samples = retrieveSamplesInInterval(_channelName, _startTime, _endTime, type);

        _meta = getDisplayRangesForChannel(channelName);

        _samplesIter = _samples.iterator();

        _windowLength = calculateWindowLength(_startTime, _endTime, _numOfWindows);

        final IArchiveSample<V, ISystemVariable<V>> lastSampleBeforeInterval = retrieveLastSampleBeforeInterval(_channelName, _startTime);

        if (lastSampleBeforeInterval == null) {
            if (_samplesIter.hasNext()) {
                _firstSample = _samplesIter.next();
                _currentWindow = findWindowOfFirstSample(_firstSample.getSystemVariable().getTimestamp(),
                                                         _startTime,
                                                         _windowLength);
                _noMoreSamples = false;
            } else {
                _noMoreSamples = true;
            }
        }
    }


    @Nonnull
    private Duration calculateWindowLength(@Nonnull final TimeInstant start,
                                           @Nonnull final TimeInstant end,
                                           final int bins) {
        return new Duration((end.getMillis() - start.getMillis()) / bins);
    }


    @Nonnull
    private Collection<IArchiveSample<V, ISystemVariable<V>>> retrieveSamplesInInterval(@Nonnull final String channelName,
                                                                                        @Nonnull final TimeInstant start,
                                                                                        @Nonnull final TimeInstant end,
                                                                                        @Nonnull final IArchiveRequestType type) throws OsgiServiceUnavailableException,
                                                                          ArchiveServiceException {
        final IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        final Collection<IArchiveSample<V, ISystemVariable<V>>> samples = service.readSamples(channelName, start, end, type); // type == null means AUTO/DEFAULT
        return samples;
    }


    @CheckForNull
    private INumericMetaData getDisplayRangesForChannel(@Nonnull final String channelName) throws ArchiveServiceException, OsgiServiceUnavailableException {
        final IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        final IArchiveChannel ch = service.getChannelByName(channelName);
        if (ch == null) {
            throw new ArchiveServiceException("Channel retrieval failed for channel '" + channelName + "'!", null);
        }
        final Limits<?> l = service.readDisplayLimits(channelName);
        if (l != null) {
            return ValueFactory.createNumericMetaData(((Number) l.getLow()).doubleValue(),
                                                      ((Number) l.getHigh()).doubleValue(), 0.0, 0.0, 0.0, 0.0, 0, "none");
        }
        return null;
    }


    private int findWindowOfFirstSample(@Nonnull final TimeInstant sampleTime,
                                        @Nonnull final TimeInstant startTime,
                                        @Nonnull final ReadableDuration windowLength) {
        int i = 1;
        TimeInstant nextWindowEnd =
            TimeInstantBuilder.buildFromMillis(startTime.getMillis()).plusMillis(windowLength.getMillis());
        while (nextWindowEnd.isBefore(sampleTime)) {
            nextWindowEnd = nextWindowEnd.plusMillis(windowLength.getMillis());
            i++;
        }
        return i;
    }


    @CheckForNull
    private IArchiveSample<V, ISystemVariable<V>>
        retrieveLastSampleBeforeInterval(@Nonnull final String channelName,
                                         @Nonnull final TimeInstant start) throws ArchiveServiceException,
                                                                                  OsgiServiceUnavailableException {
        final IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        return service.readLastSampleBefore(channelName, start);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        if (!_noMoreSamples && _currentWindow <= _numOfWindows) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IValue next() throws TypeSupportException {
        if (_noMoreSamples || _currentWindow > _numOfWindows) {
            throw new NoSuchElementException();
        }

        final TimeInstant curWindowEnd = calculateCurrentWindowEndTime(_startTime, _currentWindow, _windowLength);

        if (isFirstSampleInThisWindow(_firstSample, curWindowEnd)) {
            _firstSample =
                aggregateValuesInWindow(_firstSample, curWindowEnd, _samplesIter, _agg);
        }

        final IMinMaxDoubleValue iVal =
            ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(curWindowEnd),
                                                 new Severity("OK"), null, _meta, IValue.Quality.Interpolated,
                                                 new double[]{_agg.getAvg().doubleValue() },
                                                 _agg.getMin().doubleValue(),
                                                 _agg.getMax().doubleValue());

        _currentWindow++;
        return iVal;
    }

    private boolean isFirstSampleInThisWindow(@Nonnull final IArchiveSample<V, ISystemVariable<V>> firstSample,
                                              @Nonnull final TimeInstant curWindowEnd) {
        return curWindowEnd.isAfter(firstSample.getSystemVariable().getTimestamp());
    }


    @Nonnull
    private TimeInstant calculateCurrentWindowEndTime(@Nonnull final TimeInstant startTime,
                                                      final int currentWindow,
                                                      @Nonnull final ReadableDuration windowLength) {
        return startTime.plusMillis(currentWindow*windowLength.getMillis());
    }

    @CheckForNull
    private IArchiveSample<V, ISystemVariable<V>>
    aggregateValuesInWindow(@Nonnull final IArchiveSample<V, ISystemVariable<V>> initSample,
                                @Nonnull final TimeInstant windowEnd,
                                @Nonnull final Iterator<IArchiveSample<V, ISystemVariable<V>>> iter,
                                @Nonnull final SampleAggregator agg) throws TypeSupportException {

        IArchiveSample<V, ISystemVariable<V>> nextSample = initSample; // has to be present, either as last sample from the window before
                                                                       // or if not existing being the first within this window
        agg.reset();
        agg.aggregateNewVal(BaseTypeConversionSupport.toDouble(nextSample.getValue()),
                            nextSample.getSystemVariable().getTimestamp());

        while (iter.hasNext()) {
            nextSample =  iter.next(); // not yet clear whether this one belongs into this window

            final TimeInstant curSampleTime = nextSample.getSystemVariable().getTimestamp();
            if (curSampleTime.isBefore(windowEnd)) { // YES -> aggregate
                agg.aggregateNewVal(BaseTypeConversionSupport.toDouble(nextSample.getValue()),
                                    curSampleTime);
            } else { // NO -> stop here aggregating and return the nextSample
                return nextSample;
            }
        }
        return null; // No next sample outside this window detected.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Nothing to do
    }

}
