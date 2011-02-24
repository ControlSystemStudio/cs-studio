/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderService;
import org.csstudio.archive.common.service.requesttypes.IArchiveRequestType;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;

import com.google.common.base.Function;

/**
 * Raw value iterator for service infrastructure.
 *
 * @author bknerr
 * @since 21.12.2010
 */
public class DesyArchiveValueIterator implements ValueIterator {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(DesyArchiveValueIterator.class);

    /**
     * Static converter function.
     *
     * @author bknerr
     * @since 22.12.2010
     */
    public static final class ArchiveSampleToIValueFunction<V> implements
            Function<IArchiveMinMaxSample<V, IAlarmSystemVariable<V>>, IValue> {
        /**
         * Constructor.
         */
        public ArchiveSampleToIValueFunction() {
            // Empty
        }

        @Override
        @CheckForNull
        public IValue apply(@Nonnull final IArchiveMinMaxSample<V, IAlarmSystemVariable<V>> from) {
            try {
                // TODO (bknerr) : support lookup for every single value... check performance
                final V min = from.getMinimum();
                final V max = from.getMaximum();
                if (min != null && max != null) {
                    return SystemVariableSupport.toIMinMaxDoubleValue(from.getSystemVariable(), min, max);
                }
                return SystemVariableSupport.toIValue(from.getSystemVariable());

            } catch (final TypeSupportException e) {
                return null;
            }
        }
    }
    @SuppressWarnings("rawtypes")
    private static final ArchiveSampleToIValueFunction ARCH_SAMPLE_2_IVALUE_FUNC =
        new ArchiveSampleToIValueFunction();



    private final String _channelName;
    private final TimeInstant _start;
    private final TimeInstant _end;

    private final Iterable<IArchiveSample<Object, IAlarmSystemVariable<Object>>> _samples =
        Collections.emptyList();

    private Iterator<IArchiveSample<Object, IAlarmSystemVariable<Object>>> _iterator = _samples.iterator();

    /**
     * Constructor.
     * @throws ArchiveServiceException
     * @throws OsgiServiceUnavailableException
     */
    DesyArchiveValueIterator(@Nonnull final String channelName,
                             @Nonnull final TimeInstant start,
                             @Nonnull final TimeInstant end,
                             @Nullable final IArchiveRequestType type) throws ArchiveServiceException, OsgiServiceUnavailableException {

        _channelName = channelName;
        _start = start;
        _end = end;


        if (_start.isAfter(_end)) {
            throw new IllegalArgumentException("Start time mustn't be after end time");
        }

        final IArchiveReaderService service = Activator.getDefault().getArchiveReaderService();
        _samples = service.readSamples(channelName, start, end, type);

        _iterator = _samples.iterator();
    }

    @Nonnull
    public TimeInstant getStart() {
        return _start;
    }

    @Nonnull
    public TimeInstant getEnd() {
        return _end;
    }

    @Nonnull
    public String getChannelName() {
        return _channelName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return _iterator.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IValue next() throws Exception {
        return _iterator.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Useless here
    }


}
