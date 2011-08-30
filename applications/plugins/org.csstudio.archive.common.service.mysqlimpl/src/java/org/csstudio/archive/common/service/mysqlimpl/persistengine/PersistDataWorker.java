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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.domain.desy.task.AbstractTimeMeasuredRunnable;
import org.csstudio.domain.desy.time.StopWatch;
import org.csstudio.domain.desy.time.StopWatch.RunningStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Persistence layer worker for batched statements.
 * Intended to be scheduled periodically and if necessary on demand
 * (when the queue is getting big or the contained statements reach the max allowed packet size).
 *
 * Gets a connection and does not close it! As this worker is expected to use it very very often.
 *
 * @author bknerr
 * @since 08.02.2011
 */
public class PersistDataWorker extends AbstractTimeMeasuredRunnable {

    private static final Logger LOG =
            LoggerFactory.getLogger(PersistDataWorker.class);
    /**
     * See configuration of this logger - if log4j is used - see log4j.properties
     */
    private static final Logger EMAIL_LOG =
        LoggerFactory.getLogger("ErrorPerEmailLogger");

    private final PersistEngineDataManager _mgr;


    private final String _name;
    private final long _periodInMS;

    private final IBatchQueueHandlerProvider _handlerProvider;
    private final List<Object> _rescueDataList = Lists.newLinkedList();


    /**
     * Constructor.
     */
    public PersistDataWorker(@Nonnull final PersistEngineDataManager mgr,
                             @Nonnull final String name,
                             @Nonnull final long periodInMS,
                             @Nonnull final IBatchQueueHandlerProvider provider) {
        _mgr = mgr;
        _name = name;
        _periodInMS = periodInMS;

        _handlerProvider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void measuredRun() {
        try {
            final Connection connection = _mgr.getConnectionHandler().getThreadLocalConnection();

            processBatchHandlerMap(connection, _handlerProvider, _rescueDataList);

        } catch (final ArchiveConnectionException e) {
            LOG.error("Connection to archive failed", e);
            // FIXME (bknerr) : strategy for queues getting full, when to rescue data? How to check for failover?
        } catch (final Throwable t) {
            LOG.error("Unknown throwable in thread {}.", _name);
            t.printStackTrace();
            EMAIL_LOG.info("Unknown throwable in thread {}. See event.log for more info.", _name);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void processBatchHandlerMap(@Nonnull final Connection connection,
                                            @Nonnull final IBatchQueueHandlerProvider handlerProvider,
                                            @Nonnull final List<T> rescueDataList) {
        for (final BatchQueueHandlerSupport<?> handler : handlerProvider.getHandlers()) {
            PreparedStatement stmt = null;
            try {
                if (!handler.getQueue().isEmpty()) {
                    LOG.debug("Start for {} in {}", handler.getHandlerType().getSimpleName(), _name);
                    stmt = handler.createNewStatement(connection);
                    processBatchForStatement((BatchQueueHandlerSupport<T>) handler, stmt, rescueDataList);
                    LOG.debug("End for {}", handler.getHandlerType().getSimpleName());
                }
            } catch (final SQLException e) {
                LOG.error("Creation of batch statement failed for strategy " + handler.getClass().getSimpleName(), e);
                // FIXME (bknerr) : strategy for queues getting full, when to rescue data?
            } finally {
                closeStatement(stmt);
            }
        }
    }

    private <T> void processBatchForStatement(@Nonnull final BatchQueueHandlerSupport<T> handler,
                                              @Nonnull final PreparedStatement stmt,
                                              @Nonnull final List<T> rescueDataList) {
        final Queue<T> queue = handler.getQueue();
        T element;
        while (true) {
            element = queue.poll();
            if (element != null) {
                addElementToBatchAndRescueList(handler, stmt, element, rescueDataList);
                executeBatchAndClearListOnCondition(handler, stmt, rescueDataList, 1000);
            } else {
                executeBatchAndClearListOnCondition(handler, stmt, rescueDataList, 1);
                break;
            }
        }
    }

    private <T> void addElementToBatchAndRescueList(@Nonnull final BatchQueueHandlerSupport<T> handler,
                                                    @Nonnull final PreparedStatement stmt,
                                                    @Nonnull final T element,
                                                    @Nonnull final List<T> rescueDataList) {
        try {
            rescueDataList.add(element);
            handler.applyBatch(stmt, element);
        } catch (final ArchiveDaoException t) {
            handleThrowable(t, handler, rescueDataList);
        }
    }

    @Nonnull
    private <T> boolean executeBatchAndClearListOnCondition(@Nonnull final BatchQueueHandlerSupport<T> handler,
                                                            @Nonnull final PreparedStatement stmt,
                                                            @Nonnull final List<T> rescueDataList,
                                                            final int minBatchSize) {
        final int size = rescueDataList.size();
        if (size >= minBatchSize) {
            LOG.info("{} for {}", new Object[]{size, handler.getHandlerType().getSimpleName()});
            try {
                final RunningStopWatch start = StopWatch.start();
                stmt.executeBatch();
                LOG.info("took for {}: {}ms", size, start.getElapsedTimeInMillis());

            } catch (final Throwable t) {
                handleThrowable(t, handler, rescueDataList);
            } finally {
                rescueDataList.clear();
            }
            return true;
        }
        return false;
    }


    private <T> void handleThrowable(@Nonnull final Throwable t,
                                     @Nonnull final BatchQueueHandlerSupport<T> handler,
                                     @Nonnull final List<T> rescueDataList) {
        final Collection<String> statements = handler.convertToStatementString(rescueDataList);
        try {
            throw t;
        } catch (final ArchiveConnectionException se) {
            LOG.error("Archive Connection failed. No batch update. Drain unpersisted statements to file system.", se);
            _mgr.rescueDataToFileSystem(statements);
        } catch (final BatchUpdateException be) {
            LOG.error("Batched update failed. Drain unpersisted statements to file system.", be);
            processFailedBatch(statements, be);
        } catch (final SQLException se) {
            LOG.error("Batched update failed. Batched statement could not be composed.", se);
            _mgr.rescueDataToFileSystem(statements);
        } catch (final Throwable tt) {
            LOG.error("Unknown throwable. Thread " + _name + " is terminated", tt);
            _mgr.rescueDataToFileSystem(statements);
        } finally {
            rescueDataList.clear();
        }
    }

    private <T> void processFailedBatch(@Nonnull final Collection<String> batchedStatements,
                                        @Nonnull final BatchUpdateException be) {
        // NOT all statements have been successfully executed! (Depends on RDBM)
        final int[] updateCounts = be.getUpdateCounts();
        if (updateCounts.length == batchedStatements.size()) {
            // All statements have been tried to be executed, look for the failed ones
            final List<String> failedStmts = findFailedStatements(updateCounts, batchedStatements);
            _mgr.rescueDataToFileSystem(failedStmts);
        } else {
            // Not all statements have been tried to be executed - safe only the failed ones
            _mgr.rescueDataToFileSystem(Iterables.skip(batchedStatements, updateCounts.length));
        }
    }

    private static void closeStatement(@CheckForNull final Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (final SQLException e) {
                LOG.warn("Closing of statement failed: " + stmt);
            }
        }
    }

    @Nonnull
    private static List<String> findFailedStatements(@Nonnull final int[] updateCounts,
                                                     @Nonnull final Collection<String> allStmts) {
        final List<String> failedStmts = Lists.newLinkedList();
        int i = 0;
        for (final String stmt : allStmts) {
            if (i < updateCounts.length && updateCounts[i] == Statement.EXECUTE_FAILED) {
                failedStmts.add(stmt);
            }
            i++;
        }
        return failedStmts;
    }

    @Nonnull
    public String getName() {
        return _name;
    }

    public long getPeriodInMS() {
        return _periodInMS;
    }
}
