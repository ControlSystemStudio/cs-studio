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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.ArchiveChannelGroupDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.IArchiveChannelGroupDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.engine.ArchiveEngineDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.engine.IArchiveEngineDao;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.testsuite.util.TestDataProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Strings;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Test for {@link ArchiveConnectionHandler}.
 *
 * @author bknerr
 * @since 18.05.2011
 */
public class ArchiveDaosHeadlessTest {

    private static org.csstudio.testsuite.util.TestDataProvider PROV;

    private static String _prefHost;
    private static String _prefFailoverHost;
    private static String _prefUser;
    private static String _prefPassword;
    private static Integer _prefPort;
    private static String _prefDatabaseName;
    private static Integer _prefMaxAllowedPacketSizeInKB;

    private static ArchiveConnectionHandler HANDLER;
    private static PersistEngineDataManager PERSIST_MGR;

    private Savepoint _savepoint;

    @BeforeClass
    public static void setup() throws ArchiveConnectionException, SQLException {
        try {
            PROV = TestDataProvider.getInstance("org.csstudio.archive.common.service.mysqlimpl.test");
        } catch (final Exception e) {
            Assert.fail("Unexpected exception:\n" + e.getMessage());
        }
        _prefHost = String.valueOf(PROV.getHostProperty("mysqlHost"));
        _prefPort = Integer.valueOf((String) PROV.getHostProperty("mysqlPort"));
        _prefFailoverHost = "";
        _prefUser = String.valueOf(PROV.getHostProperty("mysqlArchiveUser"));
        _prefPassword = String.valueOf(PROV.getHostProperty("mysqlArchivePassword"));
        _prefDatabaseName = String.valueOf(PROV.getHostProperty("mysqlArchiveDatabase"));

        _prefMaxAllowedPacketSizeInKB = 1024;

        final MysqlDataSource dataSource = createDataSource();

        HANDLER = new ArchiveConnectionHandler(dataSource);

        final Connection con = HANDLER.getConnection();
        Assert.assertNotNull(con);

        PERSIST_MGR = new PersistEngineDataManager(HANDLER);
    }

    @Nonnull
    private static MysqlDataSource createDataSource() {

        final MysqlDataSource ds = new MysqlDataSource();
        String hosts = _prefHost;
        if (!Strings.isNullOrEmpty(_prefFailoverHost)) {
            hosts += "," + _prefFailoverHost;
        }
        ds.setServerName(hosts);
        ds.setPort(_prefPort);
        ds.setDatabaseName(_prefDatabaseName);
        ds.setUser(_prefUser);
        ds.setPassword(_prefPassword);
        ds.setFailOverReadOnly(false);
        ds.setMaxAllowedPacket(_prefMaxAllowedPacketSizeInKB*1024);
        ds.setUseTimezone(true);

        return ds;
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
    }

    @Test
    public void testEngineDao() throws ArchiveConnectionException, ArchiveDaoException, SQLException, MalformedURLException {
        final IArchiveEngineDao dao = new ArchiveEngineDaoImpl(HANDLER, PERSIST_MGR);
        final IArchiveEngine noEngine = dao.retrieveEngineById(ArchiveEngineId.NONE);
        Assert.assertNull(noEngine);

        final ArchiveEngineId id = new ArchiveEngineId(1L);

        IArchiveEngine engine = dao.retrieveEngineById(id);
        assertEngineLookup(id, engine);

        engine = dao.retrieveEngineByName("TestEngine");
        assertEngineLookup(id, engine);

        final TimeInstant time = TimeInstantBuilder.fromNow();
        dao.updateEngineAlive(id, time);

        engine = dao.retrieveEngineById(id);
        Assert.assertTrue(time.equals(engine.getLastAliveTime()));
    }

    private void assertEngineLookup(@Nonnull final ArchiveEngineId id,
                                    @Nonnull final IArchiveEngine engine) throws MalformedURLException {
        Assert.assertEquals(id, engine.getId());
        Assert.assertNotNull(engine);
        Assert.assertEquals(new URL("http://krykpcj.desy.de:4811"), engine.getUrl());
        Assert.assertEquals(TimeInstantBuilder.fromNanos(1309478401000000000L), engine.getLastAliveTime());
    }

    @Test
    public void testChannelGroupDao() throws ArchiveDaoException {
        final IArchiveChannelGroupDao dao = new ArchiveChannelGroupDaoImpl(HANDLER, PERSIST_MGR);

        Collection<IArchiveChannelGroup> groups =
            dao.retrieveGroupsByEngineId(new ArchiveEngineId(26L));
        Assert.assertTrue(groups.isEmpty());

        final ArchiveEngineId engineId = new ArchiveEngineId(1L);
        groups = dao.retrieveGroupsByEngineId(engineId);
        Assert.assertTrue(2 == groups.size());
        final Iterator<IArchiveChannelGroup> it = groups.iterator();
        IArchiveChannelGroup group = it.next();

        dispatchGroupCheckById(group);

        group = it.next();

        dispatchGroupCheckById(group);

    }

    private void dispatchGroupCheckById(@Nonnull final IArchiveChannelGroup group) {
        if (group.getId().intValue() == 1) {
            assertGroup(group, "TestGroup1", 1, "TestGroupDescription");
        } else if (group.getId().intValue() == 2) {
            assertGroup(group, "TestGroup2", 1, null);
        } else {
            Assert.fail("Channel group id unknown.");
        }
    }

    private void assertGroup(@Nonnull final IArchiveChannelGroup group,
                             @Nonnull final String name,
                             final int engineId,
                             @Nullable final String desc) {
        Assert.assertEquals(name, group.getName());
        Assert.assertTrue(engineId == group.getEngineId().intValue());
        Assert.assertEquals(desc, group.getDescription());
    }


}
