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
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.data.IMinMaxDoubleValue;
import org.csstudio.platform.data.INumericMetaData;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

/**
 * Generates an iterator for channels that are
 *
 * @author bknerr
 * @since Feb 24, 2011
 */
public class EquidistantTimeBinsIterator implements ValueIterator {

    private static final Logger LOG =
            CentralLogger.getInstance().getLogger(EquidistantTimeBinsIterator.class);

    @SuppressWarnings("rawtypes")
    private static final ArchiveSampleToIValueFunction ARCH_SAMPLE_2_IVALUE_FUNC =
        new ArchiveSampleToIValueFunction();


    private final String _channelName;
    private final Collection<IArchiveSample<Object, IAlarmSystemVariable<Object>>> _samples;
    private final TimeInstant _start;
    private final TimeInstant _curBinTime;
    private final TimeInstant _end;
    private final int _bins;
    private int _currentBin = 1;
    private final Iterator<IArchiveSample<Object, IAlarmSystemVariable<Object>>> _iter;

    private final ReadableDuration _windowLength;

    private IArchiveSample<Object, IAlarmSystemVariable<Object>> _lastSampleBeforeInterval;
    private IArchiveSample<Object, IAlarmSystemVariable<Object>> _nextSample;

    private SampleAggregator _agg;
    private boolean _noSamples = false;
    private final INumericMetaData _meta;



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
        _curBinTime = _start;
        _end = end;
        _bins = timeBins;
        if (start.isAfter(end) || _bins <= 0) {
            throw new IllegalArgumentException("Start time is after end time or number of time bins has to be positive.");
        }

        // init the last sample to the one before the interval (if existing)
        _lastSampleBeforeInterval = getLastSampleBeforeInterval(_channelName, _start);
        // get the samples in the specified interval
        final IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        _samples = service.readSamples(channelName, start, end, type); // type == null means AUTO/DEFAULT

        _meta = getDisplayRangesForChannel(channelName);

        _iter = _samples.iterator();
        // calc the windowlength of the bins
        _windowLength = new Duration((_end.getMillis() - _start.getMillis()) / _bins);

        if (_lastSampleBeforeInterval != null) {
            _agg = new SampleAggregator(BaseTypeConversionSupport.toDouble(_lastSampleBeforeInterval.getValue()),
                                        _start);
        } else if (!_samples.isEmpty()) { // no last samples present
            _lastSampleBeforeInterval = _iter.next();   // set to the first sample find in the range
            _nextSample = _lastSampleBeforeInterval;
            _currentBin = findBinOfFirstSample(_nextSample.getSystemVariable().getTimestamp(),
                                               _start,
                                               _windowLength);
            _agg = new SampleAggregator(BaseTypeConversionSupport.toDouble(_nextSample.getValue()),
                                        _start.plusMillis(_currentBin* _windowLength.getMillis()));
        } else {
            _noSamples = true;
        }
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


    private int findBinOfFirstSample(@Nonnull final TimeInstant time,
                                     @Nonnull final TimeInstant start,
                                     @Nonnull final ReadableDuration windowLength) {
        int i = 1;
        TimeInstant myStart =
            TimeInstantBuilder.buildFromMillis(start.getMillis()).plusMillis(windowLength.getMillis());
        while (myStart.isBefore(time)) {
            myStart = myStart.plusMillis(windowLength.getMillis());
            i++;
        }
        return i;
    }


    @CheckForNull
    private IArchiveSample<Object, IAlarmSystemVariable<Object>>
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
        if (_currentBin <= _bins && _iter.hasNext()) { // TODO (bknerr) : check whether only the last condition is sufficient
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
        if (_noSamples) {
            throw new NoSuchElementException();
        }

        final TimeInstant windowEnd = _start.plusMillis(_currentBin*_windowLength.getMillis());

        // is there a yet unaggregated sample from the last next() invocation or the constructor?
        if (_nextSample != null) {
            // Yes
            // Is it in this time window (when present from the constructor, yes, when from the next invoc, then dunno?
            if (_nextSample.getSystemVariable().getTimestamp().isBefore(windowEnd)) { // YES,
                _agg.aggregateNewVal(BaseTypeConversionSupport.toDouble(_nextSample.getValue()),
                                     _nextSample.getSystemVariable().getTimestamp());

                // start iterating over next samples until window end
                while (_iter.hasNext()) {
                    _nextSample = _iter.next(); // might belong here or not

                    if (_nextSample.getSystemVariable().getTimestamp().isBefore(windowEnd)) {
                        _agg.aggregateNewVal(BaseTypeConversionSupport.toDouble(_nextSample.getValue()),
                                             _nextSample.getSystemVariable().getTimestamp());
                        _nextSample = null; // set to null, when already aggregated
                    } else {
                        // nextSample NOT set to null, as not yet aggregated
                        break;
                    }
                }
            }
        }
        final IMinMaxDoubleValue iVal =
            ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(windowEnd),
                                                 null, null, _meta, IValue.Quality.Interpolated,
                                                 new double[]{_agg.getAvg().doubleValue() },
                                                 _agg.getMin().doubleValue(),
                                                 _agg.getMax().doubleValue());
        _agg.reset();
        _currentBin++;

        if (_nextSample != null) {
            // this sample has been retrieved from the iterator, but was outside the current window
            // aggregate it here
            _agg.aggregateNewVal(BaseTypeConversionSupport.toDouble(_nextSample.getValue()),
                                 _nextSample.getSystemVariable().getTimestamp());
        }
        return iVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Nothing to do
    }

}
