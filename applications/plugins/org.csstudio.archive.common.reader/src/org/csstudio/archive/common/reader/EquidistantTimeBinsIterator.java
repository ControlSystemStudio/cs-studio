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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderService;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.BaseTypeConversionSupport;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

/**
 * Generates
 *
 * @author bknerr
 * @since Feb 24, 2011
 */
public class EquidistantTimeBinsIterator implements ValueIterator {

    private static final Logger LOG =
            CentralLogger.getInstance().getLogger(EquidistantTimeBinsIterator.class);

    private final DesyArchiveValueIterator _iter;
    private final double _bins;



    private final ReadableDuration _windowLength;

    private final IValue _lastPriorSample;

    /**
     * Constructor.
     * @throws ArchiveServiceException
     * @throws OsgiServiceUnavailableException
     */
    public EquidistantTimeBinsIterator(@Nonnull final DesyArchiveValueIterator iter,
                                       final int timeBins) throws OsgiServiceUnavailableException, ArchiveServiceException {
        _iter = iter;
        _bins = timeBins;

        if (_bins <= 0) {
            throw new IllegalArgumentException("Number of time bins has to be positive.");
        }

        _lastPriorSample = getLastSampleBeforeInterval(_iter.getChannelName(), iter.getStart());

        _windowLength = new Duration(iter.getEnd().getMillis() - iter.getStart().getMillis());
    }

    @CheckForNull
    private IValue getLastSampleBeforeInterval(@Nonnull final String channelName,
                                               @Nonnull final TimeInstant start) throws ArchiveServiceException, OsgiServiceUnavailableException {
        final IArchiveReaderService service = Activator.getDefault().getArchiveReaderService();

        return service.readLastSampleBefore(channelName, start);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return _currentAvg != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IValue next() throws Exception {


        IValue nextBaseValue = _iter.next();
        while (!BaseTypeConversionSupport.toTimeInstant(nextBaseValue.getTime()).isAfter(nextWindowEnd)) {
            accumulate(nextBaseValue);
            nextBaseValue = _iter.next();
        }
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Nothing to do
    }

}
