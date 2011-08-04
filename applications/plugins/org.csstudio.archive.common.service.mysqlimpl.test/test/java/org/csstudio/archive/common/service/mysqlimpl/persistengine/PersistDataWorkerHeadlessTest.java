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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoTestHelper;
import org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 28.07.2011
 */
public class PersistDataWorkerHeadlessTest {

    static final String TEST_STATEMENT = "Test Statement: ";

    private static ArchiveConnectionHandler HANDLER;
    private static PersistEngineDataManager PERSIST_MGR;

    private static IBatchQueueHandlerProvider NO_HANDLER_PROVIDER =
        new IBatchQueueHandlerProvider() {
            @SuppressWarnings("rawtypes")
            @Override
            @Nonnull
            public Collection<BatchQueueHandlerSupport> getHandlers() {
                return Collections.emptyList();
            }
        };

    private static IBatchQueueHandlerProvider HANDLER_WITH_EMPTY_QUEUES_PROVIDER =
        new IBatchQueueHandlerProvider() {
            @SuppressWarnings("rawtypes")
            @Override
            @Nonnull
            public Collection<BatchQueueHandlerSupport> getHandlers() {
                final BatchQueueHandlerSupport mock1 = Mockito.mock(BatchQueueHandlerSupport.class);
                Mockito.when(mock1.getQueue()).thenReturn(new LinkedBlockingQueue<Object>());

                final BatchQueueHandlerSupport mock2 = Mockito.mock(BatchQueueHandlerSupport.class);
                Mockito.when(mock2.getQueue()).thenReturn(new LinkedBlockingQueue<Object>());
                return Lists.newArrayList(mock1, mock2);
            }
        };

    @SuppressWarnings("rawtypes")
    private static IBatchQueueHandlerProvider FALSE_STMT_PROVIDER =
        new IBatchQueueHandlerProvider() {
            private BatchQueueHandlerSupport<IArchiveSample> _handler;

            @SuppressWarnings({ "unchecked", "synthetic-access" })
            @Override
            @Nonnull
            public Collection<BatchQueueHandlerSupport> getHandlers() {
                if (_handler == null) {
                    _handler = new FalseFillStmtHandler(IArchiveSample.class,
                                                        HANDLER.getDatabaseName(),
                                                        new LinkedBlockingQueue<IArchiveSample>(TestSampleProvider.SAMPLES_MIN));
                }
                return (Collection) Lists.newArrayList(_handler);
            }
        };

    private static File RESCUE_PATH;
    private static File RESCUE_STMTS;


    @BeforeClass
    public static void setup() throws ArchiveConnectionException {
        LogManager.resetConfiguration(); // might already be read from another .log4j fragment (on default eclipse includes all fragments)
        PropertyConfigurator.configure("../../../products/DESY/plugins/org.csstudio.archive.common.engine.product.log4j/log4j.properties");


        RESCUE_PATH = new File("rescue");
        RESCUE_STMTS = new File(RESCUE_PATH, "stmts/failed.sql");
        Assert.assertTrue(RESCUE_STMTS.exists());

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
        Assert.assertTrue(RESCUE_STMTS.length() == 0L);

    }
    @Test
    public void testOnProviderForHandlersWithEmptyQueues() {
        final PersistDataWorker worker =
            new PersistDataWorker(PERSIST_MGR, "Test Data Worker", 1000, HANDLER_WITH_EMPTY_QUEUES_PROVIDER);
        worker.run();
        Assert.assertTrue(RESCUE_STMTS.length() == 0L);
    }

    @Test
    public void testOnHandlerWithWronglyFilledStatement() throws IOException {
        final PersistDataWorker worker =
            new PersistDataWorker(PERSIST_MGR, "Test Data Worker", 1000, FALSE_STMT_PROVIDER);
        worker.run();
        Assert.assertTrue(RESCUE_STMTS.length() > 0L);

        final SimpleLineProcessor lineProcessor = new SimpleLineProcessor();
        Files.readLines(RESCUE_STMTS, Charset.defaultCharset(), lineProcessor);
        Assert.assertTrue(lineProcessor.getI() == 3);

        worker.run();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        Files.write(new byte[0], RESCUE_STMTS);
        Assert.assertTrue(RESCUE_STMTS.length() == 0L);
    }
}
