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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.google.common.base.Joiner;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;

/**
 * Dao implementation for archive control system.
 *
 * @author bknerr
 * @since 18.02.2011
 */
public class ArchiveControlSystemDaoImpl extends AbstractArchiveDao implements IArchiveControlSystemDao {

    public static final String TAB = "control_system";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_TYPE = "type";

    private static final String RETRIEVAL_FAILED = "Control system retrieval from archive failed.";

    private final String _selectCSByIdStmt =
        "SELECT " + Joiner.on(",").join(COL_ID, COL_NAME, COL_TYPE) + " FROM " + getDatabaseName() +
        "." + TAB + " WHERE " + COL_ID + "=?";
    private final String _selectCSByNameStmt =
        "SELECT " + Joiner.on(",").join(COL_ID, COL_NAME, COL_TYPE) + " FROM " + getDatabaseName() +
        "." + TAB + " WHERE " + COL_NAME + "=?";


    private final Map<ArchiveControlSystemId, IArchiveControlSystem> _cacheById =
        new MapMaker().concurrencyLevel(2).weakKeys().makeMap();

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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(_selectCSByIdStmt);
            stmt.setLong(1, id.longValue());
            result = stmt.executeQuery();
            if (result.next()) {
                cs = createControlSystemFromQueryResult(result);
                _cacheById.put(id, cs);
                return cs;
            }
        } catch (final Exception e) {
            handleExceptions(RETRIEVAL_FAILED, e);
        } finally {
            closeSqlResources(result, stmt, conn, _selectCSByIdStmt);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveControlSystem retrieveControlSystemByName(@Nonnull final String name) throws ArchiveDaoException {
        IArchiveControlSystem cs = getFromCache(name);
        if (cs != null) {
            return cs;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(_selectCSByNameStmt);
            stmt.setString(1, name);
            result = stmt.executeQuery();
            if (result.next()) {
                cs = createControlSystemFromQueryResult(result);
                _cacheById.put(cs.getId(), cs);
                return cs;
            }
        } catch (final Exception e) {
            handleExceptions(RETRIEVAL_FAILED, e);
        } finally {
            closeSqlResources(result, stmt, conn, _selectCSByNameStmt);
        }
        return null;
    }

    @CheckForNull
    private IArchiveControlSystem getFromCache(@Nonnull final String name) {
        for (final IArchiveControlSystem cs : _cacheById.values()) {
            if (cs.getName().equals(name)) {
                return cs;
            }
        }
        return null;
    }

    @Nonnull
    private IArchiveControlSystem createControlSystemFromQueryResult(@Nonnull final ResultSet result) throws SQLException {

        final ArchiveControlSystemId resultId = new ArchiveControlSystemId(result.getInt(COL_ID));
        final String name = result.getString(COL_NAME);
        final String type = result.getString(COL_TYPE);

        final IArchiveControlSystem cs =
            new ArchiveControlSystem(resultId,
                                     name,
                                     Enum.valueOf(ControlSystemType.class, type));
        return cs;
    }
}
