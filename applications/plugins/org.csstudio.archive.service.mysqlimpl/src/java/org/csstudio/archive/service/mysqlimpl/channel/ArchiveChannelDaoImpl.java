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
package org.csstudio.archive.service.mysqlimpl.channel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.service.channel.ArchiveChannelDTO;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;

/**
 * DAO implementation with simple cache (hashmap).
 *
 * @author bknerr
 * @since 09.11.2010
 */
public class ArchiveChannelDaoImpl extends AbstractArchiveDao implements IArchiveChannelDao {

    /**
     * Archive channel configuration cache.
     */
    private final Map<String, IArchiveChannel> _channelCache = Maps.newHashMap();

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectChannelByNameStmt =
        "SELECT channel_id, grp_id, smpl_mode_id, smpl_val, smpl_per, ltst_smpl_time FROM archive.channel WHERE name=?";

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel getChannel(@Nonnull final String name) throws ArchiveChannelDaoException {

        IArchiveChannel channel = _channelCache.get(name);
        if (channel != null) {
            return channel;
        }

        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectChannelByNameStmt);
            stmt.setString(1, name);

            final ResultSet result = stmt.executeQuery();
            if (result.next()) {

                final long id = result.getLong(1);
                final long grpId = result.getLong(2);
                final int sampleMode = result.getInt(3);
                final double sampleVal = result.getDouble(4);
                final double samplePer = result.getDouble(5);
                final Timestamp ltstSmplTime = result.getTimestamp(6);
                channel = new ArchiveChannelDTO(new ArchiveChannelId(id),
                                                new ArchiveChannelGroup(grpId),
                                                new ArchiveSampleModeId(sampleMode),
                                                sampleVal,
                                                samplePer,
                                                new DateTime(ltstSmplTime.getTime()));

                _channelCache.put(name, channel);
            }

        } catch (final SQLException e) {
            throw new ArchiveChannelDaoException("Channel configuration retrieval from archive failed.", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    // Ignore
                }
            }
        }
        return channel;
    }


}
