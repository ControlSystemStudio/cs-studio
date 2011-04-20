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

import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.MAX_ALLOWED_PACKET_IN_KB;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.PERIOD_IN_MS;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference;
import org.csstudio.archive.common.service.mysqlimpl.notification.ArchiveNotifications;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Manager that handles the persistence worker thread.
 * @author Bastian Knerr
 * @since Feb 26, 2011
 */
public enum PersistEngineDataManager {
    INSTANCE;

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(PersistEngineDataManager.class);


    private static final File DATA_RESCUE_DIR = MySQLArchiveServicePreference.DATA_RESCUE_DIR.getValue();


    // TODO (bknerr) : number of threads?
    // get no of cpus and expected no of archive engines, and available archive connections
    private final int _cpus = Runtime.getRuntime().availableProcessors();
    /**
     * The thread pool executor for the periodicallz scheduled workers.
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

    /**
     * Entity managing the access to the blocking queue for consumer-producer pattern of submitted SQL
     * statements.
     */
    private final SqlStatementBatch _sqlStatementBatch;

    private Integer _prefPeriodInMS;
    private Integer _prefMaxAllowedPacketInBytes;

    /**
     * Constructor.
     */
    private PersistEngineDataManager() {
        loadAndCheckPreferences();

        _sqlStatementBatch = SqlStatementBatch.INSTANCE;

        addGracefullyShutdownHook();
    }

    private void loadAndCheckPreferences() {
        _prefMaxAllowedPacketInBytes = MAX_ALLOWED_PACKET_IN_KB.getValue() * 1024;
        _prefPeriodInMS = PERIOD_IN_MS.getValue();
    }

    private void submitNewPersistDataWorker() {
        final PersistDataWorker newWorker = new PersistDataWorker("Persist Data PERIODIC worker: " + _workerId.getAndIncrement(),
                                                                  _sqlStatementBatch,
                                                                  _prefPeriodInMS);
        _executor.scheduleAtFixedRate(newWorker,
                                      0L,
                                      newWorker.getPeriodInMS(),
                                      TimeUnit.MILLISECONDS);
        _submittedWorkers.add(newWorker);
    }

    public void submitStatementsToBatch(@Nonnull final List<String> stmts) {
        for (final String stmt : stmts) {
            submitStatementToBatch(stmt);
        }
    }

    public void submitStatementToBatch(@Nonnull final String stmt) {
        synchronized (this) {
            if (isAnotherWorkerRequired()) {
                submitNewPersistDataWorker();
            }
            _sqlStatementBatch.submitStatement(stmt);
        }
    }

    private void addGracefullyShutdownHook() {
        /**
         * Add shutdown hook.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             * {@inheritDoc}
             */
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                if (!_executor.isTerminating()) {
                    _executor.execute(new PersistDataWorker("Persist Data SHUTDOWN Worker",
                                                            _sqlStatementBatch,
                                                            Integer.valueOf(0)));
                    _executor.shutdown();
                    try {
                        if (!_executor.awaitTermination(_prefPeriodInMS + 1, TimeUnit.MILLISECONDS)) {
                            LOG.warn("Executor for PersistDataWorkers did not terminate in the specified period. Try to rescue data.");
                            final List<String> statements = Lists.newLinkedList();
                            _sqlStatementBatch.getQueue().drainTo(statements);
                            rescueDataToFileSystem(statements);
                        }
                    } catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
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

        if (doesSqlQueueLengthExceedMaxAllowedPacketSize()) {
            if (poolSizeExhausted()) {
                final Iterator<PersistDataWorker> it = _submittedWorkers.iterator();
                final PersistDataWorker oldestWorker = it.next();
                final long period = oldestWorker.getPeriodInMS();
                if (isPeriodAlreadySetToMinimum(period)) {
                    handlePoolExhaustionWithMinimumPeriodCornerCase();
                    return false;
                }
                lowerPeriodAndRemoveOldestWorker(it, oldestWorker);
            }
            return true;
        }
        return false;
    }

    private boolean noWorkerPresentYet() {
        return _executor.getPoolSize() <= 0;
    }

    private boolean doesSqlQueueLengthExceedMaxAllowedPacketSize() {
        return _sqlStatementBatch.sizeInBytes() > _prefMaxAllowedPacketInBytes;
    }

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


    public void rescueDataToFileSystem(@Nonnull final List<String> statements) {
        LOG.warn("Rescue statements: " + statements.size());

        try {
            final DataRescueResult result =
                PersistDataToFileRescuer.with(statements)
                                        .at(TimeInstantBuilder.fromNow())
                                        .to(DATA_RESCUE_DIR)
                                        .rescue();
            if (!result.hasSucceeded()) {
                ArchiveNotifications.notify(NotificationType.PERSIST_DATA_FAILED, result.getFilePath());
            }
        } catch (final DataRescueException e) {
            ArchiveNotifications.notify(NotificationType.PERSIST_DATA_FAILED, e.getMessage());
        }
    }
}
