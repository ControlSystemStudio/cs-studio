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
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.dao.BatchQueueHandler;
import org.csstudio.domain.desy.task.AbstractTimeMeasuredRunnable;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Persistence layer worker for batched statements.
 * Intended to be scheduled periodically and if necessary on demand
 * (when the queue is getting big or the contained statements reach the max allowed packet size).
 *
 * @author bknerr
 * @since 08.02.2011
 */
public class PersistDataWorker extends AbstractTimeMeasuredRunnable {

    private static final Logger LOG =
            LoggerFactory.getLogger(PersistDataWorker.class);

    private final PersistEngineDataManager _mgr;


    private final String _name;
    private final long _period;

    private final Map<Class<?>, BatchQueueHandler<?>> _batchQueueHandlerMap;
    private final List<Object> _elementsInBatch = Lists.newLinkedList();


    /**
     * Constructor.
     */
    public PersistDataWorker(@Nonnull final PersistEngineDataManager mgr,
                             @Nonnull final String name,
                             @Nonnull final long period,
                             @Nonnull final Map<Class<?>, BatchQueueHandler<?>> handlerMap) {
        _mgr = mgr;
        _name = name;
        _period = period;

        _batchQueueHandlerMap = handlerMap;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void measuredRun() {
        LOG.info("RUN: " + _name + " at " + TimeInstantBuilder.fromNow().formatted());

        Connection connection = null;
        try {
            connection = _mgr.getConnectionHandler().getConnection();
        } catch (final ArchiveConnectionException e) {
            LOG.error("Connection to archive failed", e);
            // FIXME (bknerr) : strategy for queues getting full, when to rescue data?
            return;
        }

        processBatchHandlerMap(connection, (Map) _batchQueueHandlerMap, _elementsInBatch);
    }

    private <T> void processBatchHandlerMap(@Nonnull final Connection connection,
                                            @Nonnull final Map<Class<T>, BatchQueueHandler<T>> batchQueueHandlerMap,
                                            @Nonnull final List<T> elementsInBatch) {
        for (final BatchQueueHandler<T> handler : batchQueueHandlerMap.values()) {
            PreparedStatement stmt = null;
            try {
                stmt = handler.createNewStatement(connection);
                processBatchForStatement(handler, stmt, elementsInBatch);
            } catch (final SQLException e) {
                LOG.error("Creation of batch statement failed for strategy " + handler.getType().getName(), e);
                // FIXME (bknerr) : strategy for queues getting full, when to rescue data?
            } finally {
                closeStatement(stmt);
            }
        }
    }

    private <T> void processBatchForStatement(@Nonnull final BatchQueueHandler<T> handler,
                                              @Nonnull final PreparedStatement stmt,
                                              @Nonnull final List<T> elementsInBatch) {
        final BlockingQueue<T> queue = handler.getQueue();
        T element = null;
        while ((element = queue.poll()) != null) {
            addElementToBatch(handler, stmt, element, elementsInBatch);
            executeBatchAndResetOnCondition(handler, stmt, elementsInBatch, 999);
        }
        if (!elementsInBatch.isEmpty()) {
            executeBatchAndResetOnCondition(handler, stmt, elementsInBatch, 0);
        }
    }

    private <T> void addElementToBatch(@Nonnull final BatchQueueHandler<T> handler,
                                       @Nonnull final PreparedStatement stmt,
                                       @Nonnull final T element,
                                       @Nonnull final List<T> elementsInBatch) {
        try {
            elementsInBatch.add(element);
            handler.applyBatch(stmt, element);
        } catch (final Throwable t) {
            handleThrowable(t, handler, elementsInBatch);
        }
    }

    private <T> void executeBatchAndResetOnCondition(@Nonnull final BatchQueueHandler<T> handler,
                                                     @Nonnull final PreparedStatement stmt,
                                                     @Nonnull final List<T> elementsInBatch,
                                                     final int noOfStmts) {
        if (elementsInBatch.size() > noOfStmts) {
            LOG.info("Execute batched stmt - num: " + elementsInBatch.size());
            try {
                stmt.executeBatch();
            } catch (final Throwable t) {
                handleThrowable(t, handler, elementsInBatch);
            } finally {
                elementsInBatch.clear();
            }
        }
    }


    private <T> void handleThrowable(@Nonnull final Throwable t,
                                     @Nonnull final BatchQueueHandler<T> handler,
                                     @Nonnull final List<T> elementsInBatch) {
        final Collection<String> statements = handler.convertToStatementString(elementsInBatch);
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
            t.printStackTrace();
            _mgr.rescueDataToFileSystem(statements);
        } finally {
            elementsInBatch.clear();
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
        return _period;
    }
}
