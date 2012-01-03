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
package org.csstudio.archive.common.service.mysqlimpl.channelgroup;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroup;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * DAO implementation with simple cache (hashmap).
 *
 * @author bknerr
 * @since 09.11.2010
 */
public class ArchiveChannelGroupDaoImpl extends AbstractArchiveDao implements IArchiveChannelGroupDao {

    private static final String EXC_MSG = "Channel group retrieval from archive failed.";
    private static final String TAB = "channel_group";

    private static final String NAME_SET = "<SET_CLAUSE>";

    // FIXME (bknerr) : refactor into CRUD command objects with cmd factories
    private final String _selectChannelGroupByEngineIdStmt =
        "SELECT id, name, engine_id, description FROM " +
        getDatabaseName() + "." + TAB + " WHERE engine_id=? ORDER BY name";
    private final String _createChannelGroupStmt = "INSERT INTO " + getDatabaseName() + "." + TAB +
                                                   " (name, engine_id, description)" +
                                                   " VALUES (?, ?, ?)";
    private final String _deleteChannelGroupStmt = "DELETE FROM " + getDatabaseName() + "." + TAB +
                                                   " WHERE name in (" + NAME_SET + ")";

    /**
     * Constructor.
     */
    @Inject
    public ArchiveChannelGroupDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                      @Nonnull final PersistEngineDataManager persister) {
        super(handler, persister);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> retrieveGroupsByEngineId(@Nonnull final ArchiveEngineId engId) throws ArchiveDaoException {

        final List<IArchiveChannelGroup> groups = Lists.newArrayList();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = createConnection();
            stmt =  conn.prepareStatement(_selectChannelGroupByEngineIdStmt);
            stmt.setInt(1, engId.intValue());

            result = stmt.executeQuery();

            while (result.next()) {
                // id, name, enabling_channel_id
                final ArchiveChannelGroupId id = new ArchiveChannelGroupId(result.getInt("id"));
                final String name = result.getString("name");
                final ArchiveEngineId engineId = new ArchiveEngineId(result.getInt("engine_id"));
                final String desc = result.getString("description");
                groups.add(new ArchiveChannelGroup(id, name, engineId, desc));
            }

        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        } finally {
            closeSqlResources(result, stmt, conn, _selectChannelGroupByEngineIdStmt);
        }
        return groups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> createGroups(@Nonnull final Collection<IArchiveChannelGroup> groups) throws ArchiveDaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        Collection<IArchiveChannelGroup> notAddedGroups = Collections.emptyList();
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(_createChannelGroupStmt);
            for (final IArchiveChannelGroup group : groups) {
                stmt.setString(1, group.getName());
                stmt.setInt(2, group.getEngineId().intValue());
                stmt.setString(3, group.getDescription());

                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (final BatchUpdateException e) {
            notAddedGroups = createNotAddedGroups(groups, e);
        } catch (final Exception e) {
            handleExceptions(EXC_MSG + ": Group creation failed in DAO impl.", e);
        } finally {
            closeSqlResources(null, stmt, conn, _createChannelGroupStmt);
        }
        return notAddedGroups;
    }

    @Nonnull
    private Collection<IArchiveChannelGroup> createNotAddedGroups(@Nonnull final Collection<IArchiveChannelGroup> groups,
                                                                  @Nonnull final BatchUpdateException e) {
        final List<IArchiveChannelGroup> notAddedGroups = Lists.newArrayList();
        final int[] updateCounts = e.getUpdateCounts();
        int i = 0;
        for (final IArchiveChannelGroup group : groups) {
            if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                notAddedGroups.add(group);
            }
            i++;
        }
        return notAddedGroups;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteChannelGroups(@Nonnull final Set<String> names) throws ArchiveDaoException {
        if (names.isEmpty()) {
            return false;
        }
        final String nameClause = "'" + Joiner.on("','").join(names) + "'";
        final String stmtStr = _deleteChannelGroupStmt.replaceFirst(NAME_SET, nameClause);

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(stmtStr);
            final int update = stmt.executeUpdate();
            return update == names.size();
        } catch (final Exception e) {
            handleExceptions(EXC_MSG + ": Group deletion failed in DAO impl.", e);
        } finally {
            closeSqlResources(null, stmt, conn, stmtStr);
        }
        return false;
    }
}
