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

import org.csstudio.archive.common.service.mysqlimpl.channel.ArchiveChannelDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.ArchiveChannelGroupDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channelgroup.IArchiveChannelGroupDao;
import org.csstudio.archive.common.service.mysqlimpl.channelstatus.ArchiveChannelStatusDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.channelstatus.IArchiveChannelStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.controlsystem.ArchiveControlSystemDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.controlsystem.IArchiveControlSystemDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.engine.ArchiveEngineDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.engine.IArchiveEngineDao;
import org.csstudio.archive.common.service.mysqlimpl.enginestatus.ArchiveEngineStatusDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.enginestatus.IArchiveEngineStatusDao;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleDaoImpl;
import org.csstudio.archive.common.service.mysqlimpl.sample.IArchiveSampleDao;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Context of the MySQL service implementation.
 * Binds the Dao implementations to their interfaces.
 *
 * Replace the module for a different context (for test purposes or other rdb technologies)
 *
 * @author bknerr
 * @since Mar 24, 2011
 */
public class MySQLArchiveServiceImplModule extends AbstractModule {
    /**
     * Constructor.
     */
    public MySQLArchiveServiceImplModule() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(MySQLArchivePreferenceService.class).in(Scopes.SINGLETON);
        bind(ArchiveConnectionHandler.class).in(Scopes.SINGLETON);
        bind(PersistEngineDataManager.class).in(Scopes.SINGLETON);

        bind(IArchiveEngineStatusDao.class).to(ArchiveEngineStatusDaoImpl.class).in(Scopes.SINGLETON);
        bind(IArchiveChannelDao.class).to(ArchiveChannelDaoImpl.class).in(Scopes.SINGLETON);
        bind(IArchiveChannelGroupDao.class).to(ArchiveChannelGroupDaoImpl.class).in(Scopes.SINGLETON);
        bind(IArchiveChannelStatusDao.class).to(ArchiveChannelStatusDaoImpl.class).in(Scopes.SINGLETON);
        bind(IArchiveControlSystemDao.class).to(ArchiveControlSystemDaoImpl.class).in(Scopes.SINGLETON);
        bind(IArchiveEngineDao.class).to(ArchiveEngineDaoImpl.class).in(Scopes.SINGLETON);
        bind(IArchiveSampleDao.class).to(ArchiveSampleDaoImpl.class).in(Scopes.SINGLETON);

        bind(MysqlArchiveCreationServiceSupport.class).in(Scopes.SINGLETON);
        bind(MysqlArchiveRetrievalServiceSupport.class).in(Scopes.SINGLETON);
        bind(MysqlArchiveUpdateServiceSupport.class).in(Scopes.SINGLETON);
        bind(MysqlArchiveDeleteServiceSupport.class).in(Scopes.SINGLETON);
    }
}
