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
import java.util.Collections;
import java.util.regex.Pattern;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.ArchiveLimitsChannel;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.channelgroup.ArchiveChannelGroupId;
import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystem;
import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystemId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.sample.TestSampleProvider;
import org.csstudio.domain.common.service.DeleteResult;
import org.csstudio.domain.common.service.UpdateResult;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.internal.Lists;

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
        Collection<IArchiveChannel> channels = DAO.retrieveChannelsByNames(Sets.newHashSet("kommeniezuspadt"));
        Assert.assertTrue(channels.isEmpty());

        channels = DAO.retrieveChannelsByIds(Sets.newHashSet(ArchiveChannelId.NONE));
        Assert.assertTrue(channels.isEmpty());


        channels = DAO.retrieveChannelsByGroupId(ArchiveChannelGroupId.NONE);
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

        //from cache
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
        final ArchiveControlSystem cs = new ArchiveControlSystem(new ArchiveControlSystemId(1L), ControlSystem.EPICS_DEFAULT);

        Collection<IArchiveChannel> channels = DAO.retrieveChannelsByNames(Sets.newHashSet("enumChannel1"));
        Assert.assertFalse(channels.isEmpty());

        assertChannelContent(channels.iterator().next(), "enumChannel1", "EpicsEnum", new ArchiveChannelGroupId(2L),
                             cs, null, true, null);

        channels = DAO.retrieveChannelsByIds(Sets.newHashSet(new ArchiveChannelId(3L)));
        Assert.assertFalse(channels.isEmpty());

        assertChannelContent(channels.iterator().next(), "byteChannel1", "Byte", new ArchiveChannelGroupId(2L),
                             cs, TimeInstantBuilder.fromNanos(2000000000L), false, Limits.create(Byte.valueOf((byte) -128), Byte.valueOf((byte) 127)));
    }

    // CHECKSTYLE OFF : ParameterNumber
    private void assertChannelContent(@CheckForNull final IArchiveChannel channel,
                                      @Nonnull final String name,
                                      @Nonnull final String type,
                                      @Nonnull final ArchiveChannelGroupId grpId,
                                      @Nonnull final IArchiveControlSystem cs,
                                      @Nullable final TimeInstant time,
                                      final boolean enabled,
                                      @CheckForNull final Limits<?> limits) {
        // CHECKSTYLE ON : ParameterNumber
        Assert.assertNotNull(channel);
        Assert.assertEquals(name, channel.getName());
        Assert.assertEquals(type, channel.getDataType());
        Assert.assertEquals(grpId, channel.getGroupId());
        if (time != null) {
            Assert.assertTrue(time.equals(channel.getLatestTimestamp()));
        } else {
            Assert.assertNull(channel.getLatestTimestamp());
        }
        Assert.assertEquals(cs, channel.getControlSystem());
        Assert.assertTrue(enabled == channel.isEnabled());

        final Limits<?> resultLimits = channel.getDisplayLimits();
        if (limits != null) {
            Assert.assertNotNull(resultLimits);
            Assert.assertEquals(limits.getHigh(), resultLimits.getHigh());
            Assert.assertEquals(limits.getLow(), resultLimits.getLow());
        } else {
            Assert.assertNull(resultLimits);
        }
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

        final Collection<IArchiveChannel> channels = DAO.retrieveChannelsByIds(Sets.newHashSet(id));
        Assert.assertFalse(channels.isEmpty());

        final Limits<V> limits = DAO.retrieveDisplayRanges(channels.iterator().next().getName());
        Assert.assertNotNull(limits);
        Assert.assertEquals(Double.valueOf(32.0), limits.getHigh());
        Assert.assertEquals(Double.valueOf(-1.0), limits.getLow());

        undoUpdateDisplayRanges();
    }

    @Test
    public void testCreateChannels() throws ArchiveDaoException {
        final IArchiveControlSystem cs = new ArchiveControlSystem(new ArchiveControlSystemId(1L), ControlSystem.EPICS_DEFAULT);
        final ArchiveChannelGroupId grpId = new ArchiveChannelGroupId(1L);
        final ArchiveChannel chan1 =
            new ArchiveChannel(ArchiveChannelId.NONE,
                               "nolimits",
                               "String",
                               grpId,
                               null,
                               cs,
                               false);
        final Collection<IArchiveChannel> directResult =
            DAO.createChannels(Collections.singleton((IArchiveChannel) chan1));
        Assert.assertTrue(directResult.isEmpty());

        final Collection<IArchiveChannel> channels = DAO.retrieveChannelsByNames(Sets.newHashSet("nolimits"));
        Assert.assertFalse(channels.isEmpty());
        assertChannelContent(channels.iterator().next(), "nolimits", "String", grpId, cs, null, false, null);

    }

    @Test
    public void testCreateMoreChannels() throws ArchiveDaoException {
        final IArchiveControlSystem cs = new ArchiveControlSystem(new ArchiveControlSystemId(1L), ControlSystem.EPICS_DEFAULT);
        final ArchiveChannelGroupId grpId = new ArchiveChannelGroupId(1L);
        IArchiveChannel chan1 =
            new ArchiveChannel(ArchiveChannelId.NONE,
                               "nolimits",
                               "String",
                               grpId,
                               null,
                               cs,
                               true);
        IArchiveChannel chan2 =
            new ArchiveLimitsChannel<Double>(ArchiveChannelId.NONE,
                               "withLimits",
                               "Double",
                               grpId,
                               null,
                               cs,
                               false,
                               Double.valueOf(0.0), Double.valueOf(10.0));

        final Collection<IArchiveChannel> directResult =
            DAO.createChannels(Lists.newArrayList(chan1, chan2));
        Assert.assertTrue(directResult.isEmpty());

        chan1 = DAO.retrieveChannelsByNames(Sets.newHashSet("nolimits")).iterator().next();
        assertChannelContent(chan1, "nolimits", "String", grpId, cs, null, true, null);
        chan2 = DAO.retrieveChannelsByNames(Sets.newHashSet("withlimits")).iterator().next();
        assertChannelContent(chan2, "withLimits", "Double", grpId, cs, null, false, Limits.create(Double.valueOf(0.0), Double.valueOf(10.0)));
    }

    @Test
    public void testCreateChannelsWithFailure() throws ArchiveDaoException {
        final IArchiveControlSystem cs = new ArchiveControlSystem(new ArchiveControlSystemId(1L), ControlSystem.EPICS_DEFAULT);
        final ArchiveChannelGroupId grpId = new ArchiveChannelGroupId(1L);
        final IArchiveChannel chan1 =
            new ArchiveChannel(ArchiveChannelId.NONE,
                               "fooNoLimits",
                               "String",
                               grpId,
                               null,
                               cs,
                               true);
        final IArchiveChannel chan2 =
            new ArchiveChannel(ArchiveChannelId.NONE,
                               "fooNoLimits",
                               "String",
                               grpId,
                               null,
                               cs,
                               false);


        final Collection<IArchiveChannel> directResult =
            DAO.createChannels(Lists.newArrayList(chan1, chan2));
        Assert.assertTrue(directResult.size() == 2);
    }

    @Test
    public void testDeleteNonExistentChannel() {
        final String name = "emeraldcity";
        final DeleteResult result = DAO.deleteChannel(name);
        Assert.assertTrue(result.failed());

        final String msg = "Deletion of channel failed. Number of updated rows != 1.";
        Assert.assertTrue(msg.equals(result.getMessage()));
    }

    @Test
    public void testDeleteExistingChannel() {
        final DeleteResult result = DAO.deleteChannel("doubleChannel1");
        Assert.assertTrue(result.succeeded());
    }

    @Test
    public void testUpdateEnabledInfo() throws ArchiveDaoException {
        UpdateResult result = DAO.updateChannelEnabledFlag("doubleChannel1", false);
        Assert.assertTrue(result.succeeded());

        Collection<IArchiveChannel> channels = DAO.retrieveChannelsByNames(Sets.newHashSet("doubleChannel1"));
        Assert.assertFalse(channels.isEmpty());
        Assert.assertFalse(channels.iterator().next().isEnabled());

        result = DAO.updateChannelEnabledFlag("doubleChannel1", true);
        Assert.assertTrue(result.succeeded());

        channels = DAO.retrieveChannelsByNames(Sets.newHashSet("doubleChannel1"));
        Assert.assertFalse(channels.isEmpty());
        Assert.assertTrue(channels.iterator().next().isEnabled());
    }

    @Test
    public void testUpdateEnabledInfoInvalid() {
        final UpdateResult result = DAO.updateChannelEnabledFlag("eeviac", false);
        Assert.assertTrue(result.failed());
        Assert.assertEquals("Channel 'eeviac' has not been updated, doesn't it exist?", result.getMessage());

    }

    @AfterClass
    public static void undoUpdateDisplayRanges() throws SQLException, ArchiveConnectionException {
        final Statement stmt = HANDLER.createConnection().createStatement();
        stmt.execute("UPDATE channel SET display_high='20.0', display_low='10.0' WHERE id=1");
        stmt.close();
    }
}
