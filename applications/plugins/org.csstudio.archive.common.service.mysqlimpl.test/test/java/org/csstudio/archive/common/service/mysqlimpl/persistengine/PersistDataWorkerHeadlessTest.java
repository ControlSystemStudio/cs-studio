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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.apache.log4j.PropertyConfigurator;
import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoTestHelper;
import org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 28.07.2011
 */
public class PersistDataWorkerHeadlessTest {

    private static ArchiveConnectionHandler HANDLER;
    private static PersistEngineDataManager PERSIST_MGR;

    private static IBatchQueueHandlerProvider NO_HANDLER_PROVIDER =
        new IBatchQueueHandlerProvider() {
            @Override
            public Collection<BatchQueueHandlerSupport<?>> getHandlers() {
                return Collections.emptyList();
            }
        };

    private static IBatchQueueHandlerProvider HANDLER_WITH_EMPTY_QUEUES_PROVIDER =
        new IBatchQueueHandlerProvider() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Collection<BatchQueueHandlerSupport<?>> getHandlers() {
                final BatchQueueHandlerSupport mock1 = Mockito.mock(BatchQueueHandlerSupport.class);
                Mockito.when(mock1.getQueue()).thenReturn(new LinkedBlockingQueue<Object>());

                final BatchQueueHandlerSupport mock2 = Mockito.mock(BatchQueueHandlerSupport.class);
                Mockito.when(mock2.getQueue()).thenReturn(new LinkedBlockingQueue<Object>());
                return (Collection) Lists.newArrayList(mock1, mock2);
            }
        };

    private static IBatchQueueHandlerProvider FALSE_STMT_PROVIDER =
        new IBatchQueueHandlerProvider() {
        @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Collection<BatchQueueHandlerSupport<?>> getHandlers() {
                @SuppressWarnings("synthetic-access")
                final BatchQueueHandlerSupport<IArchiveSample> batchHandler =
                    new BatchQueueHandlerSupport<IArchiveSample>(IArchiveSample.class,
                                                                 HANDLER.getDatabaseName(),
                                                                 new LinkedBlockingQueue<IArchiveSample>(TestSampleProvider.SAMPLES_MIN)) {
                    private int _i = 0;

                    @Override
                    protected void fillStatement(@Nonnull final PreparedStatement stmt,
                                                 @Nonnull final IArchiveSample element) throws ArchiveDaoException,
                                                                              SQLException {
                        stmt.setInt(-1, -1); // wrong statement
                    }
                    @Override
                    @Nonnull
                    protected String composeSqlString() {
                        return "INSERT INTO " + getDatabase() + "." + ArchiveSampleDaoImpl.TAB_SAMPLE +
                        " (channel_id, time, value) VALUES (" + Joiner.on(",").join(_i++,
                                                                                    (2000000000 + _i),
                                                                                    "'26.0'") +
                                                                ")";
                    }
                    @Override
                    @Nonnull
                    public Collection<String> convertToStatementString(@Nonnull final List<IArchiveSample> elements) {
                        return Collections.singleton(composeSqlString() + ";");
                    }
                    @Override
                    @Nonnull
                    public Class<IArchiveSample> getType() {
                        return IArchiveSample.class;
                    }
                };
                return (Collection) Lists.newArrayList(batchHandler);
            }
        };



    @BeforeClass
    public static void setup() throws ArchiveConnectionException {
        PropertyConfigurator.configure("../../../products/DESY/plugins/org.csstudio.archive.common.engine.product.log4j/log4j.properties");

        final MySQLArchivePreferenceService prefsMock = ArchiveDaoTestHelper.createPrefServiceMock();

        HANDLER = new ArchiveConnectionHandler(prefsMock);

        final Connection con = HANDLER.getConnection();
        Assert.assertNotNull(con);

        PERSIST_MGR = new PersistEngineDataManager(HANDLER, prefsMock);
    }

    @Test
    public void testOnProviderWithoutHandlers() {
        final PersistDataWorker worker =
            new PersistDataWorker(PERSIST_MGR, "Test Data Worker", 1000, NO_HANDLER_PROVIDER);
        worker.run();
    }
    @Test
    public void testOnProviderForHandlersWithEmptyQueues() {
        final PersistDataWorker worker =
            new PersistDataWorker(PERSIST_MGR, "Test Data Worker", 1000, HANDLER_WITH_EMPTY_QUEUES_PROVIDER);
        worker.run();
    }

    @Test
    public void testOnHandlerWithWronglyFilledStatement() {
        final PersistDataWorker worker =
            new PersistDataWorker(PERSIST_MGR, "Test Data Worker", 1000, FALSE_STMT_PROVIDER);
        worker.run();
        worker.run();

    }
}
