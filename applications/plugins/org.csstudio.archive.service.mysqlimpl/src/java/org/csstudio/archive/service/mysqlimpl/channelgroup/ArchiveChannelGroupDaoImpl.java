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
package org.csstudio.archive.service.mysqlimpl.channelgroup;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.service.ArchiveConnectionException;
import org.csstudio.archive.service.channel.ArchiveChannelId;
import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupDTO;
import org.csstudio.archive.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.service.engine.ArchiveEngineId;
import org.csstudio.archive.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * DAO implementation with simple cache (hashmap).
 *
 * @author bknerr
 * @since 09.11.2010
 */
public class ArchiveChannelGroupDaoImpl extends AbstractArchiveDao implements IArchiveChannelGroupDao {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveChannelGroupDaoImpl.class);

    // FIXME (bknerr) : refactor into CRUD command objects with cmd factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectChannelGroupByEngineIdStmt =
        "SELECT grp_id, name, enabling_chan_id FROM archive.chan_grp WHERE eng_id=? ORDER BY name";


    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Collection<IArchiveChannelGroup> retrieveGroupsByEngineId(final ArchiveEngineId engId) throws ArchiveChannelGroupDaoException {

        PreparedStatement stmt = null;
        try {
            stmt =  getConnection().prepareStatement(_selectChannelGroupByEngineIdStmt);
            stmt.setInt(1, engId.intValue());

            final ResultSet result = stmt.executeQuery();

            final List<IArchiveChannelGroup> groups = Lists.newArrayList();
            while (result.next()) {

                final ArchiveChannelGroupId id = new ArchiveChannelGroupId(result.getInt(1));
                final String name = result.getString(2);
                final ArchiveChannelId chanId = new ArchiveChannelId(result.getInt(3));

                groups.add(new ArchiveChannelGroupDTO(id,
                                                      name,
                                                      chanId));
            }

            // TODO (bknerr) : check whether this is still necessary, if not just return 'groups'
            // SQL should already give sorted result, but handling of upper/lowercase
            // names seems to differ between Oracle and MySQL, resulting in
            // files that were hard to compare
            final Ordering<IArchiveChannelGroup> o = new Ordering<IArchiveChannelGroup>() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public int compare(@Nonnull final IArchiveChannelGroup o1, @Nonnull final IArchiveChannelGroup o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            return o.sortedCopy(groups);

        } catch (final ArchiveConnectionException e) {
            throw new ArchiveChannelGroupDaoException("Channel group retrieval from archive failed.", e);
        } catch (final SQLException e) {
            throw new ArchiveChannelGroupDaoException("Channel group retrieval from archive failed.", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    LOG.warn("Closing of statement " + _selectChannelGroupByEngineIdStmt + " failed.");
                }
            }
        }
    }


}
