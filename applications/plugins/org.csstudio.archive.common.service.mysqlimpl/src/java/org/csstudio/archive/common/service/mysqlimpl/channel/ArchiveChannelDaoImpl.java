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
package org.csstudio.archive.common.service.mysqlimpl.channel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystem;
import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystemId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.mysqlimpl.controlsystem.ArchiveControlSystemDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.archive.common.service.mysqlimpl.types.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.system.ControlSystemType;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.csstudio.platform.util.StringUtil;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * DAO implementation with simple cache (hashmap).
 *
 * @author bknerr
 * @since 09.11.2010
 */
public class ArchiveChannelDaoImpl extends AbstractArchiveDao implements IArchiveChannelDao {

    private static final String EXC_MSG = "Channel table access failed.";

    private static final Timestamp DEFAULT_ZERO_TIMESTAMP = new Timestamp(0L);
    /**
     * Archive channel configuration cache.
     */
    private final Map<String, IArchiveChannel> _channelCacheByName = Maps.newHashMap();
    private final Map<ArchiveChannelId, IArchiveChannel> _channelCacheById = Maps.newHashMap();

    public static final String TAB = "channel";
    private static final String CS_TAB = ArchiveControlSystemDaoImpl.TAB;

    private final String _selectChannelPrefix =
        "SELECT " + TAB + ".id, " + TAB + ".name, " + TAB + ".datatype, " + TAB + ".group_id, " + TAB + ".last_sample_time, " +
                    TAB + ".display_high, " + TAB + ".display_low, " +
                 CS_TAB + ".id, " + CS_TAB + ".name, " + CS_TAB + ".type " +
                "FROM " + getDatabaseName() + "." + TAB + ", " + getDatabaseName() + "." + CS_TAB;
    private final String _selectChannelSuffix = "AND " + TAB + ".control_system_id=" + CS_TAB + ".id";

    // FIXME (bknerr) : refactor into CRUD command objects with cmd factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectChannelByNameStmt = _selectChannelPrefix + " WHERE " + TAB + ".name=? " + _selectChannelSuffix;
    private final String _selectChannelByIdStmt =   _selectChannelPrefix + " WHERE " + TAB + ".id=? " + _selectChannelSuffix;
    private final String _selectChannelsByGroupId = _selectChannelPrefix + " WHERE " + TAB + ".group_id=? " + _selectChannelSuffix +
                                                    " ORDER BY " + TAB + ".name";
    private final String _selectMatchingChannelsStmt = _selectChannelPrefix + " WHERE " + TAB + ".name REGEXP ? " + _selectChannelSuffix;
    private final String _selectCountAllChannelsStmt = "SELECT count(*) from " + getDatabaseName() + "." + TAB;

    /**
     * Constructor.
     */
    @Inject
    public ArchiveChannelDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                 @Nonnull final PersistEngineDataManager persister) {
        super(handler, persister);
    }

    @Nonnull
    private IArchiveChannel readChannelFromResultIntoCache(@Nonnull final ResultSet result)
                                                           throws SQLException,
                                                                  ClassNotFoundException,
                                                                  ArchiveDaoException,
                                                                  TypeSupportException {
        // id, name, datatype, group_id, last_sample_time
        final ArchiveChannelId id = new ArchiveChannelId(result.getLong(TAB + ".id"));
        final String name = result.getString(TAB + ".name");
        final String datatype = result.getString(TAB + ".datatype");
        final long groupId = result.getLong(TAB + ".group_id");
        final Timestamp lastSampleTime = result.getTimestamp(TAB + ".last_sample_time");

        final TimeInstant time = lastSampleTime.after(DEFAULT_ZERO_TIMESTAMP) ?
                                 TimeInstantBuilder.fromMillis(lastSampleTime.getTime()) :
                                 null;

        String dispHi = result.getString(TAB + ".display_high");
        dispHi = StringUtil.isBlank(dispHi) ? null : dispHi;
        String dispLo = result.getString(TAB + ".display_low");
        dispLo = StringUtil.isBlank(dispLo) ? null : dispLo;

        final ArchiveControlSystemId csId = new ArchiveControlSystemId(result.getLong(CS_TAB + ".id"));
        final String csName = result.getString(CS_TAB + ".name");
        final String csType = result.getString(CS_TAB + ".type");

        final IArchiveControlSystem cs = new ArchiveControlSystem(csId,
                                                                  csName,
                                                                  Enum.valueOf(ControlSystemType.class, csType));
        final IArchiveChannel channel =
                ArchiveTypeConversionSupport.createArchiveChannel(id,
                                                                  name,
                                                                  datatype,
                                                                  new ArchiveChannelGroupId(groupId),
                                                                  time,
                                                                  cs,
                                                                  dispLo,
                                                                  dispHi);


        _channelCacheByName.put(name, channel);
        _channelCacheById.put(id, channel);

        return channel;
    }

    @Nonnull
    private Collection<IArchiveChannel> retrieveChannelsBy(@Nonnull final Set<String> names)
                                                           throws ArchiveDaoException {

        final Set<String> foundChannelNames = Sets.newHashSet();
        final Set<IArchiveChannel> foundChannels = Sets.newHashSetWithExpectedSize(names.size());

        for (final String name : names) {
            final IArchiveChannel channel = _channelCacheByName.get(name);
            if (channel != null) {
                foundChannels.add(channel);
                foundChannelNames.add(channel.getName());
            }
        }
        if (foundChannels.size() == names.size()) {
            return foundChannels;
        }
        names.removeAll(foundChannelNames);

        try {
            foundChannels.addAll(retrieveUncachedChannelsBy(names));
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        }
        return foundChannels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel retrieveChannelBy(@Nonnull final String name) throws ArchiveDaoException {

        final Collection<IArchiveChannel> channels = retrieveChannelsBy(Sets.newHashSet(name));
        if (!channels.isEmpty()) {
            return channels.iterator().next();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel retrieveChannelById(@Nonnull final ArchiveChannelId id) throws ArchiveDaoException {

        final IArchiveChannel channel = _channelCacheById.get(id);
        if (channel != null) {
            return channel;
        }
        try {
            final PreparedStatement stmt = getConnection().prepareStatement(_selectChannelByIdStmt);
            stmt.setInt(1, id.intValue());
            return retrieveUncachedChannelBy(stmt);
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        }
        return null;
    }

    @Nonnull
    private Set<IArchiveChannel> retrieveUncachedChannelsBy(@Nonnull final Set<String> names)
                                                          throws ArchiveConnectionException,
                                                                 SQLException,
                                                                 ClassNotFoundException,
                                                                 ArchiveDaoException,
                                                                 TypeSupportException {

        final Set<IArchiveChannel> foundChannels = Sets.newHashSetWithExpectedSize(names.size());
        for (final String name : names) {
            final PreparedStatement stmt = getConnection().prepareStatement(_selectChannelByNameStmt);
            stmt.setString(1, name);
            final IArchiveChannel channel = retrieveUncachedChannelBy(stmt);
            if (channel != null) {
                foundChannels.add(channel);
            }
        }
        return foundChannels;
    }


    @CheckForNull
    private IArchiveChannel retrieveUncachedChannelBy(@Nonnull final PreparedStatement stmt)
                                                    throws SQLException, ClassNotFoundException, ArchiveDaoException, TypeSupportException
                                                     {
        try {
            final ResultSet result = stmt.executeQuery();
            if (result != null && result.next()) {
                return readChannelFromResultIntoCache(result);
            }
        } finally {
            closeStatement(stmt, "Closing of statement: '" + stmt.toString() + "' failed.");
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannel> retrieveChannelsByGroupId(@Nonnull final ArchiveChannelGroupId groupId) throws ArchiveDaoException {

        final Collection<IArchiveChannel> filteredList = getChannelsByGroupIdFromCache(groupId);
        if (!filteredList.isEmpty()) {
            return new ArrayList<IArchiveChannel>(filteredList);
        }
        // Nothing yet in the cache? Ask the database:
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectChannelsByGroupId);
            stmt.setInt(1, groupId.intValue());

            final List<IArchiveChannel> channels = Lists.newArrayList();
            final ResultSet result = stmt.executeQuery();
            while (result.next()) {
                channels.add(readChannelFromResultIntoCache(result));
            }
            return channels;

        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        } finally {
            closeStatement(stmt, "Closing of statement " + _selectChannelsByGroupId + " failed.");
        }
        return Collections.emptyList();
    }

    @Nonnull
    private Collection<IArchiveChannel> getChannelsByGroupIdFromCache(@Nonnull final ArchiveChannelGroupId groupId) {
        final Collection<IArchiveChannel> filteredList =
            Collections2.filter(_channelCacheByName.values(), new Predicate<IArchiveChannel>() {
                @Override
                public boolean apply(@Nonnull final IArchiveChannel cacheElem) {
                    return cacheElem.getGroupId().equals(groupId);
                }
            });
        return filteredList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V extends Comparable<? super V>> void updateDisplayRanges(@Nonnull final ArchiveChannelId id,
                                                                      @Nonnull final V displayLow,
                                                                      @Nonnull final V displayHigh) throws ArchiveDaoException {
        String updateDisplayRangesStmt;
        try {
            updateDisplayRangesStmt = "UPDATE " + getDatabaseName() + "." + TAB +
            " SET display_high=" + ArchiveTypeConversionSupport.toArchiveString(displayHigh) +
            ", display_low=" + ArchiveTypeConversionSupport.toArchiveString(displayLow) +
            " WHERE " + getDatabaseName() + "." + TAB + ".id=" + id.asString();

            getEngineMgr().submitStatementToBatch(updateDisplayRangesStmt);
        } catch (final TypeSupportException e) {
            handleExceptions(EXC_MSG + " Display ranges could not be written.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @CheckForNull
    public <V extends Comparable<? super V>>
    Limits<V> retrieveDisplayRanges(@Nonnull final String channelName) throws ArchiveDaoException {

        final IArchiveChannel channel = retrieveChannelBy(channelName);
        if (channel != null) {
            return (Limits<V>) channel.getDisplayLimits();
        }
        return null;
    }


    /**
     * {@inheritDoc}
     *
     * This implementation checks first whether the number of channels in the archive already match
     * the number of channels in the channel cache. If so, the cache is utilized to extract
     * the channel names matching the pattern. If not so, all channels are requested from the archive,
     * the channel cache is updated, and then the collection is created.
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannel> retrieveChannelsByNamePattern(@Nonnull final Pattern pattern)
                                                                     throws ArchiveDaoException {
        final int numOfChannels = retrieveNumberOfChannels();
        if (_channelCacheByName.size() < numOfChannels) {

            PreparedStatement stmt = null;
            try {
                stmt = getConnection().prepareStatement(_selectMatchingChannelsStmt);
                stmt.setString(1, pattern.toString());

                final ResultSet result = stmt.executeQuery();
                while (result.next()) {
                    readChannelFromResultIntoCache(result);
                }
            } catch (final Exception e) {
                handleExceptions(EXC_MSG + ": Number of channels could not be determined.", e);
            } finally {
                closeStatement(stmt, "Closing of statement: '" + _selectMatchingChannelsStmt + "' failed.");
            }
        }
        return createMatchingChannelsCollection(_channelCacheByName, pattern);
    }

    @Nonnull
    private Collection<IArchiveChannel> createMatchingChannelsCollection(@Nonnull final Map<String, IArchiveChannel> channelCacheByName,
                                                                         @Nonnull final Pattern pattern) {
        final Collection<IArchiveChannel> allChannels = channelCacheByName.values();

        final Collection<IArchiveChannel> matchingChannels =
            Collections2.filter(allChannels, new Predicate<IArchiveChannel>() {
                @Override
                public boolean apply(@Nonnull final IArchiveChannel channel) {
                    final boolean matches = pattern.matcher(channel.getName()).matches();
                    return matches;
                }
            });
        return matchingChannels;
    }


    private int retrieveNumberOfChannels() throws ArchiveDaoException {

        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectCountAllChannelsStmt);
            final ResultSet result = stmt.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (final Exception e) {
            handleExceptions(EXC_MSG + ": Number of channels could not be determined.", e);
        } finally {
            closeStatement(stmt, "Closing of statement " + _selectCountAllChannelsStmt + " failed.");
        }
        return 0;
    }
}
