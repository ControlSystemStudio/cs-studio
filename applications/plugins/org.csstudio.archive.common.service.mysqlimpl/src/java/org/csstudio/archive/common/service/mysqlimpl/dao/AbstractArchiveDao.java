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
package org.csstudio.archive.common.service.mysqlimpl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.platform.logging.CentralLogger;


/**
 * Abstract implementation of an archive DAO.
 *
 * @author bknerr
 * @since 10.11.2010
 */
public abstract class AbstractArchiveDao {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(AbstractArchiveDao.class);

    private final ArchiveDaoManager _mgr;


    /**
     * Constructor.
     */
    public AbstractArchiveDao(@Nonnull final ArchiveDaoManager mgr) {
        _mgr = mgr;
    }

    /**
     * Returns the current connection for the dao implementation and its subclasses.
     * @return the connection
     * @throws ArchiveConnectionException
     */
    @Nonnull
    protected Connection getConnection() throws ArchiveConnectionException {
        return _mgr.getConnection();
    }

    @Nonnull
    protected IArchiveDaoManager getDaoMgr() {
        return _mgr;
    }

    /**
     * Tries to close the passed statement and logs the given message on closing error.
     * @param stmt
     * @param logMsg
     */
    protected void closeStatement(@CheckForNull final Statement stmt, @Nonnull final String logMsg) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (final SQLException e) {
                LOG.warn(logMsg);
            }
        }
    }
}
