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
package org.csstudio.archive.common.service.mysqlimpl.enginestatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Integration test for {@link ArchiveEngineStatusDaoImpl} for batch writes.
 *
 * @author bknerr
 * @since 22.07.2011
 */
public class ArchiveEngineStatusDaoWriteBatchUnitTest extends AbstractDaoTestSetup {

    private static IArchiveEngineStatusDao DAO;
    private static final ArchiveEngineId ENGINE_SINGLE_ID = new ArchiveEngineId(997L);
    private static final ArchiveEngineId ENGINE_BATCH_ID = new ArchiveEngineId(999L);
    private static final String BATCH_INSERT_INFO = "Batch";
    private static final String SINGLE_INSERT_INFO = "Single";

    @BeforeClass
    public static void setupDao() {
        DAO = new ArchiveEngineStatusDaoImpl(HANDLER, PERSIST_MGR);
    }

    /**
     * This test starts one or more {@link org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistDataWorker} implicitly that have their own
     * database connection. Hence the normal rollback in the test setup won't work.
     *
     * The test data has to be removed in the {@link this#teardown()} method again.
     *
     * @throws ArchiveDaoException
     * @throws InterruptedException
     */
    @Test
    public void testEngineStatusSubmission() throws ArchiveDaoException, InterruptedException {

        final TimeInstant now = TimeInstantBuilder.fromNow();

        final EngineMonitorStatus on = EngineMonitorStatus.ON;

        DAO.createMgmtEntry(new ArchiveEngineStatus(ENGINE_SINGLE_ID, on, now, SINGLE_INSERT_INFO));

        Thread.sleep(2500);

        final IArchiveEngineStatus status = DAO.retrieveLastEngineStatus(ENGINE_SINGLE_ID, now.plusMillis(1000L));

        Assert.assertNotNull(status);
        Assert.assertEquals(ENGINE_SINGLE_ID, status.getEngineId());
        Assert.assertEquals(on, status.getStatus());
        Assert.assertEquals(SINGLE_INSERT_INFO, status.getInfo());
    }

    @Test
    public void testEngineStatusSubmissionLargeBatch() throws ArchiveDaoException, InterruptedException, SQLException, ArchiveConnectionException {
        final TimeInstant now = TimeInstantBuilder.fromNow();

        final Collection<IArchiveEngineStatus> first = Lists.newLinkedList();
        final Collection<IArchiveEngineStatus> second = Lists.newLinkedList();
        final Collection<IArchiveEngineStatus> third = Lists.newLinkedList();
        final TimeInstant lastOne = prepareMgmtEntryBatches(now, first, second, third);

        DAO.createMgmtEntries(first);
        Thread.sleep(500);
        DAO.createMgmtEntries(second);
        Thread.sleep(500);
        DAO.createMgmtEntries(third);
        Thread.sleep(2500);

        final IArchiveEngineStatus status = DAO.retrieveLastEngineStatus(ENGINE_BATCH_ID, now.plusMillis(7000000L));

        assertLastMgmtEntry(lastOne, status);
        assertMgmtEntryBatches();
    }

    private void assertMgmtEntryBatches() throws ArchiveConnectionException, SQLException {
        final Connection connection = HANDLER.getConnection();
        final Statement stmt = connection.createStatement();
        final ResultSet resultSet =
            stmt.executeQuery("SELECT count(*) FROM " + HANDLER.getDatabaseName() + "." + ArchiveEngineStatusDaoImpl.TAB +
                              " WHERE engine_id=" + ENGINE_BATCH_ID.asString() + " AND info='" + BATCH_INSERT_INFO + "'");
        Assert.assertNotNull(resultSet);
        Assert.assertTrue(resultSet.next());
        final int count = resultSet.getInt(1);
        Assert.assertTrue(count == 6001);
        stmt.close();
    }


    private void assertLastMgmtEntry(@Nonnull final TimeInstant lastOne,
                                     @Nonnull final IArchiveEngineStatus status) {
        Assert.assertNotNull(status);
        Assert.assertEquals(ENGINE_BATCH_ID, status.getEngineId());
        Assert.assertEquals(ENGINE_BATCH_ID, status.getEngineId());
        Assert.assertTrue(lastOne.equals(status.getTimestamp()));
        Assert.assertEquals(BATCH_INSERT_INFO, status.getInfo());
    }

    @Nonnull
    private TimeInstant prepareMgmtEntryBatches(@Nonnull final TimeInstant now,
                                                @Nonnull final Collection<IArchiveEngineStatus> first,
                                                @Nonnull final Collection<IArchiveEngineStatus> second,
                                                @Nonnull final Collection<IArchiveEngineStatus> third) {
        for (int i = 0; i < 2000; i++) {
            final EngineMonitorStatus st = i%2 > 0 ? EngineMonitorStatus.ON : EngineMonitorStatus.OFF;

            first.add(new ArchiveEngineStatus(ENGINE_BATCH_ID, st, now.plusMillis(i*1000), BATCH_INSERT_INFO));
            second.add(new ArchiveEngineStatus(ENGINE_BATCH_ID, st, now.plusMillis(i*1000 + 2000000), BATCH_INSERT_INFO));
            third.add(new ArchiveEngineStatus(ENGINE_BATCH_ID, st, now.plusMillis(i*1000 + 4000000), BATCH_INSERT_INFO));
        }
        final TimeInstant lastOne = now.plusMillis(6000001L);
        third.add(new ArchiveEngineStatus(ENGINE_BATCH_ID, EngineMonitorStatus.ON, lastOne, BATCH_INSERT_INFO));
        return lastOne;
    }

    @AfterClass
    public static void teardown() throws SQLException, ArchiveConnectionException {

        undoTestEngineStatusSubmissionModification(ENGINE_BATCH_ID, BATCH_INSERT_INFO);
        undoTestEngineStatusSubmissionModification(ENGINE_SINGLE_ID, SINGLE_INSERT_INFO);
    }

    private static void undoTestEngineStatusSubmissionModification(@Nonnull final ArchiveEngineId id,
                                                                   @Nonnull final String info)
                                                                   throws ArchiveConnectionException,
                                                                          SQLException {
        final Connection connection = HANDLER.getConnection();
        final Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM " + HANDLER.getDatabaseName() + "." + ArchiveEngineStatusDaoImpl.TAB +
                     " WHERE engine_id=" + id.asString() + " AND info='" + info + "'");
        stmt.close();
    }
}
