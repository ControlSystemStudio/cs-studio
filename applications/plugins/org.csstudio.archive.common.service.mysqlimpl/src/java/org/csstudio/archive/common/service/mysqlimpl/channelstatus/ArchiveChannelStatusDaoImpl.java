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
package org.csstudio.archive.common.service.mysqlimpl.channelstatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatus;
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatusId;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.domain.common.service.DeleteResult;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 *
 * @author bknerr
 * @since Feb 26, 2011
 */
public class ArchiveChannelStatusDaoImpl extends AbstractArchiveDao implements IArchiveChannelStatusDao {

    public static final String TAB = "channel_status";

    private static final String EXC_MSG = "Retrieval of channel status from archive failed.";

    private final String _selectLatestChannelStatusStmt =
        "SELECT id, channel_id, connected, info, time FROM " +
        getDatabaseName() + "." + TAB +
        " WHERE channel_id=? AND time BETWEEN ? AND ? ORDER BY time DESC LIMIT 1";
    private final String _deleteFromChannelStatusStmt =
        "DELETE FROM " + getDatabaseName() + "." + TAB + " WHERE channel_id=?";

    @Inject
    public ArchiveChannelStatusDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                       @Nonnull final PersistEngineDataManager persister) {
        super(handler, persister);

        BatchQueueHandlerSupport.installHandlerIfNotExists(new ArchiveChannelStatusBatchQueueHandler(getDatabaseName()));
    }


    @Override
    public void createChannelStatus(@Nonnull final IArchiveChannelStatus entry) throws ArchiveDaoException {
        try {
            getEngineMgr().submitToBatch(Collections.singleton(entry));
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException("Batch type support missing for " + entry.getClass().getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelStatus>
    retrieveLatestStatusByChannelIds(@Nonnull final Collection<ArchiveChannelId> ids,
                                     @Nonnull final TimeInstant start,
                                     @Nonnull final TimeInstant end)
    throws ArchiveDaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(_selectLatestChannelStatusStmt);

            final List<IArchiveChannelStatus> resultList = Lists.newArrayListWithExpectedSize(ids.size());
            for (final ArchiveChannelId id : ids) {
                stmt.setInt(1, id.intValue()); // channel_id=?
                stmt.setLong(2, start.getNanos()); // time between ?
                stmt.setLong(3, end.getNanos()); // and ?

                rs = stmt.executeQuery();
                if (rs.next()) {
                    resultList.add(createChannelStatusFromResult(rs));
                }
            }
            return resultList;
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        } finally {
            closeSqlResources(rs, stmt, conn, _selectLatestChannelStatusStmt);
        }
        return Collections.emptyList();
    }


    @Nonnull
    private IArchiveChannelStatus createChannelStatusFromResult(@Nonnull final ResultSet resultSet)
                                                                throws SQLException {
        // id, channel_id, connected, info, time
        final int id = resultSet.getInt("id");
        final int channelId = resultSet.getInt("channel_id");
        final boolean connected = resultSet.getBoolean("connected");
        final String info = resultSet.getString("info");
        final long time = resultSet.getLong("time");

        return new ArchiveChannelStatus(new ArchiveChannelStatusId(id),
                                        new ArchiveChannelId(channelId),
                                        connected,
                                        info,
                                        TimeInstantBuilder.fromNanos(time));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public DeleteResult deleteStatusForChannelId(@Nonnull final ArchiveChannelId id) throws ArchiveDaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(_deleteFromChannelStatusStmt);
            stmt.setInt(1, id.intValue());
            final int updated = stmt.executeUpdate();
            if (updated >= 0) {
                return DeleteResult.succeeded("Channel status removal for id '" + id.intValue() + "' succeeded: " + updated);
            }
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        } finally {
            closeSqlResources(null, stmt, conn, _selectLatestChannelStatusStmt);
        }
        return DeleteResult.failed("Channel status removal failed for id '" + id.intValue() + "'");
    }
}
