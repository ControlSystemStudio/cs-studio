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

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import junit.framework.Assert;

import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.ArchiveChannelGroupDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.IArchiveChannelGroupDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link ArchiveChannelGroupDaoImpl}.
 *
 * @author bknerr
 * @since 07.07.2011
 */
public class ArchiveChannelGroupDaoUnitTest extends AbstractDaoTestSetup {

    private static IArchiveChannelGroupDao DAO;

    @BeforeClass
    public static void setupDao() {
        DAO = new ArchiveChannelGroupDaoImpl(HANDLER, PERSIST_MGR);
    }

    @Test
    public void testChannelGroupDao() throws ArchiveDaoException {
        Collection<IArchiveChannelGroup> groups =
            DAO.retrieveGroupsByEngineId(new ArchiveEngineId(26L));
        Assert.assertTrue(groups.isEmpty());

        final ArchiveEngineId engineId = new ArchiveEngineId(1L);
        groups = DAO.retrieveGroupsByEngineId(engineId);
        Assert.assertTrue(2 == groups.size());
        final Iterator<IArchiveChannelGroup> it = groups.iterator();
        IArchiveChannelGroup group = it.next();

        dispatchGroupCheckById(group);

        group = it.next();

        dispatchGroupCheckById(group);

    }

    private void dispatchGroupCheckById(@Nonnull final IArchiveChannelGroup group) {
        if (group.getId().intValue() == 1) {
            assertGroup(group, "TestGroup1", 1, "TestGroupDescription");
        } else if (group.getId().intValue() == 2) {
            assertGroup(group, "TestGroup2", 1, null);
        } else {
            Assert.fail("Channel group id unknown.");
        }
    }

    private void assertGroup(@Nonnull final IArchiveChannelGroup group,
                             @Nonnull final String name,
                             final int engineId,
                             @Nullable final String desc) {
        Assert.assertEquals(name, group.getName());
        Assert.assertTrue(engineId == group.getEngineId().intValue());
        Assert.assertEquals(desc, group.getDescription());
    }
}
