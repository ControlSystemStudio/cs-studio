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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractBatchQueueHandler;
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

    private final Logger shutdownLog =
            LoggerFactory.getLogger(ShutdownWorkerThread.class);

    private final PersistEngineDataManager _persistEngineDataManager;
    private final Map<Class<?>, AbstractBatchQueueHandler<?>> _strategyAndBatchMap;



    /**
     * Constructor.
     */
    public ShutdownWorkerThread(@Nonnull final PersistEngineDataManager mgr,
                                @Nonnull final Map<Class<?>, AbstractBatchQueueHandler<?>> strategyAndBatchMap) {
        _persistEngineDataManager = mgr;
        _strategyAndBatchMap = strategyAndBatchMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new PersistDataWorker(_persistEngineDataManager,
                                               "SHUTDOWN MySQL Archive Worker",
                                               Integer.valueOf(0),
                                               _strategyAndBatchMap));
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                shutdownLog.warn("Executor for PersistDataWorkers did not terminate in the specified period. Try to rescue data.");
                for (final AbstractBatchQueueHandler<?> handler : _strategyAndBatchMap.values()) {
                    rescueQueueContent(handler);
                }
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdownNow();
    }

    private <T> void rescueQueueContent(@Nonnull final AbstractBatchQueueHandler<T> handler) {
        final List<T> elements = Lists.newLinkedList();
        handler.getQueue().drainTo(elements);
        final Collection<String> statements = handler.convertToStatementString(elements);
        _persistEngineDataManager.rescueDataToFileSystem(statements);
    }
}
