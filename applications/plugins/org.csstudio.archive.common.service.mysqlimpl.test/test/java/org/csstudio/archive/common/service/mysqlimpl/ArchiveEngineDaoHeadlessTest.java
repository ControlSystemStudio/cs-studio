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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.engine.ArchiveEngineId;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.engine.ArchiveEngineDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.engine.IArchiveEngineDao;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Test;

/**
 * Integration test for {@link ArchiveEngineDaoImpl}.
 *
 * @author bknerr
 * @since 18.05.2011
 */
public class ArchiveEngineDaoHeadlessTest extends AbstractDaoTestSetup {

    @Test
    public void testEngineDao() throws ArchiveConnectionException, ArchiveDaoException, SQLException, MalformedURLException {
        final IArchiveEngineDao dao = new ArchiveEngineDaoImpl(HANDLER, PERSIST_MGR);
        final IArchiveEngine noEngine = dao.retrieveEngineById(ArchiveEngineId.NONE);
        Assert.assertNull(noEngine);

        final ArchiveEngineId id = new ArchiveEngineId(1L);

        IArchiveEngine engine = dao.retrieveEngineById(id);
        assertEngineLookup(id, engine);

        engine = dao.retrieveEngineByName("TestEngine");
        assertEngineLookup(id, engine);

        final TimeInstant time = TimeInstantBuilder.fromNow();
        dao.updateEngineAlive(id, time);

        engine = dao.retrieveEngineById(id);
        Assert.assertTrue(time.equals(engine.getLastAliveTime()));
    }

    private void assertEngineLookup(@Nonnull final ArchiveEngineId id,
                                    @Nonnull final IArchiveEngine engine) throws MalformedURLException {
        Assert.assertEquals(id, engine.getId());
        Assert.assertNotNull(engine);
        Assert.assertEquals(new URL("http://krykpcj.desy.de:4811"), engine.getUrl());
        Assert.assertEquals(TimeInstantBuilder.fromNanos(1309478401000000000L), engine.getLastAliveTime());
    }

}
