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
import java.util.Comparator;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.domain.desy.DesyRunContext;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Manager that handles the persistence worker thread.
 * @author Bastian Knerr
 * @since Feb 26, 2011
 */
public class PersistEngineDataManager {

    // TODO (bknerr) : number of threads?
    // get no of cpus and expected no of archive engines, and available archive connections
    private final int _cpus = Runtime.getRuntime().availableProcessors();
    /**
     * The thread pool executor for the periodically scheduled workers.
     */
    private final ScheduledThreadPoolExecutor _executor =
        (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Math.max(2, _cpus + 1));
//jhatje 2.2.12: set to 1 Thread
//jhatje 22.2.12: back to previous thread number
//    private final ScheduledThreadPoolExecutor _executor =
//            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    /**
     * Sorted set for submitted periodic workers - decreasing by period
     */
    private final SortedSet<PersistDataWorker> _submittedWorkers =
        Sets.newTreeSet(new Comparator<PersistDataWorker>() {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public int compare(@Nonnull final PersistDataWorker arg0,
                                               @Nonnull final PersistDataWorker arg1) {
                                return Long.valueOf(arg0.getPeriodInMS()).compareTo(Long.valueOf(arg1.getPeriodInMS()));
                            }
                        });
    private final AtomicInteger _workerId = new AtomicInteger(0);

    private final Integer _prefPeriodInMS;
    private final Integer _prefTermTimeInMS;

    private final ArchiveConnectionHandler _connectionHandler;

    private final IBatchQueueHandlerProvider _allHandlersProvider =
        new IBatchQueueHandlerProvider() {
            @SuppressWarnings("rawtypes")
            @Override
            @Nonnull
            public Collection<BatchQueueHandlerSupport> getHandlers() {
                return BatchQueueHandlerSupport.getInstalledHandlers();
            }
        };

    /**
     * Constructor.
     */
    @Inject
    public PersistEngineDataManager(@Nonnull final ArchiveConnectionHandler connectionHandler,
                                    @Nonnull final MySQLArchivePreferenceService prefs) {
        _connectionHandler = connectionHandler;

        _prefPeriodInMS = prefs.getPeriodInMS();
        _prefTermTimeInMS = prefs.getTerminationTimeInMS();

        addGracefulShutdownHook(_connectionHandler, _allHandlersProvider, _prefTermTimeInMS);
    }

    private void submitNewPersistDataWorker(@Nonnull final ScheduledThreadPoolExecutor executor,
                                            @Nonnull final ArchiveConnectionHandler connectionHandler,
                                            @Nonnull final Integer prefPeriodInMS,
                                            @Nonnull final IBatchQueueHandlerProvider handlerProvider,
                                            @Nonnull final AtomicInteger workerId,
                                            @Nonnull final SortedSet<PersistDataWorker> submittedWorkers) {

        final PersistDataWorker newWorker = new PersistDataWorker(connectionHandler,
                                                                  "PERIODIC Worker: " + workerId.getAndIncrement(),
                                                                  prefPeriodInMS,
                                                                  handlerProvider);
        executor.scheduleAtFixedRate(newWorker,
                                     0L,
                                     newWorker.getPeriodInMS(),
                                     TimeUnit.MILLISECONDS);
        submittedWorkers.add(newWorker);
    }

    /**
     * This shutdown hook is only added when the sys property context is not set to "CI",
     * meaning continuous integration. This is a flaw as the production code should be unaware
     * of its run context, but we couldn't think of another option.
     * @param prefTermTimeInMS
     */
    private void addGracefulShutdownHook(@Nonnull final ArchiveConnectionHandler connectionHandler,
                                         @Nonnull final IBatchQueueHandlerProvider provider,
                                         @Nonnull final Integer prefTermTimeInMS) {
        if (DesyRunContext.isProductionContext()) {
            /**
             * Add shutdown hook.
             */
            Runtime.getRuntime().addShutdownHook(new ShutdownWorkerThread(connectionHandler,
                                                                          provider,
                                                                          prefTermTimeInMS));
        }
    }

    /**
     * Checks whether we need another worker.
     * First check is whether the blocking queue of statements exceeds the max allowed packet size.
     * If so, is there still space in the thread pool for another periodic task.
     * If not so, is there the possibility to replace a rarely scheduled task with a task with higher
     * frequency.
     * If not so, FIXME (bknerr) : start a data rescue worker to save the stuff to disc and inform the staff per email
     * @return
     */
    private boolean isAnotherWorkerRequired() {
        if (noWorkerPresentYet()) {
            return true;
        }
//
//
//
//        if (isMaxPoolSizeNotReached()) {
//            submitNewPersistDataWorker(_executor,
//                                       _prefPeriodInMS,
//                                       _allHandlersProvider,
//                                       _workerId,
//                                       _submittedWorkers);
//        } else {
//            lowerPeriodOfExistingWorker(_executor,
//                                        _prefPeriodInMS,
//                                        _allHandlersProvider,
//                                        _workerId,
//                                        _submittedWorkers);
//        }

        return false;
    }

    private boolean noWorkerPresentYet() {
        return _executor.getPoolSize() <= 0;
    }

//    private boolean isMaxPoolSizeNotReached() {
//        return _executor.getPoolSize() < _executor.getCorePoolSize();
//    }
//    private void lowerPeriodOfExistingWorker(ScheduledThreadPoolExecutor executor,
//                                             Integer prefPeriodInMS,
//                                             IBatchQueueHandlerProvider handlerProvider,
//                                             AtomicInteger workerId,
//                                             SortedSet<PersistDataWorker> submittedWorkers) {
//
//    }

//
//    private boolean isPeriodAlreadySetToMinimum(final long period) {
//        return Long.valueOf(period).intValue() <= 2000;
//    }
//
//    private void handlePoolExhaustionWithMinimumPeriodCornerCase() {
//        // FIXME (bknerr) : handle pool and thread frequency exhaustion
//        // notify staff, rescue data to disc with dedicated worker
//    }
//
//    private void lowerPeriodAndRemoveOldestWorker(@Nonnull final Iterator<PersistDataWorker> it,
//                                                  @Nonnull final PersistDataWorker oldestWorker) {
//        _prefPeriodInMS = Math.max(_prefPeriodInMS>>1, 2000);
//        LOG.info("Remove Worker: " + oldestWorker.getName());
//        _executor.remove(oldestWorker);
//        it.remove();
//    }


    @Nonnull
    public ArchiveConnectionHandler getConnectionHandler() {
        return _connectionHandler;
    }

    public void shutdown() {
        _executor.shutdown();
    }

    public void submitToBatch(@Nonnull final Collection<?> entries) throws TypeSupportException {

        BatchQueueHandlerSupport.addToQueue(entries);

        if (isAnotherWorkerRequired()) {
            submitNewPersistDataWorker(_executor,
                                       _connectionHandler,
                                       _prefPeriodInMS,
                                       _allHandlersProvider,
                                       _workerId,
                                       _submittedWorkers);
        }
    }
}
