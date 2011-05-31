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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Deque;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.domain.desy.Strings;
import org.csstudio.domain.desy.task.AbstractTimeMeasuredRunnable;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final List<String> _batchedStatements;
    private final SqlStatementBatch _queuedStatements;

    private int _numOfStmtsInBatch;
    private long _totalSizeOfBatchInBytes;
    private final long _maxBatchSizeInBytes;



    /**
     * Constructor.
     * @param sqlStatements
     */
    public PersistDataWorker(@Nonnull final PersistEngineDataManager mgr,
                             @Nonnull final String name,
                             @Nonnull final SqlStatementBatch sqlStatements,
                             @Nonnull final long period,
                             @Nonnull final long maxBatchSizeInBytes) {
        _mgr = mgr;
        _name = name;
        _period = period;
        _numOfStmtsInBatch = 0;
        _totalSizeOfBatchInBytes = 0;
        _maxBatchSizeInBytes = maxBatchSizeInBytes;

        _queuedStatements = sqlStatements;
        _batchedStatements = Lists.newLinkedList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void measuredRun() {
        LOG.info("RUN: " + _name + " at " + TimeInstantBuilder.fromNow().formatted());

        Statement sqlStmt = null;
        Connection connection = null;
        try {

            final Deque<String> stmtStrings = Lists.newLinkedList();
            _queuedStatements.drainTo(stmtStrings);

            connection = _mgr.getConnectionHandler().getConnection();
            sqlStmt = connection.createStatement();
            while (stmtStrings.peek() != null) {
                sqlStmt = executeBatchOnCondition(connection, sqlStmt, stmtStrings.pop());
            }
            executeBatchAndResetSqlStatement(connection, sqlStmt);

        } catch (final Throwable t) {
            handleThrowable(t);
        } finally {
            _batchedStatements.clear();
            closeStatement(sqlStmt);
            try {
                finalizeWorker();
            } catch (final ArchiveConnectionException e) {
                LOG.warn("Connection retrieval for close failed on termination of worker");
            } catch (final SQLException e) {
                LOG.warn("Closing of connection failed on termination of worker");
            }
        }
    }

    /**
     * Collects the queued statements in a batch for the sql statement. If the number of statements
     * exceeds 500, the statement is executed and closed, the batched statements field is cleared,
     * and a newly created statement is returned. Otherwise the very same statement is returned, ready
     * for more queued statements to be batched.
     *
     * @param connection
     * @param PStmt
     * @param stmtStr
     * @return
     */
    @Nonnull
    private Statement executeBatchOnCondition(@Nonnull final Connection connection,
                                              @Nonnull final Statement pSqlStmt,
                                              @Nonnull final String stmtStr) {
        final Statement sqlStmt = pSqlStmt;

        if(stmtSizeConditionNotMet(stmtStr, _numOfStmtsInBatch)) {
            addStmtStrToBatchedStmt(stmtStr, sqlStmt);
            return sqlStmt;
        }

        return executeBatchAndResetSqlStatement(connection, sqlStmt);
    }

    private boolean stmtSizeConditionNotMet(@Nonnull final String stmtStr,
                                            @Nonnull final int numOfStmtsInBatch) {
        final int sizeInBytes = Strings.getSizeInBytes(stmtStr);
        _totalSizeOfBatchInBytes += sizeInBytes;
        if (numOfStmtsInBatch < 100 && _totalSizeOfBatchInBytes < _maxBatchSizeInBytes) {
            return true;
        }
        return false;
    }

    private void addStmtStrToBatchedStmt(@Nonnull final String stmtStr,
                                         @Nonnull final Statement sqlStmt) {
        try {
            _batchedStatements.add(stmtStr);
            sqlStmt.addBatch(stmtStr);
            _numOfStmtsInBatch++;
        } catch (final Throwable t) {
            handleThrowable(t);
        }
    }

    @Nonnull
    private Statement executeBatchAndResetSqlStatement(@Nonnull final Connection connection,
                                                       @Nonnull final Statement sqlStmt) {
        try {
            sqlStmt.executeBatch();
            LOG.info("Execute batched stmt - num: " + _batchedStatements.size());

            return connection.createStatement();
        }  catch (final Throwable t) {
            handleThrowable(t);
        } finally {
            _batchedStatements.clear();
            closeStatement(sqlStmt);
            _numOfStmtsInBatch = 0;
            _totalSizeOfBatchInBytes = 0;
        }
        return sqlStmt;
    }



    void handleThrowable(@Nonnull final Throwable t) {
        try {
            throw t;
        } catch (final ArchiveConnectionException se) {
            LOG.error("Archive Connection failed. No batch update. Drain unpersisted statements to file system.", se);
            _mgr.rescueDataToFileSystem(_batchedStatements);
        } catch (final BatchUpdateException be) {
            LOG.error("Batched update failed. Drain unpersisted statements to file system.", be);
            processFailedBatch(_batchedStatements, be);
        } catch (final SQLException se) {
            LOG.error("Batched update failed. Statement was already closed or driver does not support batched statements.", se);
            _mgr.rescueDataToFileSystem(_batchedStatements);
        } catch (final Throwable tt) {
            LOG.error("Unknown throwable. Thread " + _name + " is terminated", tt);
            t.printStackTrace();
            _mgr.rescueDataToFileSystem(_batchedStatements);
        }
    }

    /**
     * Called in finally block at the end of the {@link PersistDataWorker#run()} method.
     * Can be overridden for instance to close the thread's own connection.
     *
     * @throws SQLException
     * @throws ArchiveConnectionException
     */
    protected void finalizeWorker() throws SQLException, ArchiveConnectionException {
        // Empty
    }

    private void processFailedBatch(@Nonnull final List<String> batchedStatements,
                                    @Nonnull final BatchUpdateException be) {
        // NOT all statements have been successfully executed! (Depends on RDBM)
        final int[] updateCounts = be.getUpdateCounts();
        if (updateCounts.length == batchedStatements.size()) {
            // All statements have been tried executed, look for the failed ones
            final List<String> failedStmts = findFailedStatements(updateCounts, batchedStatements);
            _mgr.rescueDataToFileSystem(failedStmts);
        } else {
            // Not all statements have been tried to be executed - safe only the failed ones
            _mgr.rescueDataToFileSystem(batchedStatements.subList(updateCounts.length, batchedStatements.size()));
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
                                                     @Nonnull final List<String> allStmts) {
        final List<String> failedStmts = Lists.newLinkedList();
        for (int j = 0; j < updateCounts.length; j++) {
            if (updateCounts[j] == Statement.EXECUTE_FAILED) {
                failedStmts.add(allStmts.get(j));
            }
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
