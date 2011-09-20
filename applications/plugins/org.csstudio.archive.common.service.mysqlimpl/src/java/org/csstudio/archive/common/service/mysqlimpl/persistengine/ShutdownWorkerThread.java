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

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread to hold the shutdown worker on stopping the engine.
 *
 * @author bknerr
 * @since 11.05.2011
 */
final class ShutdownWorkerThread extends Thread {

    private final Logger _shutdownLog =
            LoggerFactory.getLogger(ShutdownWorkerThread.class);

    private final ArchiveConnectionHandler _connectionHandler;
    private final IBatchQueueHandlerProvider _handlerProvider;
    private final Integer _prefTermTimeMS;



    /**
     * Constructor.
     * @param prefTermTimeInMS
     */
    public ShutdownWorkerThread(@Nonnull final ArchiveConnectionHandler connectionHandler,
                                @Nonnull final IBatchQueueHandlerProvider provider,
                                @Nonnull final Integer prefTermTimeInMS) {
        _connectionHandler = connectionHandler;
        _handlerProvider = provider;
        _prefTermTimeMS = prefTermTimeInMS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        _shutdownLog.info("Execute and await termination for maximum {}ms", _prefTermTimeMS);

        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final PersistDataWorker worker =
            new PersistDataWorker(_connectionHandler,
                                  "SHUTDOWN Worker",
                                  Integer.valueOf(0),
                                  _handlerProvider);
        executor.execute(worker);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(_prefTermTimeMS, TimeUnit.MILLISECONDS)) {
                _shutdownLog.warn("Executor for PersistDataWorkers did not terminate in the specified period. Try to rescue data.");
                rescueStatementsOfAllHandlers(worker);
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            _shutdownLog.info("Shutdown now.");
            executor.shutdownNow();
        }
    }

    private void rescueStatementsOfAllHandlers(@Nonnull final PersistDataWorker worker) {
        for (final BatchQueueHandlerSupport<?> handler : _handlerProvider.getHandlers()) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            final Collection<String> statements = handler.convertToStatementString((Collection) handler.getQueue());
            worker.rescueDataToFileSystem(statements);
        }
    }
}
