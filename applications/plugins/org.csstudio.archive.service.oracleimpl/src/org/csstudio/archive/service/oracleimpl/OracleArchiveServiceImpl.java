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
package org.csstudio.archive.service.oracleimpl;

import java.util.List;
import java.util.Map;

import org.csstudio.archive.rdb.RDBArchive;
import org.csstudio.archive.service.IArchiveService;
import org.csstudio.platform.data.IValue;

/**
 * Example archive service implementation to separate the processing and logic layer from
 * the data access layer.
 *
 * TODO: Gather here all accesses to the database (best via DAOs).
 *       Should be moved to another plugin that can be loaded/unloaded via
 *       OSGi dynamic services (tracker or declarative)
 *
 * @author bknerr
 * @since 01.11.2010
 */
public enum OracleArchiveServiceImpl implements IArchiveService {

    INSTANCE;

    public static final String ARCHIVE_PREF_KEY = "archive";

    /**
     * In case there'll be several WriteThreads later on.
     */
    private final ThreadLocal<RDBArchive> _archive = new ThreadLocal<RDBArchive>();

    /**
     * {@inheritDoc}
     */
    synchronized public void connect(final Map<String, Object> prefs) {
        final RDBArchive rdbArchive = (RDBArchive) prefs.get(ARCHIVE_PREF_KEY);
        if (rdbArchive == null) {
            throw new IllegalArgumentException("RDBArchive is not set for ArchiveService.");
        }
        _archive.set(rdbArchive);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     *  For performance reasons, this call actually only adds
     *  the sample to a 'batch'.
     *  Need to follow up with <code>RDBArchive.commitBatch()</code> when done.
     */
    public boolean writeSamples(final int channelId, final List<IValue> samples) throws Exception { // TODO : Untyped exception? A catch would swallow ALL exceptions!

//        for (final IValue sample : samples) {
//            _archive.get().batchSample(channelId, sample);
//            // certainly, batching *could* be done in the processing layer, leaving the commitBatch for here,
//            // but that would break encapsulation...
//        }
//        _archive.get().commitBatch();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean writeSample(final int channelId, final IValue sample) throws Exception { // TODO : Untyped exception? A catch would swallow ALL exceptions!
//        _archive.get().batchSample(channelId, sample);
//        _archive.get().commitBatch();
        return true;
    }
}
