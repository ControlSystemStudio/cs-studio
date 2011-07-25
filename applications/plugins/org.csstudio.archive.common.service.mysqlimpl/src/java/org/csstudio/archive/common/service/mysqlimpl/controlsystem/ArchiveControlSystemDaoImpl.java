/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.mysqlimpl.controlsystem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystem;
import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystemId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.domain.desy.system.ControlSystemType;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * Dao implementation for archive control system.
 *
 * @author bknerr
 * @since 18.02.2011
 */
public class ArchiveControlSystemDaoImpl extends AbstractArchiveDao implements IArchiveControlSystemDao {

    public static final String TAB = "control_system";

    private static final String RETRIEVAL_FAILED = "Control system retrieval from archive failed.";

    private final String _selectCSByIdStmt = "SELECT name,type FROM " +
                                             getDatabaseName() +
                                             "." + TAB + " WHERE id=?";


    private final Map<ArchiveControlSystemId, IArchiveControlSystem> _cacheById = Maps.newHashMap();

    /**
     * Constructor.
     */
    @Inject
    public ArchiveControlSystemDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                       @Nonnull final PersistEngineDataManager persister) {
        super(handler, persister);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveControlSystem retrieveControlSystemById(@Nonnull final ArchiveControlSystemId id) throws ArchiveDaoException {
        IArchiveControlSystem cs = _cacheById.get(id);
        if (cs != null) {
            return cs;
        }
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(_selectCSByIdStmt);
            stmt.setLong(1, id.longValue());
            final ResultSet result = stmt.executeQuery();
            if (result.next()) {
                final String name = result.getString("name");
                final String type = result.getString("type");
                cs = new ArchiveControlSystem(name,
                                              Enum.valueOf(ControlSystemType.class, type));

                _cacheById.put(id, cs);
                return cs;
            }
        } catch (final Exception e) {
            handleExceptions(RETRIEVAL_FAILED, e);
        } finally {
            closeStatement(stmt, "Closing of statement " + _selectCSByIdStmt + " failed.");
        }
        return null;
    }

}
