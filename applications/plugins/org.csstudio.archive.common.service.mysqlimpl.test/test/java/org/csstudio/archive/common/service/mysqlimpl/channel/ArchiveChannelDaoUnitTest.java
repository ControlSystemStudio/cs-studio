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
package org.csstudio.archive.common.service.mysqlimpl.channel;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link ArchiveChannelDaoImpl}.
 *
 * @author bknerr
 * @since 25.07.2011
 */
public class ArchiveChannelDaoUnitTest extends AbstractDaoTestSetup {

    private static IArchiveChannelDao DAO;

    @BeforeClass
    public static void setupDao() {
        DAO = new ArchiveChannelDaoImpl(HANDLER, PERSIST_MGR);
    }

    @Test
    public void testNoChannel() throws ArchiveDaoException {
        IArchiveChannel channel = DAO.retrieveChannelBy("kommeniezuspadt");
        Assert.assertNull(channel);

        channel = DAO.retrieveChannelById(ArchiveChannelId.NONE);
        Assert.assertNull(channel);

        Collection<IArchiveChannel> channels = DAO.retrieveChannelsByGroupId(ArchiveChannelGroupId.NONE);
        Assert.assertNotNull(channels);
        Assert.assertTrue(channels.isEmpty());

        channels = DAO.retrieveChannelsByNamePattern(Pattern.compile("DasGuteA"));
        Assert.assertNotNull(channels);
        Assert.assertTrue(channels.isEmpty());
    }

    @Test
    public void testChannelsRetrievalByGroupIdNone() throws ArchiveDaoException {
        final Collection<IArchiveChannel> channels = DAO.retrieveChannelsByGroupId(ArchiveChannelGroupId.NONE);
        Assert.assertNotNull(channels);
        Assert.assertTrue(channels.isEmpty());
    }

    @Test
    public void testChannelsRetrievalByGroupId() throws ArchiveDaoException {
        Collection<IArchiveChannel> channels = DAO.retrieveChannelsByGroupId(new ArchiveChannelGroupId(1L));
        Assert.assertNotNull(channels);
        Assert.assertFalse(channels.isEmpty());
        Assert.assertTrue(channels.size() == 2);

        channels = DAO.retrieveChannelsByGroupId(new ArchiveChannelGroupId(2L));
        Assert.assertNotNull(channels);
        Assert.assertFalse(channels.isEmpty());
        Assert.assertTrue(channels.size() == 3);
    }

    @Test
    public void testChannelsRetrievalByRegExp() throws ArchiveDaoException {
        Collection<IArchiveChannel> channels = DAO.retrieveChannelsByNamePattern(Pattern.compile("^double.*$"));
        Assert.assertNotNull(channels);
        Assert.assertFalse(channels.isEmpty());
        Assert.assertTrue(channels.size() == 2);

        //f rom cache
        channels = DAO.retrieveChannelsByNamePattern(Pattern.compile("doubleChannel1"));
        Assert.assertNotNull(channels);
        Assert.assertFalse(channels.isEmpty());
        Assert.assertTrue(channels.size() == 1);
        Assert.assertEquals("doubleChannel1", channels.iterator().next().getName());

        // fresh from database?
        channels = DAO.retrieveChannelsByNamePattern(Pattern.compile("enumChannel1"));
        Assert.assertNotNull(channels);
        Assert.assertFalse(channels.isEmpty());
        Assert.assertTrue(channels.size() == 1);
        Assert.assertEquals("enumChannel1", channels.iterator().next().getName());
    }

    @Test
    public void testChannelRetrieval() throws ArchiveDaoException {
        IArchiveChannel channel = DAO.retrieveChannelBy("enumChannel1");

        assertChannelContent(channel, "EpicsEnum", "enumChannel1", null, null, new ArchiveChannelGroupId(2L), 1262307723000000000L);

        channel = DAO.retrieveChannelById(new ArchiveChannelId(3L));
        assertChannelContent(channel, "Byte", "byteChannel1", Byte.valueOf((byte) -128), Byte.valueOf((byte) 127), new ArchiveChannelGroupId(2L), 2000000000L);
    }

    private void assertChannelContent(@Nonnull final IArchiveChannel channel,
                                      @Nonnull final String datatype,
                                      @Nonnull final String name,
                                      @Nonnull final Number low,
                                      @Nonnull final Number high,
                                      @Nonnull final ArchiveChannelGroupId groupId,
                                      final long nanos) {
        Assert.assertNotNull(channel);
        Assert.assertEquals(datatype, channel.getDataType());
        Assert.assertEquals(name, channel.getName());
        Assert.assertEquals("EpicsDefault", channel.getControlSystem().getName());
        Assert.assertEquals(ControlSystem.EPICS_DEFAULT.getType(), channel.getControlSystem().getType());
        if (low != null) {
            Assert.assertEquals(low, channel.getDisplayLimits().getLow());
        }
        if (high != null) {
            Assert.assertEquals(high, channel.getDisplayLimits().getHigh());
        }
        Assert.assertEquals(groupId, channel.getGroupId());
        Assert.assertEquals(TimeInstantBuilder.fromNanos(nanos), channel.getLatestTimestamp());
    }

    @Test
    public <V extends Comparable<? super V>> void testRetrieveDisplayRanges() throws ArchiveDaoException {
        final Limits<V> limits = DAO.retrieveDisplayRanges("doubleChannel2");
        Assert.assertNotNull(limits);
        Assert.assertEquals(Double.valueOf(25.0), limits.getHigh());
        Assert.assertEquals(Double.valueOf(5.0), limits.getLow());
    }

    @Test
    public <V extends Comparable<? super V>> void testRetrieveNullDisplayRanges() throws ArchiveDaoException {
        final Limits<V> limits = DAO.retrieveDisplayRanges("enumChannel1");
        Assert.assertNull(limits);
    }

    @Test
    public <V extends Comparable<? super V>> void testUpdateDisplayRanges() throws ArchiveDaoException, InterruptedException, ArchiveConnectionException, SQLException {
        final ArchiveChannelId id = TestSampleProvider.CHANNEL_ID_1ST;
        DAO.updateDisplayRanges(id, Double.valueOf(-1.0), Double.valueOf(32.0));

        Thread.sleep(3000);

        final IArchiveChannel channel = DAO.retrieveChannelById(id);

        final Limits<V> limits = DAO.retrieveDisplayRanges(channel.getName());
        Assert.assertNotNull(limits);
        Assert.assertEquals(Double.valueOf(32.0), limits.getHigh());
        Assert.assertEquals(Double.valueOf(-1.0), limits.getLow());

        undoUpdateDisplayRanges();
    }

    @AfterClass
    public static void undoUpdateDisplayRanges() throws SQLException, ArchiveConnectionException {
        final Statement stmt = HANDLER.createConnection().createStatement();
        stmt.execute("UPDATE channel SET display_high='20.0', display_low='10.0' WHERE id=1");
        stmt.close();
    }
}
