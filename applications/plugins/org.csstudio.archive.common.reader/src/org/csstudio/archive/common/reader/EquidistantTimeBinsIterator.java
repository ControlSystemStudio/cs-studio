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

    private final TimeInstant _start;
    private final TimeInstant _end;
    private final ReadableDuration _windowLength;
    private final TimeInstant _nextWindowEnd;

    private final int _bins;
    private int _currentBin;

    private final Iterator<IArchiveSample<V, ISystemVariable<V>>> _iter;
    private IArchiveSample<V, ISystemVariable<V>> _nextSample;
    private final INumericMetaData _meta;
    private SampleAggregator _agg;
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
        _start = start;
        _end = end;
        _bins = timeBins;
        if (start.isAfter(end) || _bins <= 0) {
            throw new IllegalArgumentException("Start time is after end time or number of time bins less equal zero.");
        }

        // init the last sample to the one before the interval (if existing)
        _nextSample = getLastSampleBeforeInterval(_channelName, _start);

        // get the samples in the specified interval
        final IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        _samples = service.readSamples(channelName, start, end, type); // type == null means AUTO/DEFAULT
        _iter = _samples.iterator();


        _windowLength = new Duration((_end.getMillis() - _start.getMillis()) / _bins);

        if (_nextSample == null) { // no samples present before the given start time
            if (_samples.isEmpty()) {
                _currentBin = _bins + 1;
                _noSamples = true;
                _meta = null;
            } else {
                _nextSample = _iter.next();
                // in which bin is it - fast forward to that one
                _currentBin = findBinOfNextSample(_nextSample.getSystemVariable().getTimestamp(),
                                                  _start,
                                                  _windowLength);
                _meta = getDisplayRangesForChannel(channelName);
            }
        } else {
            _currentBin = 1;
            _meta = getDisplayRangesForChannel(channelName);
        }
        _nextWindowEnd = _start.plusMillis(_currentBin * _windowLength.getMillis());
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


    private int findBinOfNextSample(@Nonnull final TimeInstant sampleTime,
                                    @Nonnull final TimeInstant start,
                                    @Nonnull final ReadableDuration windowLength) {
        int i = 1;
        TimeInstant nextWindowEnd =
            TimeInstantBuilder.buildFromMillis(start.getMillis()).plusMillis(windowLength.getMillis());
        while (nextWindowEnd.isBefore(sampleTime)) {
            nextWindowEnd = nextWindowEnd.plusMillis(windowLength.getMillis());
            i++;
        }
        return i;
    }


    @CheckForNull
    private IArchiveSample<V, ISystemVariable<V>>
        getLastSampleBeforeInterval(@Nonnull final String channelName,
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
        if (!_noSamples && _currentBin <= _bins) {
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
        if (_noSamples && _currentBin < _bins) {
            throw new NoSuchElementException();
        }

        final TimeInstant windowEnd = _start.plusMillis(_currentBin*_windowLength.getMillis());
        if (_nextSample != null) { // There are more samples to be aggregated
            // Is the _nextSample in this window?
            final int offset = findBinOfNextSample(_nextSample.getSystemVariable().getTimestamp(),
                                             windowEnd,
                                             _windowLength);
            if (offset <= 1) { // YES, start aggregating this window anew
                _nextSample =
                    aggregateValuesInNextWindow(_nextSample, windowEnd, _iter, _agg);
            }
            _currentBin++;
        }

        final IMinMaxDoubleValue iVal =
            ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(windowEnd),
                                                 new Severity("OK"), null, _meta, IValue.Quality.Interpolated,
                                                 new double[]{_agg.getAvg().doubleValue() },
                                                 _agg.getMin().doubleValue(),
                                                 _agg.getMax().doubleValue());
        return iVal;
    }

    /**
     *
     * @param <V>
     * @param initSample
     * @param windowEnd
     * @param windowLength
     * @param iter
     * @return
     * @throws TypeSupportException
     */
    @CheckForNull
    private <V> IArchiveSample<V, ISystemVariable<V>>
    aggregateValuesInNextWindow(@Nonnull final IArchiveSample<V, ISystemVariable<V>> initSample,
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
