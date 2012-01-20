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

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract implementation of an archive DAO.
 *
 * @author bknerr
 * @since 10.11.2010
 */
public abstract class AbstractArchiveDao {

    private static final Logger LOG =
        LoggerFactory.getLogger(AbstractArchiveDao.class);

    private final ArchiveConnectionHandler _connectionHandler;
    private final PersistEngineDataManager _engineMgr;

    /**
     * Constructor.
     */
    public AbstractArchiveDao(@Nonnull final ArchiveConnectionHandler handler,
                              @Nonnull final PersistEngineDataManager persister) {
        _connectionHandler = handler;
        _engineMgr = persister;
    }

    /**
     * Returns a new connection for the dao implementation and its subclasses.
     * @return the connection
     * @throws ArchiveConnectionException
     */
    @Nonnull
    protected Connection createConnection() throws ArchiveConnectionException {
        return _connectionHandler.createConnection();
    }

    @Nonnull
    protected String getDatabaseName() {
        return _connectionHandler.getDatabaseName();
    }


    @Nonnull
    protected PersistEngineDataManager getEngineMgr() {
        return _engineMgr;
    }

    /**
     * Tries to close the sql resources {@Statement} and implicitly {@link ResultSet}.
     * TODO (bknerr) : just found out - resultset is automatically closed when its statement is closed.
     */
    protected void closeSqlResources(@CheckForNull final ResultSet rs,
                                     @CheckForNull final Statement stmt,
                                     @Nonnull final String logMsgForCloseError) {
        closeSqlResources(rs, stmt, null, logMsgForCloseError);
    }
    /**
     * Tries to close the sql resources {@link Connection} and implicitly {@Statement} and {@link ResultSet}.
     * TODO (bknerr) : just found out - resultset is automatically closed when its statement is closed.
     */
    protected void closeSqlResources(@CheckForNull final ResultSet rs,
                                     @CheckForNull final Statement stmt,
                                     @CheckForNull final Connection conn,
                                     @Nonnull final String logMsgForCloseError) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (final SQLException e) {
            LOG.warn("Closing of SQL resources failed (ResultSet|Statement|Connection) for: {}", logMsgForCloseError);
        }
    }

    protected void handleExceptions(@Nonnull final String msg,
                                    @Nonnull final Exception inE) throws ArchiveDaoException {
        try {
            throw inE;
        } catch (final SQLException e) {
            throw new ArchiveDaoException("SQL: " + msg, e);
        } catch (final ArchiveConnectionException e) {
            throw new ArchiveDaoException("Connection: " + msg, e);
        } catch (final ClassNotFoundException e) {
            throw new ArchiveDaoException("Class not found: " + msg, e);
        } catch (final TypeSupportException e) {
            throw new ArchiveDaoException("Type support failure: " + msg, e);
        } catch (final MalformedURLException e) {
            throw new ArchiveDaoException("Malformed URL: " + msg, e);
        } catch (final Exception re) {
            throw new ArchiveDaoException("Unknown: ", re);
        }
    }
}
