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

import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.EMAIL_ADDRESS;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.MAX_ALLOWED_PACKET;
import static org.csstudio.archive.common.service.mysqlimpl.MySQLArchiveServicePreference.SMTP_HOST;

import java.io.IOException;
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
import org.csstudio.email.EMailSender;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 * @author baschtl
 * @since Feb 26, 2011
 */
public enum PersistEngineDataManager {
    INSTANCE;

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(PersistEngineDataManager.class);

    private static final int KBYTE_SIZE = 1024;
    private static final int MIN_PERIOD_MS = 3000;
    private static final int MAX_PERIOD_MS = 60000;
    private static final int DEFAULT_PERIOD_MS = 5000;

    // TODO (bknerr) : number of threads?
    // get no of cpus and expected no of archive engines, and available archive connections
    private final int _cpus = Runtime.getRuntime().availableProcessors();
    private final ScheduledThreadPoolExecutor _executor =
        (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(Math.max(1, _cpus + 1));
//    (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
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
     * Entity managing access to blocking queue for consumer-producer pattern of submitted SQL
     * statements.
     */
    private final SqlStatementBatch _sqlStatementBatch;

    private Integer _prefPeriodInMS;
    private Integer _prefMaxAllowedPacketInBytes;

    private String _prefMailHost;
    private String _prefEmailReceiver;

    private PersistEngineDataManager() {
        loadAndCheckPreferences();

        _sqlStatementBatch = SqlStatementBatch.INSTANCE;
        addGracefullyShutdownHook();
    }

    private void loadAndCheckPreferences() {

        _prefPeriodInMS = MySQLArchiveServicePreference.PERIOD.getValue();
        if (_prefPeriodInMS < MIN_PERIOD_MS || _prefPeriodInMS > MAX_PERIOD_MS) {
            LOG.info("fuup");
//            LOG.warn("Initial interval in seconds for the PersistDataWorker thread out of recommended bounds [" +
//                     MIN_PERIOD_MS + "," + MAX_PERIOD_MS+ "]." +
//                     "Set to " + DEFAULT_PERIOD_MS + "ms.");
            _prefPeriodInMS = DEFAULT_PERIOD_MS;
        }


        final int maxAllowedPacketInKB = MAX_ALLOWED_PACKET.getValue();

        // TODO (bknerr) : test code for minimum size [1kB,64MB]
        if (maxAllowedPacketInKB < 1 || maxAllowedPacketInKB > 64 * 1024) {
            LOG.warn("MaxAllowedPacket connection parameter out of recommended range [" +
                     KBYTE_SIZE + "," + 64 * KBYTE_SIZE + "]kb. Set to " + 16 * KBYTE_SIZE + " kb.");
            _prefMaxAllowedPacketInBytes = 16 * KBYTE_SIZE * KBYTE_SIZE;
        } else {
            _prefMaxAllowedPacketInBytes = maxAllowedPacketInKB * KBYTE_SIZE;
        }

        _prefMailHost = SMTP_HOST.getValue();
        _prefEmailReceiver = EMAIL_ADDRESS.getValue();
    }

    private void submitNewPersistDataWorker() {
        final PersistDataWorker newWorker = new PersistDataWorker("Persist Data PERIODIC worker: " + _workerId.getAndIncrement(),
                                                                  _sqlStatementBatch,
                                                                  _prefPeriodInMS);
        LOG.info("What???");
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
                        if (!_executor.awaitTermination(_prefPeriodInMS + 1, TimeUnit.SECONDS)) {
                            LOG.warn("Executor for PersistDataWorkers did not terminate in the specified period. Try to rescue data.");
                            final List<String> statements = Lists.newLinkedList();
                            _sqlStatementBatch.getQueue().drainTo(statements);
                            rescueData(statements);
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
        if (_executor.getPoolSize() <= 0) {
            return true; // none yet submitted, so yes we need one
        }

        // More than one necessary? If queue gets larger than what can be committed in one batch
        if (_sqlStatementBatch.sizeInBytes() > _prefMaxAllowedPacketInBytes) {
            // Do it, but is there still space in the pool for another worker?
            if (_executor.getPoolSize() < _executor.getCorePoolSize()) {
                return true; // Yes, submit another one
            } else {
                // No, but perhaps we could increase the frequency of the scheduled tasks?
                final Iterator<PersistDataWorker> it = _submittedWorkers.iterator();
                final PersistDataWorker oldestWorker = it.next();
                final long period = oldestWorker.getPeriodInMS();
                if (Long.valueOf(period).intValue() <= MIN_PERIOD_MS) {
                    // No, already set to minimum
                    // FIXME (bknerr) : handle pool and thread frequency exhaustion
                    // notify staff, rescue data to disc with dedicated worker
                    return false;
                }
                // Yes, lower the frequency and remove the oldest periodic worker, return true
                _prefPeriodInMS = Math.max(_prefPeriodInMS>>1, MIN_PERIOD_MS);
                LOG.info("Remove Worker: " + oldestWorker.getName());
                _executor.remove(oldestWorker);
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * FIXME (bknerr) : data rescue on failover fail not yet implemented!
     * @param statements
     */
    void rescueData(@Nonnull final List<String> statements) {
        LOG.info("Rescue statements " + statements.size());

        EMailSender mailer;
        try {
            mailer = new EMailSender(_prefMailHost,
                                     "DontReply@MySQLArchiver",
                                     _prefEmailReceiver,
                                     "[MySQL archiver notification]: Failed failover");
            mailer.addText("Statements rescued:\n");
            for (final String stmt : statements) {
                mailer.addText(stmt + "\n");
            }
            mailer.close();
        } catch (final IOException e) {
            // TODO (bknerr) : handle exceptions on notifications
            e.printStackTrace();
        }

    }

}
