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
import org.csstudio.archive.common.service.requesttypes.IArchiveRequestType;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.sample.SampleAggregator;
import org.csstudio.archive.common.service.util.ArchiveSampleToIValueFunction;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.data.IMinMaxDoubleValue;
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

    private IArchiveSample<Object, IAlarmSystemVariable<Object>> _lastSample;
    private IArchiveSample<Object, IAlarmSystemVariable<Object>> _nextSample;

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
        _curBinTime = _start;
        _end = end;
        _bins = timeBins;
        if (start.isAfter(end) || _bins <= 0) {
            throw new IllegalArgumentException("Start time is after end time or number of time bins has to be positive.");
        }

        // init the last sample to the one before the interval (if existing)
        _lastSample = getLastSampleBeforeInterval(_channelName, _start);
        // get the samples in the specified interval
        final IArchiveReaderFacade service = Activator.getDefault().getArchiveReaderService();
        _samples = service.readSamples(channelName, start, end, type); // type == null means AUTO/DEFAULT
        _iter = _samples.iterator();
        // calc the windowlength of the bins
        _windowLength = new Duration((_end.getMillis() - _start.getMillis()) / _bins);

        if (_lastSample != null) {
            _agg = new SampleAggregator(BaseTypeConversionSupport.toDouble(_lastSample.getValue()),
                                        _start);
        } else if (!_samples.isEmpty()) {
            _lastSample = _iter.next();
            _nextSample = _lastSample;
            _currentBin = findBinForFirstSample(_nextSample.getSystemVariable().getTimestamp(), _start, _windowLength);
            _agg = new SampleAggregator(BaseTypeConversionSupport.toDouble(_nextSample.getValue()),
                                        _start.plusMillis(_currentBin* _windowLength.getMillis()));
        } else {
            _noSamples = true;
        }
    }


    private int findBinForFirstSample(@Nonnull final TimeInstant time,
                                      @Nonnull final TimeInstant start,
                                      @Nonnull final ReadableDuration windowLength) {
        TimeInstant myStart = TimeInstantBuilder.buildFromMillis(start.getMillis());
        int i = 1;
        while (myStart.plusMillis(windowLength.getMillis()).isBefore(time)) {
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
        if (_currentBin <= _bins && _noSamples) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IValue next() throws TypeSupportException {

        if (_noSamples) {
            throw new NoSuchElementException();
        }

        final TimeInstant windowEnd = _start.plusMillis(_currentBin*_windowLength.getMillis());

        while (_nextSample.getSystemVariable().getTimestamp().isBefore(windowEnd)) {
            final Double newVal = BaseTypeConversionSupport.toDouble(_nextSample.getValue());

            _agg.aggregateNewVal(newVal, _nextSample.getSystemVariable().getTimestamp());

            _nextSample = _iter.next();
        }

        final IMinMaxDoubleValue iVal =
            ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(windowEnd),
                                                 null, null, null, null,
                                                 new double[]{_agg.getAvg().doubleValue() },
                                                 _agg.getMin().doubleValue(),
                                                 _agg.getMax().doubleValue());

        _agg.reset();
        _currentBin++;

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
