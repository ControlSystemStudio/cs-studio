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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroup;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;

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
    // FIXME (bknerr) : refactor into CRUD command objects with cmd factories
    private final String _selectChannelGroupByEngineIdStmt =
        "SELECT id, name, engine_id, description FROM " +
        getDatabaseName() + " .channel_group" +
        " WHERE engine_id=? ORDER BY name";

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
            conn = getThreadLocalConnection();
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
            closeSqlResources(result, stmt, _selectChannelGroupByEngineIdStmt);
        }
        return groups;
    }


}
