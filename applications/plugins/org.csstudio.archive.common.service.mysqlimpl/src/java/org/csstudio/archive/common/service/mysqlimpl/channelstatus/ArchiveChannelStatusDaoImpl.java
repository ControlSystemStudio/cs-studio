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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatus;
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatusId;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;

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
        "SELECT id, channel_id, connected, info, timestamp FROM " +
        getDatabaseName() + "." + TAB +
        " WHERE channel_id=? ORDER BY timestamp DESC LIMIT 1";

    @Inject
    public ArchiveChannelStatusDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                       @Nonnull final PersistEngineDataManager persister) throws ArchiveDaoException {
        super(handler, persister);

        getEngineMgr().registerBatchQueueHandler(new ArchiveChannelStatusBatchQueueHandler(getDatabaseName()));
    }


    @Override
    public void createChannelStatus(@Nonnull final IArchiveChannelStatus entry) throws ArchiveDaoException {

        getEngineMgr().submitToBatch(Collections.singleton(entry));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannelStatus retrieveLatestStatusByChannelId(@Nonnull final ArchiveChannelId id)
                                                                 throws ArchiveDaoException {
        try {
            final PreparedStatement stmt = getConnection().prepareStatement(_selectLatestChannelStatusStmt);
            // channel_id=?
            stmt.setInt(1, id.intValue());
            final ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return createChannelStatusFromResult(resultSet);
            }
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        }
        return null;
    }

    @Nonnull
    private IArchiveChannelStatus createChannelStatusFromResult(@Nonnull final ResultSet resultSet)
                                                                throws SQLException {
        // id, channel_id, connected, info, timestamp
        final int id = resultSet.getInt(1);
        final int channelId = resultSet.getInt(2);
        final boolean connected = resultSet.getBoolean(3);
        final String info = resultSet.getString(4);
        final Timestamp timestamp = resultSet.getTimestamp(5);

        return new ArchiveChannelStatus(new ArchiveChannelStatusId(id),
                                        new ArchiveChannelId(channelId),
                                        connected,
                                        info,
                                        TimeInstantBuilder.fromMillis(timestamp.getTime()));
    }

}
