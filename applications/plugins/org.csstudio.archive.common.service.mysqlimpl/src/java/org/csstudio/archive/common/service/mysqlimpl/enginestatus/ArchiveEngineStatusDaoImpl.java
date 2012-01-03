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
package org.csstudio.archive.common.service.mysqlimpl.enginestatus;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatusId;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

import com.google.inject.Inject;

/**
 * Dao implementation using
 *
 * @author bknerr
 * @since 02.02.2011
 */
public class ArchiveEngineStatusDaoImpl extends AbstractArchiveDao implements IArchiveEngineStatusDao {

    public static final String TAB = "engine_status";

    private static final String EXC_MSG = "Retrieval of engine status from archive failed.";

    private final String _selectLatestEngineStatusInfoStmt =
        "SELECT id, engine_id, status, time, info FROM " + getDatabaseName() + "." + TAB +
        " WHERE time < ?" +
        " AND engine_id=? ORDER BY time DESC LIMIT 1";


    /**
     * Constructor.
     */
    @Inject
    public ArchiveEngineStatusDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                      @Nonnull final PersistEngineDataManager persister) {
        super(handler, persister);

        BatchQueueHandlerSupport.installHandlerIfNotExists(new ArchiveEngineStatusBatchQueueHandler(getDatabaseName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngineStatus createMgmtEntry(@Nonnull final IArchiveEngineStatus status) throws ArchiveDaoException {
        createMgmtEntries(Collections.singleton(status));
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createMgmtEntries(@Nonnull final Collection<IArchiveEngineStatus> monitorStates) throws ArchiveDaoException {
        try {
            getEngineMgr().submitToBatch(monitorStates);
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException("Batch type support missing for " + IArchiveEngineStatus.class.getName(), e);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngineStatus retrieveLastEngineStatus(@Nonnull final ArchiveEngineId id,
                                                         @Nonnull final TimeInstant latestAliveTime) throws ArchiveDaoException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(_selectLatestEngineStatusInfoStmt);

            stmt.setLong(1, latestAliveTime.getNanos());
            stmt.setInt(2, id.intValue());

            resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return createIArchiverMgmtEntryFromResult(resultSet);
            }
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        } finally {
            closeSqlResources(resultSet, stmt, conn, _selectLatestEngineStatusInfoStmt);
        }
        return null;
    }

    @Nonnull
    private IArchiveEngineStatus createIArchiverMgmtEntryFromResult(@Nonnull final ResultSet resultSet)
                                                                  throws SQLException {
        // id, monitor_mode, engine_id, time, info
        final int idVal = resultSet.getInt("id");
        final int engineId = resultSet.getInt("engine_id");
        final String status = resultSet.getString("status");
        final long time = resultSet.getLong("time");
        final String info = resultSet.getString("info");

        return new ArchiveEngineStatus(new ArchiveEngineStatusId(idVal),
                                       new ArchiveEngineId(engineId),
                                       EngineMonitorStatus.valueOf(status),
                                       TimeInstantBuilder.fromNanos(time),
                                       info);
    }

}
