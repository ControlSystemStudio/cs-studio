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

import org.csstudio.archive.common.requesttype.IArchiveRequestType;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.sample.SampleMinMaxAggregator;
import org.csstudio.archivereader.Severity;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.data.values.INumericMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
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

    /**
     * Result container for method
     * {@link EquidistantTimeBinsIterator#findWindowOfFirstSample(TimeInstant, TimeInstant, ReadableDuration)}.
     *
     * @author bknerr
     * @since Mar 16, 2011
     */
    private static final class SampleAndWindow<V> {
        private final IArchiveSample<V, ISystemVariable<V>>_sample;
        private final int _window;
        /**
         * Constructor.
         */
        public SampleAndWindow(@Nullable final IArchiveSample<V, ISystemVariable<V>> sample,
                               final int window) {
            _sample = sample;
            _window = window;
        }
        public int getWindow() {
            return _window;
        }
        @CheckForNull
        public IArchiveSample<V, ISystemVariable<V>> getSample() {
            return _sample;
        }


    }

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
    private final INumericMetaData _metaData;
    private final SampleMinMaxAggregator _agg;
    private boolean _noSamples = false;

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

        if (_startTime.isAfter(_endTime) || _numOfWindows <= 0) {
            throw new IllegalArgumentException("Start time is after end time or number of time bins less equal zero.");
        }

        _samples = retrieveSamplesInInterval(_channelName, _startTime, _endTime, type);
        _samplesIter = _samples.iterator();

        _metaData = retrieveMetaDataForChannel(channelName);

        _windowLength = calculateWindowLength(_startTime, _endTime, _numOfWindows);

        final SampleAndWindow<V> saw = findFirstSampleAndItsWindow(channelName, _startTime, _windowLength, _samplesIter);

        _firstSample = saw.getSample();
        _currentWindow = saw.getWindow();

        _noSamples = _firstSample == null;

        _agg = initAggregator(_firstSample);
    }


    @Nonnull
    private SampleMinMaxAggregator initAggregator(@CheckForNull final IArchiveSample<V, ISystemVariable<V>> sample) throws TypeSupportException {
        if (sample == null) {
            return new SampleMinMaxAggregator();
        }
        final IArchiveMinMaxSample<V, ISystemVariable<V>> mmSample = (IArchiveMinMaxSample<V, ISystemVariable<V>>) sample;
        final V minimum = mmSample.getMinimum();
        final V maximum = mmSample.getMaximum();

        final Double value = BaseTypeConversionSupport.toDouble(mmSample.getValue());
        return new SampleMinMaxAggregator(value,
                                          minimum == null ? value : BaseTypeConversionSupport.toDouble(minimum),
                                          maximum == null ? value : BaseTypeConversionSupport.toDouble(maximum),
                                          mmSample.getSystemVariable().getTimestamp());
    }


    @Nonnull
    private SampleAndWindow<V> findFirstSampleAndItsWindow(@Nonnull final String name,
                                                           @Nonnull final TimeInstant startTime,
                                                           @Nonnull final ReadableDuration windowLength,
                                                           @CheckForNull final Iterator<IArchiveSample<V, ISystemVariable<V>>> samplesIter) throws ArchiveServiceException,
                                                                              OsgiServiceUnavailableException {

        final IArchiveSample<V, ISystemVariable<V>> lastSampleBeforeStartTime = retrieveLastSampleBeforeInterval(name, startTime);

        if (lastSampleBeforeStartTime != null) {
            return new SampleAndWindow<V>(lastSampleBeforeStartTime, 1);
        } else {
            if (samplesIter.hasNext()) {
                final IArchiveSample<V, ISystemVariable<V>> firstSampleInWindow = samplesIter.next();
                final int window = findWindowOfFirstSample(firstSampleInWindow.getSystemVariable().getTimestamp(),
                                                         startTime,
                                                         windowLength);
                return new SampleAndWindow<V>(firstSampleInWindow, window);
            }
        }
        return new SampleAndWindow<V>(null, 1);
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
        return service.readSamples(channelName, start, end, type);
    }



    @CheckForNull
    private INumericMetaData retrieveMetaDataForChannel(@Nonnull final String channelName) throws ArchiveServiceException, OsgiServiceUnavailableException {
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
            TimeInstantBuilder.fromMillis(startTime.getMillis()).plusMillis(windowLength.getMillis());
        while (sampleTime.isAfter(nextWindowEnd)) {
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
        if (!_noSamples && _currentWindow <= _numOfWindows) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IValue next() throws Exception {
        if (_noSamples || _currentWindow > _numOfWindows) {
            throw new NoSuchElementException();
        }

        final TimeInstant curWindowEnd = calculateCurrentWindowEndTime(_startTime, _currentWindow, _windowLength);

        if (_firstSample != null && hasTimestampBeforeWindowEnd(_firstSample, curWindowEnd)) {
            _firstSample =
                aggregateSamplesUntilWindowEnd(_firstSample, curWindowEnd, _samplesIter, _agg);
        }

        final IValue iVal = createMinMaxDoubleValue(curWindowEnd, _metaData, _agg);

        _currentWindow++;
        return iVal;
    }

    @Nonnull
    private IValue createMinMaxDoubleValue(@Nonnull final TimeInstant curWindowEnd,
                                           @Nonnull final INumericMetaData metaData,
                                           @Nonnull final SampleMinMaxAggregator agg) throws Exception {
        final Double avg = agg.getAvg();
        final Double min = agg.getMin();
        final Double max = agg.getMax();

        if (avg != null && min != null && max != null) {
            return ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(curWindowEnd),
                                                        new Severity("OK"), null, metaData, IValue.Quality.Interpolated,
                                                        new double[]{avg.doubleValue()},
                                                        min.doubleValue(),
                                                        max.doubleValue());
        }
        throw new Exception("Creation of MinMaxDoubleValue failed. " + SampleMinMaxAggregator.class.getName() + " returned null values.");
    }

    private boolean hasTimestampBeforeWindowEnd(@Nonnull final IArchiveSample<V, ISystemVariable<V>> firstSample,
                                              @Nonnull final TimeInstant curWindowEnd) {
        return curWindowEnd.isAfter(firstSample.getSystemVariable().getTimestamp());
    }


    @Nonnull
    private TimeInstant calculateCurrentWindowEndTime(@Nonnull final TimeInstant startTime,
                                                      final int currentWindow,
                                                      @Nonnull final ReadableDuration windowLength) {
        return startTime.plusMillis(currentWindow*windowLength.getMillis());
    }

    /**
     * Uses iterator to find samples before current window end and aggregates all those found.
     * Returns either the first sample provided by the iterator that lies outside the current window
     * or <code>null</code> if there isn't any further sample.
     */
    @CheckForNull
    private IArchiveSample<V, ISystemVariable<V>>
    aggregateSamplesUntilWindowEnd(@Nonnull final IArchiveSample<V, ISystemVariable<V>> initSample,
                                   @Nonnull final TimeInstant windowEnd,
                                   @Nonnull final Iterator<IArchiveSample<V, ISystemVariable<V>>> iter,
                                   @Nonnull final SampleMinMaxAggregator aggregator) throws TypeSupportException {


        aggregator.reset();
        aggregateMinMaxSample(aggregator, (IArchiveMinMaxSample<V, ISystemVariable<V>>) initSample);

        IArchiveMinMaxSample<V, ISystemVariable<V>> nextSample =
            (IArchiveMinMaxSample<V, ISystemVariable<V>>) initSample;

        while (iter.hasNext()) {
            nextSample =  (IArchiveMinMaxSample<V, ISystemVariable<V>>) iter.next();

            final TimeInstant curSampleTime = nextSample.getSystemVariable().getTimestamp();
            if (curSampleTime.isBefore(windowEnd)) {
                aggregateMinMaxSample(aggregator, nextSample);
            } else {
                return nextSample;
            }
        }
        return null;
    }


    private void aggregateMinMaxSample(@Nonnull final SampleMinMaxAggregator aggregator,
                                       @Nonnull final IArchiveMinMaxSample<V, ISystemVariable<V>> sample) throws TypeSupportException {
        final Double value = BaseTypeConversionSupport.toDouble(sample.getValue());
        final V minimum = sample.getMinimum();
        final V maximum = sample.getMaximum();
        final TimeInstant curSampleTime = sample.getSystemVariable().getTimestamp();

        aggregator.aggregate(value,
                             minimum == null ? value : BaseTypeConversionSupport.toDouble(minimum),
                             maximum == null ? value : BaseTypeConversionSupport.toDouble(maximum),
                             curSampleTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Nothing to do
    }

}
