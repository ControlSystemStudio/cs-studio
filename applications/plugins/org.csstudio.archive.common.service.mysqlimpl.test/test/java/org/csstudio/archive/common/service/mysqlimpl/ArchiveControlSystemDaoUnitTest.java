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

import junit.framework.Assert;

import org.csstudio.archive.common.service.controlsystem.ArchiveControlSystemId;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.mysqlimpl.controlsystem.ArchiveControlSystemDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.controlsystem.IArchiveControlSystemDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.domain.desy.system.ControlSystem;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Integration test for {@link ArchiveControlSystemDaoUnitTest}.
 *
 * @author bknerr
 * @since 25.07.2011
 */
public class ArchiveControlSystemDaoUnitTest extends AbstractDaoTestSetup {

    private static IArchiveControlSystemDao DAO;

    @BeforeClass
    public static void setupDao() {
        DAO = new ArchiveControlSystemDaoImpl(HANDLER, PERSIST_MGR);
    }

    @Test
    public void testRetrieveControlSystem() throws ArchiveDaoException {
        final ArchiveControlSystemId noId = ArchiveControlSystemId.NONE;
        final IArchiveControlSystem noCs =
            DAO.retrieveControlSystemById(noId);
        Assert.assertNull(noCs);

        final ArchiveControlSystemId id = new ArchiveControlSystemId(1L);
        final IArchiveControlSystem cs =
            DAO.retrieveControlSystemById(id);
        Assert.assertNotNull(cs);
        Assert.assertEquals(id, cs.getId());
        Assert.assertEquals("EpicsDefault", cs.getName());
        Assert.assertEquals(ControlSystem.EPICS_DEFAULT, cs.getType());
    }
}
