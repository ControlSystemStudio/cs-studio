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

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.testsuite.util.TestDataProvider;
import org.mockito.Mockito;

/**
 * Test setup provider for {@link ArchiveConnectionHandler} and
 * {@link org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager}
 * for the integration tests of the Daos.
 *
 * @author bknerr
 * @since 07.07.2011
 */
public final class ArchiveDaoTestHelper {

    private static TestDataProvider PROV;


    /**
     * Constructor.
     */
    private ArchiveDaoTestHelper() {
        // EMPTY
    }

    @Nonnull
    public static MySQLArchivePreferenceService createPrefServiceMock() {
        try {
            PROV = TestDataProvider.getInstance("org.csstudio.archive.common.service.mysqlimpl.test");
        } catch (final Exception e) {
            Assert.fail("Unexpected exception:\n" + e.getMessage());
        }

        final MySQLArchivePreferenceService mock = Mockito.mock(MySQLArchivePreferenceService.class);
        Mockito.when(mock.getDatabaseName()).thenReturn(String.valueOf(PROV.getHostProperty("mysqlArchiveDatabase")));
        //Mockito.when(mock.getDataRescueDir()).thenReturn(new File("D:/temp/rescue"));
        //Mockito.when(mock.getEmailAddress()).thenReturn(String.valueOf(PROV.getHostProperty("mysqlArchiverEmail")));
        Mockito.when(mock.getFailOverHost()).thenReturn("");
        Mockito.when(mock.getHost()).thenReturn(String.valueOf(PROV.getHostProperty("mysqlHost")));
        Mockito.when(mock.getMaxAllowedPacketSizeInKB()).thenReturn(Integer.valueOf(1024));
        Mockito.when(mock.getPassword()).thenReturn(String.valueOf(PROV.getHostProperty("mysqlArchivePassword")));
        Mockito.when(mock.getPeriodInMS()).thenReturn(Integer.valueOf(2000));
        Mockito.when(mock.getTerminationTimeInMS()).thenReturn(Integer.valueOf(25000));
        Mockito.when(mock.getPort()).thenReturn(Integer.valueOf((String) PROV.getHostProperty("mysqlPort")));
        //Mockito.when(mock.getSmtpHost()).thenReturn("smtp.desy.de");
        Mockito.when(mock.getUser()).thenReturn(String.valueOf(PROV.getHostProperty("mysqlArchiveUser")));

        return mock;
    }
}
