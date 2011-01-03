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

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveReaderServiceTracker;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderService;
import org.csstudio.archivereader.ValueIterator;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Reduced value set iterator for service infrastructure.
 *
 * @author bknerr
 * @since 21.12.2010
 */
public class ServiceUsingValueIterator implements ValueIterator {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ServiceUsingValueIterator.class);

    // Anti pattern galore - but minimally invasive
    private static ArchiveReaderServiceTracker TRACKER =
        new ArchiveReaderServiceTracker(Activator.getDefault().getContext());
    static {
        TRACKER.open();
    }

    private Iterable<IValue> _values;
    private final Iterator<IValue> _iterator;

    /**
     * Constructor.
     */
    ServiceUsingValueIterator(final String channelName,
                              final ITimestamp start,
                              final ITimestamp end) {

        final IArchiveReaderService service = (IArchiveReaderService) TRACKER.getService();

        try {
            _values = service.readSamples(channelName, start, end);
        } catch (final ArchiveServiceException e) {
            LOG.error("Failure on retrieving samples from service layer.", e);
            _values = Collections.emptyList();
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
