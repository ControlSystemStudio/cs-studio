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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroup;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoManager;

import com.google.common.collect.Lists;

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
        "SELECT id, name, enabling_channel_id FROM " + getDaoMgr().getDatabaseName() + " .channel_group" +
                                            " WHERE engine_id=? ORDER BY name";


    /**
     * Constructor.
     * @param the dao manager
     */
    public ArchiveChannelGroupDaoImpl(@Nonnull final ArchiveDaoManager mgr) {
        super(mgr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> retrieveGroupsByEngineId(@Nonnull final ArchiveEngineId engId) throws ArchiveDaoException {

        final List<IArchiveChannelGroup> groups = Lists.newArrayList();
        PreparedStatement stmt = null;
        try {
            stmt =  getConnection().prepareStatement(_selectChannelGroupByEngineIdStmt);
            stmt.setInt(1, engId.intValue());

            final ResultSet result = stmt.executeQuery();

            while (result.next()) {
                // id, name, enabling_channel_id
                final ArchiveChannelGroupId id = new ArchiveChannelGroupId(result.getInt(1));
                final String name = result.getString(2);
                final ArchiveChannelId chanId = new ArchiveChannelId(result.getInt(3));

                groups.add(new ArchiveChannelGroup(id,
                                                      name,
                                                      chanId));
            }

        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        } finally {
            closeStatement(stmt, "Closing of statement " + _selectChannelGroupByEngineIdStmt + " failed.");
        }
        return groups;
    }


}
