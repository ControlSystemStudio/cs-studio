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
package org.csstudio.archive.common.service.mysqlimpl.engine;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.engine.ArchiveEngine;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.platform.logging.CentralLogger;

/**
 * DAO implementation for engine table.
 *
 * @author bknerr
 * @since 19.11.2010
 */
public class ArchiveEngineDaoImpl extends AbstractArchiveDao implements IArchiveEngineDao {

    private static final String EXC_MSG = "Engine retrieval from archive failed.";

    @SuppressWarnings("unused")
    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ArchiveEngineDaoImpl.class);

    private static final String TAB = "engine";

    // FIXME (bknerr) : refactor this shit into CRUD command objects with factories
    private final String _selectEngineByNameStmt =
        "SELECT id, url FROM " + getDaoMgr().getDatabaseName() + "." + TAB + " WHERE name=?";


    /**
     * Constructor.
     */
    public ArchiveEngineDaoImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public IArchiveEngine retrieveEngineByName(@Nonnull final String name) throws ArchiveDaoException {

        PreparedStatement statement = null;
        try {
            statement = getConnection().prepareStatement(_selectEngineByNameStmt);
            statement.setString(1, name);
            final ResultSet result = statement.executeQuery();
            if (result.next()) {
                // id, url
                final int id = result.getInt(1);
                final String url = result.getString(2);
                return new ArchiveEngine(new ArchiveEngineId(id),
                                            new URL(url));
            }
        } catch (final Exception e) {
            handleExceptions(EXC_MSG, e);
        } finally {
            closeStatement(statement, "Closing of statement " + _selectEngineByNameStmt + " failed.");
        }
        return null;
    }


}
