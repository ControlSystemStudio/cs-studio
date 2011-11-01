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
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.CHANNEL_ID_1ST;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.CHANNEL_ID_2ND;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.END;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.SAMPLES;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.SAMPLES_HOUR;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.SAMPLES_MIN;
import static org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider.START;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.mysqlimpl.channel.ArchiveChannelDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.sample.ArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.junit.AfterClass;
import org.junit.Test;

import com.google.common.collect.Sets;

/**
 * Integration test for {@link ArchiveSampleDaoImpl}.
 *
 * @author bknerr
 * @since 28.07.2011
 */
public class ArchiveSampleDaoCreateUnitTest extends AbstractDaoTestSetup {

    private static IArchiveSampleDao SAMPLE_DAO;
    private static IArchiveChannelDao CHANNEL_DAO;

    /**
     * Constructor.
     */
    public ArchiveSampleDaoCreateUnitTest() {
        super(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeHook() throws ArchiveConnectionException, SQLException {
        CHANNEL_DAO = new ArchiveChannelDaoImpl(HANDLER, PERSIST_MGR);
        SAMPLE_DAO = new ArchiveSampleDaoImpl(HANDLER, PERSIST_MGR, CHANNEL_DAO);
    }

    @Test
    public void testTriggerMinuteSampleCreation() throws ArchiveDaoException, InterruptedException, ArchiveConnectionException, SQLException {
        SAMPLE_DAO.createSamples(SAMPLES_MIN);

        Thread.sleep(2500);

        final Collection<IArchiveChannel> channels = CHANNEL_DAO.retrieveChannelsByIds(Sets.newHashSet(CHANNEL_ID_1ST));
        Assert.assertTrue(channels.size() == 1);

        final IArchiveChannel channel = channels.iterator().next();
        Assert.assertTrue(channel.getLatestTimestamp().equals(START.plusMillis(1000*60)));

        final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> minSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.AVG_PER_MINUTE, channel, START, START.plusMillis(1000*60));

        assertSamples(minSamples);

        undoSampleCreation(START, START.plusMillis(1000*60), CHANNEL_ID_1ST);
    }

    @Test
    public void testTriggerHourSampleCreation() throws ArchiveDaoException, InterruptedException, ArchiveConnectionException, SQLException {
        SAMPLE_DAO.createSamples(SAMPLES_HOUR);

        Thread.sleep(2500);

        final Collection<IArchiveChannel> channels = CHANNEL_DAO.retrieveChannelsByIds(Sets.newHashSet(CHANNEL_ID_1ST));
        Assert.assertTrue(channels.size() == 1);

        final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> hourSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.AVG_PER_HOUR, channels.iterator().next(), START, START.plusMillis(1000*60*60));

        assertSamples(hourSamples);

        undoSampleCreation(START, START.plusMillis(1000*60*60), CHANNEL_ID_1ST);
    }

    @SuppressWarnings("rawtypes")
    private void assertSamples(@Nonnull final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> samples) {
        Assert.assertNotNull(samples);
        // no sample before the first one, so the first one is persisted immediately, the second one is
        // aggregated, the third one is persisted again
        Assert.assertEquals(2, samples.size());

        final Iterator<IArchiveSample<Serializable, ISystemVariable<Serializable>>> iterator = samples.iterator();
        IArchiveSample<Serializable, ISystemVariable<Serializable>> sample = iterator.next();
        Assert.assertEquals(Double.valueOf(1.0), sample.getValue());
        Assert.assertTrue(sample instanceof ArchiveMinMaxSample);
        Assert.assertEquals(Double.valueOf(1.0), ((ArchiveMinMaxSample) sample).getMinimum());
        Assert.assertEquals(Double.valueOf(1.0), ((ArchiveMinMaxSample) sample).getMaximum());

        sample = iterator.next();
        Assert.assertEquals(Double.valueOf(2.5), sample.getValue());
        Assert.assertTrue(sample instanceof ArchiveMinMaxSample);
        Assert.assertEquals(Double.valueOf(2.0), ((ArchiveMinMaxSample) sample).getMinimum());
        Assert.assertEquals(Double.valueOf(3.0), ((ArchiveMinMaxSample) sample).getMaximum());
    }

    @Test
    public void testCreateSamples() throws ArchiveDaoException, InterruptedException, ArchiveConnectionException, SQLException {
        SAMPLE_DAO.createSamples(SAMPLES);

        Thread.sleep(2500);

        final Collection<IArchiveChannel> channels = CHANNEL_DAO.retrieveChannelsByIds(Sets.newHashSet(CHANNEL_ID_2ND));
        Assert.assertTrue(channels.size() == 1);
        final IArchiveChannel channel = channels.iterator().next();

        final IArchiveSample<Serializable, ISystemVariable<Serializable>> sample =
            SAMPLE_DAO.retrieveLatestSampleBeforeTime(channel, END.plusMillis(1L));

        Assert.assertNotNull(sample);
        Assert.assertEquals(sample.getSystemVariable().getTimestamp(), END);

        assertRawSamples(channel);

        assertPerMinuteSamples(channel);

        assertPerHourSamples(channel);

        undoSampleCreation(START, END, CHANNEL_ID_1ST);
    }

    private void assertPerHourSamples(@Nonnull final IArchiveChannel channel) throws ArchiveDaoException {
        final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> hourSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.AVG_PER_HOUR, channel, START, END);
        Assert.assertNotNull(hourSamples);
        Assert.assertFalse(hourSamples.isEmpty());

        TimeInstant lastTime = hourSamples.iterator().next().getSystemVariable().getTimestamp().minusMillis(1000*60*60);
        for (final IArchiveSample<Serializable, ISystemVariable<Serializable>> minSample : hourSamples) {
            final TimeInstant curTime = minSample.getSystemVariable().getTimestamp();
            Assert.assertTrue(!curTime.isBefore(lastTime.plusMillis(1000*60)));
            lastTime= curTime;
        }
    }

    private void assertPerMinuteSamples(@Nonnull final IArchiveChannel channel) throws ArchiveDaoException {
        final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> minSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.AVG_PER_MINUTE, channel, START, END);
        Assert.assertNotNull(minSamples);
        Assert.assertFalse(minSamples.isEmpty());

        TimeInstant lastTime = minSamples.iterator().next().getSystemVariable().getTimestamp().minusMillis(1000*60);
        for (final IArchiveSample<Serializable, ISystemVariable<Serializable>> minSample : minSamples) {
            final TimeInstant curTime = minSample.getSystemVariable().getTimestamp();
            Assert.assertTrue(!curTime.isBefore(lastTime.plusMillis(1000*60)));
            lastTime= curTime;
        }
    }

    private void assertRawSamples(@Nonnull final IArchiveChannel channel) throws ArchiveDaoException {
        final Collection<IArchiveSample<Serializable, ISystemVariable<Serializable>>> rawSamples =
            SAMPLE_DAO.retrieveSamples(DesyArchiveRequestType.RAW, channel, START, END);
        Assert.assertNotNull(rawSamples);
        Assert.assertFalse(rawSamples.isEmpty());
        Assert.assertEquals(SAMPLES.size(), rawSamples.size());
    }

    private static void undoSampleCreation(@Nonnull final TimeInstant start,
                                           @Nonnull final TimeInstant end,
                                           @Nonnull final ArchiveChannelId channelId) throws ArchiveConnectionException, SQLException {
        executeDeleteStatementForTable(TAB_SAMPLE, channelId, start, end);
        executeDeleteStatementForTable(TAB_SAMPLE_M, channelId, start, end);
        executeDeleteStatementForTable(TAB_SAMPLE_H, channelId, start, end);
    }

    private static void executeDeleteStatementForTable(@Nonnull final String table,
                                                       @Nonnull final ArchiveChannelId channelId,
                                                       @Nonnull final TimeInstant start,
                                                       @Nonnull final TimeInstant end) throws SQLException, ArchiveConnectionException {
        final Connection connection = HANDLER.createConnection();
        final Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM " + table + " " +
                     "WHERE " + COLUMN_TIME + " between " + start.minusMillis(1L).getNanos() + " AND " + end.plusMillis(1L).getNanos() + " " +
                     "AND " + COLUMN_CHANNEL_ID + "=" + channelId.asString());
        stmt.close();
        connection.close();
    }

    @AfterClass
    public static void undoBatchedStatements() throws ArchiveConnectionException, SQLException {
        undoSampleCreation(START, END.plusMillis(1L), CHANNEL_ID_2ND);
    }
}
