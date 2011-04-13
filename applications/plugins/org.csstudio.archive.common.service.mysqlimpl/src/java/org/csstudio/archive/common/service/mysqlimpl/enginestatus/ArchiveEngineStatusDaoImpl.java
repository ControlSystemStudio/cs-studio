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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatusId;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 02.02.2011
 */
public class ArchiveEngineStatusDaoImpl extends AbstractArchiveDao implements IArchiveEngineStatusDao {

    /**
     * Converter function to single sql VALUE, i.e. comma separated strings embraced by parentheses.
     * TODO (bknerr) : extract to be used by all VALUE assemblers
     *
     * @author bknerr
     * @since 03.02.2011
     */
    private static final class MonitorStates2SqlValue implements Function<IArchiveEngineStatus, String> {
        /**
         * Constructor.
         */
        public MonitorStates2SqlValue() {
            // Empty
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String apply(@Nonnull final IArchiveEngineStatus from) {

            return "(" +
                   Joiner.on(",").join(from.getEngineId().longValue(),
                                       "'" + from.getStatus().name() + "'", // TODO (bknerr) : once we use hibernate...
                                       "'" + from.getTimestamp().formatted() + "'",
                                       "'" + from.getInfo() + "'") +
                    ")";
        }
    }
    private static final MonitorStates2SqlValue M2S_FUNC = new MonitorStates2SqlValue();

    private static final String EXC_MSG = "Retrieval of engine status from archive failed.";

    private static final String TAB = "engine_status";

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
    }

    @Nonnull
    private String createMgmtEntryUpdateStmtPrefix(@Nonnull final String database) {
        return "INSERT INTO " + database + "." + TAB + " (engine_id, status, time, info) VALUES ";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngineStatus createMgmtEntry(@Nonnull final IArchiveEngineStatus entry) throws ArchiveDaoException {
        final String sqlValue = M2S_FUNC.apply(entry);
        final String stmtStr = createMgmtEntryUpdateStmtPrefix(getDatabaseName()) + sqlValue;

        getEngineMgr().submitStatementToBatch(stmtStr);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createMgmtEntries(@Nonnull final Collection<IArchiveEngineStatus> monitorStates) throws ArchiveDaoException {

        final String values = Joiner.on(",").join(Iterables.transform(monitorStates, M2S_FUNC));
        final String stmtStr = createMgmtEntryUpdateStmtPrefix(getDatabaseName()) + values;

        getEngineMgr().submitStatementToBatch(stmtStr);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveEngineStatus retrieveLastEngineStatus(@Nonnull final ArchiveEngineId id,
                                                         @Nonnull final TimeInstant latestAliveTime) throws ArchiveDaoException {
        try {
            final PreparedStatement stmt = getConnection().prepareStatement(_selectLatestEngineStatusInfoStmt);
            // time < now
            stmt.setTimestamp(1, new Timestamp(TimeInstantBuilder.fromNow().getMillis()));
            stmt.setInt(2, id.intValue());

            final ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return createIArchiverMgmtEntryFromResult(resultSet);
            }
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
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
        final Timestamp time = resultSet.getTimestamp("time");
        final String info = resultSet.getString("info");

        return new ArchiveEngineStatus(new ArchiveEngineStatusId(idVal),
                                new ArchiveEngineId(engineId),
                                EngineMonitorStatus.valueOf(status),
                                TimeInstantBuilder.fromMillis(time.getTime()),
                                info);
    }

}
