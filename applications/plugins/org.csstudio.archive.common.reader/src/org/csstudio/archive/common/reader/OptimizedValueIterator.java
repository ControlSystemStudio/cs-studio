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

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderService;
import org.csstudio.archive.common.service.IArchiveRequestType;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.joda.time.Duration;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 05.01.2011
 */
public class OptimizedValueIterator implements ValueIterator {

    // FIXME (bknerr with kasemir) :
    // the available archive request types should be obtained over the extpoint/service from the
    // implementation, offered to the client. then the client decides for a specific one, and asks for the
    // data with the 'typed' request type information
    private static final class AvgPerHourType implements IArchiveRequestType {
        @Override
        public String getTypeIdentifier() {
            return "AVG_PER_HOUR";
        }

        @Override
        public String getDescription() {
            return "blubb";
        }
    }
    private static final AvgPerHourType APH_TYPE = new AvgPerHourType();

    private static final class AvgPerMinType implements IArchiveRequestType {
        @Override
        public String getTypeIdentifier() {
            return "AVG_PER_MINUTE";
        }

        @Override
        public String getDescription() {
            return "blubb";
        }
    }
    private static final AvgPerMinType APM_TYPE = new AvgPerMinType();

    public static final class RawType implements IArchiveRequestType {
        // TODO (bknerr with kasemir) :
        // the available archive request types can be obtained over the service from the
        // implementation, offered to the client, the client decides for a specific one, and asks for the
        // data with the 'typed' request type information
        @Override
        @Nonnull
        public String getTypeIdentifier() {
            return "RAW";
        }

        @Override
        @Nonnull
        public String getDescription() {
            return "Complete set of existing (raw) values in the time interval.";
        }
    }
    public static final RawType RAW_TYPE = new RawType();


    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(OptimizedValueIterator.class);

    private Iterable<IValue> _values = Collections.emptyList();
    private Iterator<IValue> _iterator = _values.iterator();

    /**
     * Constructor.
     */
    OptimizedValueIterator(@Nonnull final String channelName,
                           @Nonnull final ITimestamp start,
                           @Nonnull final ITimestamp end,
                           @Nonnull final int bins) {

        IArchiveReaderService service;
        try {
            service = Activator.getDefault().getArchiveReaderService();

            // TODO (bknerr with kasemir) :
            // the available archive request types can be obtained over the service from the
            // implementation, offered to the client, the client decides for a specific one, and asks for the
            // data with the 'typed' request type information

            // Decide which value set is best
            final long interval = end.seconds() - start.seconds();
            if (Duration.standardSeconds(interval).isLongerThan(Duration.standardDays(30))) {
                // Longer than a month, use the 'per hour'-aggregation
                _values = service.readSamples(channelName, start, end, APH_TYPE);
            } else if (Duration.standardSeconds(interval).isLongerThan(Duration.standardDays(1))) {
                // Longer than a month, use the 'per hour'-aggregation
                _values = service.readSamples(channelName, start, end, APM_TYPE);
            } else {
                // Others
                _values = service.readSamples(channelName, start, end, RAW_TYPE);
            }
        } catch (final ArchiveServiceException e) {
            LOG.error("Failure on retrieving samples from service layer.", e);
        } catch (final OsgiServiceUnavailableException e1) {
            LOG.error("Archive service not present - forgot to (auto-)start?", e1);
        }
        _iterator = _values.iterator();
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
