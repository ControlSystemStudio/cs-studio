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
public class AbstractDaoTestSetup {

    protected static ArchiveConnectionHandler HANDLER;
    protected static PersistEngineDataManager PERSIST_MGR;

    private Savepoint _savepoint;

    @BeforeClass
    public static void setup() throws ArchiveConnectionException, SQLException {

        HANDLER = ArchiveDaoTestHelper.getTestHandler();

        final Connection con = HANDLER.getConnection();
        Assert.assertNotNull(con);

        PERSIST_MGR = new PersistEngineDataManager(HANDLER);
    }

    @Before
    public void setSavePoint() throws ArchiveConnectionException, SQLException {
        final Connection con = HANDLER.getConnection();
        con.setAutoCommit(false);
        _savepoint = con.setSavepoint();
    }
    @After
    public void rollBack() throws ArchiveConnectionException, SQLException {
        final Connection con = HANDLER.getConnection();
        con.rollback(_savepoint);
        con.setAutoCommit(true);
    }
}
