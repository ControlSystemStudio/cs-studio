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

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.BatchQueueHandler;
import org.csstudio.archive.common.service.mysqlimpl.notification.ArchiveNotifications;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.DesyRunContext;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Manager that handles the persistence worker thread.
 * @author Bastian Knerr
 * @since Feb 26, 2011
 */
public class PersistEngineDataManager {

    private static final Logger LOG =
        LoggerFactory.getLogger(PersistEngineDataManager.class);


    // TODO (bknerr) : number of threads?
    // get no of cpus and expected no of archive engines, and available archive connections
    private final int _cpus = Runtime.getRuntime().availableProcessors();
    /**
     * The thread pool executor for the periodically scheduled workers.
     */
    private final ScheduledThreadPoolExecutor _executor =
        (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Math.max(2, _cpus + 1));

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

    private Integer _prefPeriodInMS;
    private final Integer _prefMaxAllowedPacketInBytes;
    private final File _prefRescueDir;
    private final String _prefSmtpHost;
    private final String _prefEmailAddress;

    private final ArchiveConnectionHandler _connectionHandler;

    private final Map<Class<?>, BatchQueueHandler<?>> _strategyAndBatchMap = Maps.newConcurrentMap();

    /**
     * Constructor.
     */
    @Inject
    public PersistEngineDataManager(@Nonnull final ArchiveConnectionHandler connectionHandler,
                                    @Nonnull final MySQLArchivePreferenceService prefs) {
        _connectionHandler = connectionHandler;
        _prefMaxAllowedPacketInBytes = prefs.getMaxAllowedPacketSizeInKB()*1024;
        _prefPeriodInMS = prefs.getPeriodInMS();
        _prefRescueDir = prefs.getDataRescueDir();
        _prefSmtpHost = prefs.getSmtpHost();
        _prefEmailAddress = prefs.getEmailAddress();

        addGracefulShutdownHook(_strategyAndBatchMap);
    }

    private void submitNewPersistDataWorker() {
        final PersistDataWorker newWorker = new PersistDataWorker(this,
                                                                  "PERIODIC MySQL Archive Worker: " + _workerId.getAndIncrement(),
                                                                  _prefPeriodInMS,
                                                                  _strategyAndBatchMap);
        _executor.scheduleAtFixedRate(newWorker,
                                      0L,
                                      newWorker.getPeriodInMS(),
                                      TimeUnit.MILLISECONDS);
        _submittedWorkers.add(newWorker);
    }

    /**
     * This shutdown hook is only added when the sys property context is not set to "CI",
     * meaning continuous integration. This is a flaw as the production code should be unaware
     * of its run context, but we couldn't think of another option.
     */
    private void addGracefulShutdownHook(@Nonnull final Map<Class<?>, BatchQueueHandler<?>> strategyAndBatchMap) {
        if (DesyRunContext.isProductionContext()) {
            /**
             * Add shutdown hook.
             */
            Runtime.getRuntime().addShutdownHook(new ShutdownWorkerThread(this,
                                                                          strategyAndBatchMap));
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

        return false;

//        if (doesSqlQueueLengthExceedMaxAllowedPacketSize()) {
//            if (poolSizeExhausted()) {
//                final Iterator<PersistDataWorker> it = _submittedWorkers.iterator();
//                final PersistDataWorker oldestWorker = it.next();
//                final long period = oldestWorker.getPeriodInMS();
//                if (isPeriodAlreadySetToMinimum(period)) {
//                    handlePoolExhaustionWithMinimumPeriodCornerCase();
//                    return false;
//                }
//                lowerPeriodAndRemoveOldestWorker(it, oldestWorker);
//            }
//            return true;
//        }
//        return false;
    }

    private boolean noWorkerPresentYet() {
        return _executor.getPoolSize() <= 0;
    }

//    private boolean doesSqlQueueLengthExceedMaxAllowedPacketSize() {
//        return _sqlStatementBatch.sizeInBytes() > _prefMaxAllowedPacketInBytes;
//    }

    private boolean poolSizeExhausted() {
        return _executor.getPoolSize() >= _executor.getCorePoolSize();
    }

    private boolean isPeriodAlreadySetToMinimum(final long period) {
        return Long.valueOf(period).intValue() <= 2000;
    }

    private void handlePoolExhaustionWithMinimumPeriodCornerCase() {
        // FIXME (bknerr) : handle pool and thread frequency exhaustion
        // notify staff, rescue data to disc with dedicated worker
    }

    private void lowerPeriodAndRemoveOldestWorker(@Nonnull final Iterator<PersistDataWorker> it,
                                                  @Nonnull final PersistDataWorker oldestWorker) {
        _prefPeriodInMS = Math.max(_prefPeriodInMS>>1, 2000);
        LOG.info("Remove Worker: " + oldestWorker.getName());
        _executor.remove(oldestWorker);
        it.remove();
    }

    public void rescueDataToFileSystem(@Nonnull final Iterable<String> statements) {
        rescueDataToFileSystem(_prefSmtpHost, _prefEmailAddress, _prefRescueDir, statements);
    }

    public void rescueDataToFileSystem(@Nonnull final String smtpHost,
                                       @Nonnull final String address,
                                       @Nonnull final File rescueDir,
                                       @Nonnull final Iterable<String> statements) {
        LOG.warn("Rescue statements: " + Iterables.size(statements));

        try {
            final DataRescueResult result =
                PersistDataToFileRescuer.with(statements)
                                        .at(TimeInstantBuilder.fromNow())
                                        .to(rescueDir)
                                        .rescue();
            if (!result.hasSucceeded()) {
                ArchiveNotifications.notify(smtpHost, address, NotificationType.PERSIST_DATA_FAILED, result.getFilePath());
            }
        } catch (final DataRescueException e) {
            ArchiveNotifications.notify(smtpHost, address, NotificationType.PERSIST_DATA_FAILED, e.getMessage());
        }
    }

    @Nonnull
    public ArchiveConnectionHandler getConnectionHandler() {
        return _connectionHandler;
    }

    public void shutdown() {
        _executor.shutdown();
    }

    public void registerBatchQueueHandler(@Nonnull final BatchQueueHandler<?> batchStrategy) throws ArchiveDaoException {
        final Class<?> type = batchStrategy.getType();
        if (_strategyAndBatchMap.containsKey(type)) {
            throw new ArchiveDaoException("A batch strategy for type " + type.getName() + " has already been registered.", null);
        }
        _strategyAndBatchMap.put(batchStrategy.getType(), batchStrategy);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void submitToBatch(@Nonnull final Collection<?> entries) throws ArchiveDaoException {
        if (entries.isEmpty()) {
            return;
        }
        final BlockingQueue<?> queue = getQueueForTypeOf(entries.iterator().next());
        queue.addAll((Collection) entries);

        if (isAnotherWorkerRequired()) {
            submitNewPersistDataWorker();
        }
    }

    @Nonnull
    private BlockingQueue<?> getQueueForTypeOf(@Nonnull final Object entry) throws ArchiveDaoException {
        final Class<?> type = entry.getClass();
        final BatchQueueHandler<?> strategy = _strategyAndBatchMap.get(type);
        if (strategy != null) {
            return strategy.getQueue();
        }
        throw new ArchiveDaoException("A batch strategy for type " + type.getName() + " has not been registered.", null);
    }
}
