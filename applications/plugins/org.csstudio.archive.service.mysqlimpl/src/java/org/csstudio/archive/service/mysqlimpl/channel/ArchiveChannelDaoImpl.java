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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.rdb.ChannelConfig;
import org.csstudio.archive.rdb.SampleMode;
import org.csstudio.archive.service.businesslogic.IArchiveChannel;

import com.google.common.collect.Maps;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 09.11.2010
 */
public class ArchiveChannelDaoImpl implements IArchiveChannelDao {

    /**
     * Archive channel configuration cache.
     */
    private final Map<String, IArchiveChannel> _channelCache = Maps.newHashMap();

    // FIXME (bknerr) : refactor this shit into CRUD commands
    private final String _selectChannelByNameStmt =
        "SELECT channel_id, grp_id, smpl_mode_id, smpl_val, smpl_per FROM archive.channel WHERE name=?";

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveChannel getChannel(@CheckForNull final Connection connection, @Nonnull final String name) {

        IArchiveChannel channel = _channelCache.get(name);
        if (channel != null) {
            return channel;
        }
        // Access database
        PreparedStatement stmt;
        try {
            stmt = connection.prepareStatement(_selectChannelByNameStmt);
            stmt.setString(1, name);
            final ResultSet result = stmt.executeQuery();
            if (result.next()) {
                final SampleMode sample_mode = archive.getSampleMode(result.getInt(3));
                channel = new ChannelConfig(archive,
                                            result.getInt(1),
                                            name,
                                            result.getInt(2),
                                            sample_mode,
                                            result.getDouble(4),
                                            result.getDouble(5));
                _channelCache.put(name, channel);
            }

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            stmt.close();
        }
        return channel;
    }


}
