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
package org.csstudio.archive.service.mysqlimpl.samplemode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.csstudio.archive.service.mysqlimpl.AbstractArchiveDao;
import org.csstudio.archive.service.samplemode.ArchiveSampleModeId;
import org.csstudio.archive.service.samplemode.IArchiveSampleMode;

import com.google.common.collect.Maps;

/**
 * DAO implementation with simple cache (hashmap).
 *
 * @author bknerr
 * @since 10.11.2010
 */
public class ArchiveSampleModeDaoImpl extends AbstractArchiveDao implements IArchiveSampleModeDao {

    /**
     * Archive sample mode cache.
     */
    private final Map<ArchiveSampleModeId, IArchiveSampleMode> _sampleModeCache = Maps.newHashMap();

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    private final String _selectSampleModebyId = "SELECT smpl_mode_id, name, descr FROM archive.smpl_mode where smpl_mode_id=?";

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveSampleMode getSampleModeById(final ArchiveSampleModeId id) throws ArchiveSampleModeDaoException {

        IArchiveSampleMode mode = _sampleModeCache.get(id);
        if (mode != null) {
            return mode;
        }
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectSampleModebyId);
            stmt.setInt(1, id.intValue());

            final ResultSet result = stmt.executeQuery();
            if (result.next()) {

                final ArchiveSampleModeId newId = new ArchiveSampleModeId(result.getInt(1));
                final String name = result.getString(2);
                final String desc = result.getString(3);
                mode = new ArchiveSampleModeDTO(newId, name, desc);

                _sampleModeCache.put(mode.getId(), mode);
            }
        } catch (final SQLException e) {
            throw new ArchiveSampleModeDaoException("Sample mode retrieval from archive failed.", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    // Ignore
                }
            }
        }

        return mode;
    }

}
