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
package org.csstudio.archive.common.service.mysqlimpl.channelstatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channelstatus.ArchiveChannelStatus;
import org.csstudio.archive.common.service.channelstatus.IArchiveChannelStatus;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractDaoTestSetup;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Integration test for {@link ArchiveChannelStatusDaoImpl}.
 *
 * @author bknerr
 * @since 26.07.2011
 */
public class ArchiveChannelStatusDaoUnitTest extends AbstractDaoTestSetup {

    private static IArchiveChannelStatusDao DAO;

    private static final ArchiveChannelId CHANNEL_ID = new ArchiveChannelId(1L);
    private static final TimeInstant NOW = TimeInstantBuilder.fromNow();


    @BeforeClass
    public static void setupDao() {
        DAO = new ArchiveChannelStatusDaoImpl(HANDLER, PERSIST_MGR);
    }


    @Test
    public void testCreateAndRetrieveStatus() throws ArchiveDaoException, InterruptedException, ArchiveConnectionException, SQLException {
        final TimeInstant fstTime = NOW.plusMillis(1000L);
        final IArchiveChannelStatus statusEntryFirst =
            new ArchiveChannelStatus(CHANNEL_ID,
                                     true,
                                     "test connect",
                                     fstTime);
        DAO.createChannelStatus(statusEntryFirst);


        final String sndInfo = "test disconnect";
        final TimeInstant sndTime = fstTime.plusMillis(1000L);
        final boolean sndConStatus = false;

        final IArchiveChannelStatus statusEntrySecond =
            new ArchiveChannelStatus(CHANNEL_ID,
                                     sndConStatus,
                                     sndInfo,
                                     sndTime);

        DAO.createChannelStatus(statusEntrySecond);

        Thread.sleep(2500);

        final Collection<IArchiveChannelStatus> coll =
            DAO.retrieveLatestStatusByChannelIds(Collections.singleton(CHANNEL_ID));

        Assert.assertTrue(coll.size() == 1);
        final IArchiveChannelStatus result = coll.iterator().next();
        Assert.assertEquals(result.getChannelId(), CHANNEL_ID);
        Assert.assertEquals(sndInfo, result.getInfo());
        Assert.assertEquals(sndTime, result.getTime());
        Assert.assertTrue(sndConStatus == result.isConnected());

        undoCreateAndRetrieveStatus();
    }

    @AfterClass
    public static void undoBatchedStatements() throws ArchiveConnectionException, SQLException {
        undoCreateAndRetrieveStatus();
    }

    private static void undoCreateAndRetrieveStatus() throws ArchiveConnectionException,
                                                             SQLException {
        final Connection connection = HANDLER.createConnection();
        final Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM " + HANDLER.getDatabaseName() + "." + ArchiveChannelStatusDaoImpl.TAB +
                     " WHERE channel_id=" + CHANNEL_ID.asString() + " AND time>" + NOW.getNanos());
        stmt.close();
    }
}
