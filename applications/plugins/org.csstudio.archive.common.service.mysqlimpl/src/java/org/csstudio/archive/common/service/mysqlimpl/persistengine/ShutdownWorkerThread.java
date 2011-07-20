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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Thread to hold the shutdown worker on stopping the engine.
 *
 * @author bknerr
 * @since 11.05.2011
 */
final class ShutdownWorkerThread extends Thread {
    private final PersistEngineDataManager _persistEngineDataManager;

    private final Logger shutdownLog =
            LoggerFactory.getLogger(ShutdownWorkerThread.class);

    private final ScheduledThreadPoolExecutor _exec;
    private final SqlStatementBatch _batch;
    private final Integer _maxAllowedPacketSize;

    /**
     * Constructor.
     */
    public ShutdownWorkerThread(@Nonnull final PersistEngineDataManager mgr,
                                @Nonnull final ScheduledThreadPoolExecutor executor,
                                @Nonnull final SqlStatementBatch batch,
                                @Nonnull final Integer maxAllowedPacketInBytes) {
        _persistEngineDataManager = mgr;
        _exec = executor;
        _batch = batch;
        _maxAllowedPacketSize = maxAllowedPacketInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (!_exec.isTerminating()) {
            _exec.execute(new PersistDataWorker(_persistEngineDataManager,
                                                "SHUTDOWN MySQL Archive Worker",
                                                _batch,
                                                Integer.valueOf(0),
                                                _maxAllowedPacketSize));
            _exec.shutdown();
            try {
                if (!_exec.awaitTermination(_maxAllowedPacketSize + 1, TimeUnit.MILLISECONDS)) {
                    shutdownLog.warn("Executor for PersistDataWorkers did not terminate in the specified period. Try to rescue data.");
                    final List<String> statements = Lists.newLinkedList();
                    _batch.drainTo(statements);
                    _persistEngineDataManager.rescueDataToFileSystem(statements);
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
