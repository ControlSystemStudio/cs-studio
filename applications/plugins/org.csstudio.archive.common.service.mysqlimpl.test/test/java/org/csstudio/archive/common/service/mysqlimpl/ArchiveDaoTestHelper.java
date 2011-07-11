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

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.testsuite.util.TestDataProvider;

import com.google.common.base.Strings;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * Test setup provider for {@link ArchiveConnectionHandler} and {@link PersistEngineDataManager}
 * for the integration tests of the Daos.
 *
 * @author bknerr
 * @since 07.07.2011
 */
public class ArchiveDaoTestHelper {

    private static TestDataProvider PROV;

    private static String _prefHost;
    private static String _prefFailoverHost;
    private static String _prefUser;
    private static String _prefPassword;
    private static Integer _prefPort;
    private static String _prefDatabaseName;
    private static Integer _prefMaxAllowedPacketSizeInKB;

    public static final ArchiveConnectionHandler getTestHandler() {
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

        return new ArchiveConnectionHandler(dataSource);
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
}
