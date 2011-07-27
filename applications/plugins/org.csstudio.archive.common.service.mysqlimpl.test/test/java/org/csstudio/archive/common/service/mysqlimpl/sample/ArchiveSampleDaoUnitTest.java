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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_CHANNEL_ID;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.COLUMN_TIME;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE_H;
import static org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl.TAB_SAMPLE_M;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.CHANNEL_ID;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.END;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.SAMPLES;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.START;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.mysqlimpl.channel.ArchiveChannelDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link ArchiveSampleDaoImpl}.
 *
 * @author bknerr
 * @since 27.07.2011
 */
public class ArchiveSampleDaoUnitTest extends AbstractDaoTestSetup {
    private static IArchiveSampleDao SAMPLE_DAO;
    private static IArchiveChannelDao CHANNEL_DAO;

    @BeforeClass
    public static void setupDao() {
        SAMPLE_DAO = new ArchiveSampleDaoImpl(HANDLER, PERSIST_MGR);
        CHANNEL_DAO = new ArchiveChannelDaoImpl(HANDLER, PERSIST_MGR);
    }

    @Test
    public void testCreateSamples() throws ArchiveDaoException, InterruptedException, ArchiveConnectionException, SQLException {
        SAMPLE_DAO.createSamples(SAMPLES);

        Thread.sleep(2500);

        final IArchiveChannel channel = CHANNEL_DAO.retrieveChannelById(CHANNEL_ID);
        final IArchiveSample<Object,ISystemVariable<Object>> sample =
            SAMPLE_DAO.retrieveLatestSampleBeforeTime(channel, END.plusMillis(1L));

        Assert.assertNotNull(sample);
        Assert.assertEquals(sample.getSystemVariable().getTimestamp(), END);

        assertRawSamples(channel);

        assertPerMinuteSamples(channel);

        assertPerHourSamples(channel);

        undoCreateSamples();
    }

    private void assertPerHourSamples(final IArchiveChannel channel) throws ArchiveDaoException {
        final Collection<IArchiveSample<Object, ISystemVariable<Object>>> hourSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.AVG_PER_HOUR, channel, START, END);
        Assert.assertNotNull(hourSamples);
        Assert.assertFalse(hourSamples.isEmpty());

        TimeInstant lastTime = hourSamples.iterator().next().getSystemVariable().getTimestamp().minusMillis(1000*60*60);
        for (final IArchiveSample<Object, ISystemVariable<Object>> minSample : hourSamples) {
            final TimeInstant curTime = minSample.getSystemVariable().getTimestamp();
            Assert.assertTrue(!curTime.isBefore(lastTime.plusMillis(1000*60)));
            lastTime= curTime;
        }
    }

    private void assertPerMinuteSamples(final IArchiveChannel channel) throws ArchiveDaoException {
        final Collection<IArchiveSample<Object, ISystemVariable<Object>>> minSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.AVG_PER_MINUTE, channel, START, END);
        Assert.assertNotNull(minSamples);
        Assert.assertFalse(minSamples.isEmpty());

        TimeInstant lastTime = minSamples.iterator().next().getSystemVariable().getTimestamp().minusMillis(1000*60);
        for (final IArchiveSample<Object, ISystemVariable<Object>> minSample : minSamples) {
            final TimeInstant curTime = minSample.getSystemVariable().getTimestamp();
            Assert.assertTrue(!curTime.isBefore(lastTime.plusMillis(1000*60)));
            lastTime= curTime;
        }
    }

    private void assertRawSamples(final IArchiveChannel channel) throws ArchiveDaoException {
        final Collection<IArchiveSample<Object, ISystemVariable<Object>>> rawSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.RAW, channel, START, END);
        Assert.assertNotNull(rawSamples);
        Assert.assertFalse(rawSamples.isEmpty());
        Assert.assertEquals(SAMPLES.size(), rawSamples.size());
    }

    private static void undoCreateSamples() throws ArchiveConnectionException, SQLException {
        final Connection connection = HANDLER.getConnection();
        final Statement stmt = connection.createStatement();
        executeStatementForTable(stmt, TAB_SAMPLE);
        executeStatementForTable(stmt, TAB_SAMPLE_M);
        executeStatementForTable(stmt, TAB_SAMPLE_H);
        stmt.close();
    }

    private static void executeStatementForTable(@Nonnull final Statement stmt,
                                                 @Nonnull final String table) throws SQLException {
        stmt.execute("DELETE FROM " + table + " " +
                     "WHERE " + COLUMN_TIME + " between " + START.minusMillis(1L).getNanos() + " AND " + END.plusMillis(1L).getNanos() + " " +
                     "AND " + COLUMN_CHANNEL_ID + "=" + CHANNEL_ID.asString());
    }

    @AfterClass
    public static void undoBatchedStatements() throws ArchiveConnectionException, SQLException {
        undoCreateSamples();
    }

}
