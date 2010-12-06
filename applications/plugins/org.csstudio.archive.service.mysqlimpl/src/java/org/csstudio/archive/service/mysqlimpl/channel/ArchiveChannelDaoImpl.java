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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.channel.ArchiveChannelDTO;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.channel.IArchiveChannel;
import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

/**
 * DAO implementation with simple cache (hashmap).
 *
 * @author bknerr
 * @since 09.11.2010
 */
public class ArchiveChannelDaoImpl extends AbstractArchiveDao implements IArchiveChannelDao {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveChannelDaoImpl.class);

    /**
     * Archive channel configuration cache.
     */
    private final Map<String, IArchiveChannel> _channelCache = Maps.newHashMap();

    // FIXME (bknerr) : refactor into CRUD command objects with cmd factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectChannelByNameStmt =
        "SELECT channel_id, grp_id, smpl_mode_id, smpl_val, smpl_per, ltst_smpl_time FROM archive.channel WHERE name=?";
    private final String _selectChannelsByGroupId =
        "SELECT channel_id, name, smpl_mode_id, smpl_val, smpl_per, ltst_smpl_time FROM archive.channel WHERE grp_id=?";

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel retrieveChannelByName(@Nonnull final String name) throws ArchiveDaoException {

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
                // channel_id, grp_id, smpl_mode_id, smpl_val, smpl_per, ltst_smpl_time
                final long id = result.getLong(1);
                final long grpId = result.getLong(2);
                final int sampleMode = result.getInt(3);
                final double sampleVal = result.getDouble(4);
                final double samplePer = result.getDouble(5);
                final Timestamp ltstSmplTime = result.getTimestamp(6);

                channel = new ArchiveChannelDTO(new ArchiveChannelId(id),
                                                name,
                                                new ArchiveChannelGroupId(grpId),
                                                new ArchiveSampleModeId(sampleMode),
                                                sampleVal,
                                                samplePer,
                                                TimeInstantBuilder.buildFromMillis(ltstSmplTime.getTime()));

                _channelCache.put(name, channel);
            }

        } catch (final SQLException e) {
            throw new ArchiveDaoException("Channel configuration retrieval from archive failed.", e);
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException("Channel configuration retrieval from archive failed.", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    LOG.warn("Closing of statement " + _selectChannelByNameStmt + " failed.");
                }
            }
        }
        return channel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannel> retrieveChannelsByGroupId(final ArchiveChannelGroupId groupId) throws ArchiveDaoException {

        final Collection<IArchiveChannel> filteredList =
            Collections2.filter(_channelCache.values(), new Predicate<IArchiveChannel>() {
                @Override
                public boolean apply(@Nonnull final IArchiveChannel input) {
                    return input.getGroupId().equals(groupId);
                }
            });

        if (!filteredList.isEmpty()) {
            return new ArrayList<IArchiveChannel>(filteredList);
        }

        final Map<String, IArchiveChannel> tempCache = Maps.newHashMap();

        // Nothing yet in the cache? Ask the database:
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectChannelsByGroupId);
            stmt.setInt(1, groupId.intValue());

            final ResultSet result = stmt.executeQuery();
            while (result.next()) {
                // channel_id, name, smpl_mode_id, smpl_val, smpl_per, ltst_smpl_time
                final long id = result.getInt(1);
                final String name = result.getString(2);
                final int sampleModeId = result.getInt(3);
                final double sampleVal = result.getDouble(4);
                final double samplePer = result.getDouble(5);
                final Timestamp ltstSmplTime = result.getTimestamp(6);
                final TimeInstant instant = ltstSmplTime == null ? null :
                                                                   TimeInstantBuilder.buildFromMillis(ltstSmplTime.getTime());

                final IArchiveChannel channel =
                    new ArchiveChannelDTO(new ArchiveChannelId(id),
                                          name,
                                          groupId,
                                          new ArchiveSampleModeId(sampleModeId),
                                          sampleVal,
                                          samplePer,
                                          instant);

                tempCache.put(name, channel);
            }
            _channelCache.putAll(tempCache); // cache the overall result

            // TODO (bknerr) : order by sql statement or this way ?
            final Ordering<IArchiveChannel> o = new Ordering<IArchiveChannel>() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public int compare(@Nonnull final IArchiveChannel o1, @Nonnull final IArchiveChannel o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            return o.sortedCopy(tempCache.values());

        } catch (final SQLException e) {
            throw new ArchiveDaoException("Channels retrieval for group " + groupId.intValue() + " from archive failed.", e);
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException("Channels retrieval for group " + groupId.intValue() + " from archive failed.", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    LOG.warn("Closing of statement " + _selectChannelsByGroupId + " failed.");
                }
            }
        }
    }


}
