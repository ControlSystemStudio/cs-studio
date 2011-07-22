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

import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.enginestatus.ArchiveEngineStatusDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.enginestatus.IArchiveEngineStatusDao;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link ArchiveEngineStatusDaoImpl}.
 *
 * @author bknerr
 * @since 11.07.2011
 */
public class ArchiveEngineStatusDaoUnitTest extends AbstractDaoTestSetup {

    private static IArchiveEngineStatusDao DAO;

    @BeforeClass
    public static void setupDao() {
        DAO = new ArchiveEngineStatusDaoImpl(HANDLER, PERSIST_MGR);
    }

    @Test
    public void testEngineStatusRetrieval() throws ArchiveDaoException {
        final IArchiveEngineStatus status = DAO.retrieveLastEngineStatus(ArchiveEngineId.NONE, TimeInstantBuilder.fromNow());
        Assert.assertNull(status);

        final ArchiveEngineId engineId = new ArchiveEngineId(1L);
        final IArchiveEngineStatus lastStatus = DAO.retrieveLastEngineStatus(engineId, TimeInstantBuilder.fromNow());
        Assert.assertNotNull(lastStatus);
        Assert.assertTrue(engineId.intValue() ==  lastStatus.getEngineId().intValue());
        Assert.assertEquals(EngineMonitorStatus.OFF, lastStatus.getStatus());
        Assert.assertEquals(TimeInstantBuilder.fromNanos(1309478401000000000L), lastStatus.getTimestamp());
        Assert.assertEquals(ArchiveEngineStatus.ENGINE_STOP, lastStatus.getInfo());
    }

}
