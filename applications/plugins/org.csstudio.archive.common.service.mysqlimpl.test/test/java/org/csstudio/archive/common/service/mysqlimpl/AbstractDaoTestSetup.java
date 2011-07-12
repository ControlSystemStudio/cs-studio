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
package org.csstudio.archive.common.service.mysqlimpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * JUnit test setup for DAO integration tests.
 *
 * @author bknerr
 * @since 07.07.2011
 */
public abstract class AbstractDaoTestSetup {

    protected static ArchiveConnectionHandler HANDLER;
    protected static PersistEngineDataManager PERSIST_MGR;

    private Savepoint _savepoint;
    private boolean _autoCommit;

    @BeforeClass
    public static void beforeClass() throws ArchiveConnectionException {

        HANDLER = new ArchiveConnectionHandler(ArchiveDaoTestHelper.createPrefServiceMock());

        final Connection con = HANDLER.getConnection();
        Assert.assertNotNull(con);

        PERSIST_MGR = new PersistEngineDataManager(HANDLER, ArchiveDaoTestHelper.createPrefServiceMock());
    }


    @Before
    public void before() throws ArchiveConnectionException, SQLException {
        setSavePoint();

        beforeHook();
    }

    private void setSavePoint() throws ArchiveConnectionException, SQLException {
        final Connection con = HANDLER.getConnection();
        _autoCommit = con.getAutoCommit();

        con.setAutoCommit(false);
        _savepoint = con.setSavepoint();
    }

    /**
     * Hook method called before any method annotated with {@link Before}.
     *
     * To be overridden by inheritors.
     */
    protected void beforeHook() {
        // Empty on purpose
    }

    /**
     * Hook method called before any method annotated with {@link After}.
     *
     * To be overridden by inheritors.
     */
    protected void afterHook() {
        // Empty on purpose
    }

    @After
    public void after() throws ArchiveConnectionException, SQLException {
        rollback();

        afterHook();
    }

    private void rollback() throws ArchiveConnectionException, SQLException {
        final Connection con = HANDLER.getConnection();
        con.rollback(_savepoint);
        con.setAutoCommit(_autoCommit);
    }



}
