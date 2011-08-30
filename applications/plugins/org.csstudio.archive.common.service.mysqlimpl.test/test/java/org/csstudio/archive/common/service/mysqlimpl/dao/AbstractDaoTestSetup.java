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
package org.csstudio.archive.common.service.mysqlimpl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * JUnit test setup for DAO integration tests.
 *
 * @author bknerr
 * @since 07.07.2011
 */
public abstract class AbstractDaoTestSetup {

    // CHECKSTYLE OFF: |
    protected static ArchiveConnectionHandler HANDLER;
    protected static PersistEngineDataManager PERSIST_MGR;
    // CHECKSTYLE ON: |

    private Connection _connection;
    private Savepoint _savepoint;
    private boolean _autoCommit;
    private final boolean _withRollback;

    /**
     * Constructor.
     */
    public AbstractDaoTestSetup(final boolean withRollback) {
        _withRollback = withRollback;
    }
    /**
     * Constructor.
     */
    public AbstractDaoTestSetup() {
        this(true);
    }

    @BeforeClass
    public static void beforeClass() {

        final MySQLArchivePreferenceService prefsMock = ArchiveDaoTestHelper.createPrefServiceMock();

        HANDLER = new ArchiveConnectionHandler(prefsMock);

        PERSIST_MGR = new PersistEngineDataManager(HANDLER, prefsMock);

        /**
         *  unfortunately, the lifecycle of the {@link org.csstudio.archive.common.service.mysqlimpl.MySqlServiceImplActivator}
         *  cannot be controlled in JUnit Plugin tests. Hence, the daos will be instantiated by the activator
         *  before the test and this method here runs. That means, the daos create their batch queue handlers
         *  ({@link BatchQueueHandlerSupport#installIfNotExists}) with the database name from the production prefs,
         *  which are statically registered as type supports that cannot be overridden in the typesupport by a recreation of
         *  the daos. We have to set the queue handlers parameters again - namely the database name.
         */
        for (final BatchQueueHandlerSupport<?> handler : BatchQueueHandlerSupport.getInstalledHandlers()) {
            handler.setDatabase(prefsMock.getDatabaseName());
        }
    }


    /**
     * Don't override. Use {@link AbstractDaoTestSetup#beforeHook()}.
     * @throws ArchiveConnectionException
     * @throws SQLException
     */
    @Before
    public void before() throws ArchiveConnectionException, SQLException {
        if (_withRollback) {
            setSavePoint();
        }

        beforeHook();
    }

    private void setSavePoint() throws ArchiveConnectionException, SQLException {
        _connection = HANDLER.getThreadLocalConnection();
        _autoCommit = _connection.getAutoCommit();

        _connection.setAutoCommit(false);
        _savepoint = _connection.setSavepoint();
    }

    /**
     * Hook method called before any method annotated with {@link Before}.
     *
     * To be overridden by inheritors.
     * @throws SQLException
     * @throws ArchiveConnectionException
     */
    @SuppressWarnings("unused")
    protected void beforeHook() throws ArchiveConnectionException, SQLException {
        // Empty on purpose
    }


    /**
     * Don't override. Use {@link AbstractDaoTestSetup#afterHook()}.
     * @throws ArchiveConnectionException
     * @throws SQLException
     */
    @After
    public void after() throws ArchiveConnectionException, SQLException {
        afterHook();

        if (_withRollback) {
            rollback();
        }
    }

    private void rollback() throws SQLException {
        if (_connection != null) {
            _connection.rollback(_savepoint);
            _connection.setAutoCommit(_autoCommit);
        }
    }

    /**
     * Hook method called before any method annotated with {@link After}.
     *
     * To be overridden by inheritors.
     * @throws SQLException
     * @throws ArchiveConnectionException
     */
    @SuppressWarnings("unused")
    protected void afterHook() throws ArchiveConnectionException, SQLException {
        // Empty on purpose
    }

    @AfterClass
    public static void afterClass() {
        if (PERSIST_MGR != null) {
            PERSIST_MGR.shutdown();
        }
    }
}
