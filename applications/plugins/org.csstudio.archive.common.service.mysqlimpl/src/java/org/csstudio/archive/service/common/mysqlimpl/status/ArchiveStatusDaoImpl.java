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
package org.csstudio.archive.service.common.mysqlimpl.status;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoManager;
import org.csstudio.archive.common.service.status.ArchiveStatusDTO;
import org.csstudio.archive.common.service.status.ArchiveStatusId;
import org.csstudio.archive.common.service.status.IArchiveStatus;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.platform.logging.CentralLogger;

import com.google.common.collect.Maps;

/**
 * Dao implementation for archive status.
 *
 * @author bknerr
 * @since 19.11.2010
 */
public class ArchiveStatusDaoImpl extends AbstractArchiveDao implements IArchiveStatusDao {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveStatusDaoImpl.class);

    private static final String RETRIEVAL_FAILED = "Status retrieval from archive failed.";

    /**
     * Archive status configuration cache.
     */
    private final Map<EpicsAlarmStatus, IArchiveStatus> _statusCache = Maps.newEnumMap(EpicsAlarmStatus.class);

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    // TODO (bknerr) : parameterize the database schema name via dao call
    private final String _selectStatusByNameStmt = "SELECT status_id FROM archive.status WHERE name=?";

    /**
     * Constructor.
     */
    public ArchiveStatusDaoImpl(@Nonnull final ArchiveDaoManager mgr) {
        super(mgr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public ArchiveStatusId retrieveStatusId(@Nonnull final EpicsAlarmStatus stts) throws ArchiveDaoException {
        final IArchiveStatus status = retrieveStatus(stts);
        if (status != null) {
            return status.getId();
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveStatus retrieveStatus(@Nonnull final EpicsAlarmStatus stts) throws ArchiveDaoException {

        final IArchiveStatus status = _statusCache.get(stts);
        if (status != null) {
            return status;
        }
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectStatusByNameStmt);

            stmt.setString(1, stts.name());

            final ResultSet result = stmt.executeQuery();
            if (result.next()) {
                final ArchiveStatusId id = new ArchiveStatusId(result.getInt(1));
                final IArchiveStatus newStts = new ArchiveStatusDTO(id, stts.name());

                _statusCache.put(stts, newStts);
                return newStts;
            }
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } catch (final SQLException e) {
            throw new ArchiveDaoException(RETRIEVAL_FAILED, e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (final SQLException e) {
                    LOG.warn("Closing of statement " + _selectStatusByNameStmt + " failed.");
                }
            }
        }
        return null;
    }

}
