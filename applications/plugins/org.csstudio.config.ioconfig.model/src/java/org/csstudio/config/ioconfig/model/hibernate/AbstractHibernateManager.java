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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.hibernate;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NodeDBO;
import org.csstudio.config.ioconfig.model.NodeImageDBO;
import org.csstudio.config.ioconfig.model.PV2IONameMatcherModelDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.SearchNodeDBO;
import org.csstudio.config.ioconfig.model.SensorsDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.service.internal.Channel4ServicesDBO;
import org.csstudio.config.ioconfig.model.service.internal.Node4ServicesDBO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 08.07.2011
 */
public abstract class AbstractHibernateManager extends Observable implements IHibernateManager {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateTestManager.class);
    private static final Set<Class<?>> CLASSES = new HashSet<Class<?>>();

    private SessionFactory _sessionFactoryDevDB;
    private Session _sessionLazy;

    /**
     * The timeout in sec.
     */
    private int _timeout = 10;

    private Transaction _trx;

    /**
     * Constructor.
     */
    protected AbstractHibernateManager() {
        super();
//        CLASSES.add(VirtualRoot.class);
        CLASSES.add(NodeImageDBO.class);
        CLASSES.add(ChannelDBO.class);
        CLASSES.add(ChannelStructureDBO.class);
        CLASSES.add(ModuleDBO.class);
        CLASSES.add(SlaveDBO.class);
        CLASSES.add(MasterDBO.class);
        CLASSES.add(ProfibusSubnetDBO.class);
        CLASSES.add(GSDModuleDBO.class);
        CLASSES.add(IocDBO.class);
        CLASSES.add(FacilityDBO.class);
        CLASSES.add(NodeDBO.class);
        CLASSES.add(AbstractNodeSharedImpl.class);
        CLASSES.add(GSDFileDBO.class);
        CLASSES.add(ModuleChannelPrototypeDBO.class);
        CLASSES.add(DocumentDBO.class);
        CLASSES.add(SearchNodeDBO.class);
        CLASSES.add(SensorsDBO.class);
        CLASSES.add(PV2IONameMatcherModelDBO.class);
        CLASSES.add(Node4ServicesDBO.class);
        CLASSES.add(Channel4ServicesDBO.class);
    }

    protected abstract void buildConfig();

    @Override
    public final synchronized void closeSession() {
        if (_sessionLazy != null && _sessionLazy.isOpen()) {
            //            _sessionLazy.close();
            _sessionLazy.disconnect();
            _sessionLazy = null;
        }
        if (_sessionFactoryDevDB != null && !_sessionFactoryDevDB.isClosed()) {
            _sessionFactoryDevDB.close();
            _sessionFactoryDevDB = null;
        }
        LOG.info("DB Session  Factory closed");

    }

    @Override
    @CheckForNull
    public final <T> T doInDevDBHibernateEager(@Nonnull final IHibernateCallback hibernateCallback) throws PersistenceException {
        try {
            initSessionFactoryDevDB();
        } catch (final Exception e) {
            throw new PersistenceException("Can't init Hibernate Session", e);
        }
        _trx = null;
        Session sessionEager = _sessionFactoryDevDB.openSession();
        T result;
        try {
            _trx = sessionEager.getTransaction();
            _trx.setTimeout(_timeout);
            _trx.begin();
            result = execute(hibernateCallback, sessionEager);
            _trx.commit();
        } catch (final HibernateException ex) {
            notifyObservers(ex);
            tryRollback(ex);
            throw new PersistenceException(ex);
        } finally {
            if (sessionEager != null) {
                sessionEager.close();
                sessionEager = null;
            }
        }
        return result;
    }

    /**
     *
     * @param <T>
     *            The result Object type.
     * @param hibernateCallback
     *            The Hibernate call back.
     * @return the Session resulte.
     */
    @Override
    @CheckForNull
    public final <T> T doInDevDBHibernateLazy(@Nonnull final IHibernateCallback hibernateCallback) throws PersistenceException {
        initSessionFactoryDevDB();
        _trx = null;
        try {
            if (_sessionLazy == null) {
                if (_sessionFactoryDevDB == null) {
                    initSessionFactoryDevDB();
                }
                _sessionLazy = _sessionFactoryDevDB.openSession();
            }
            _trx = _sessionLazy.getTransaction();
            _trx.setTimeout(_timeout);
            _trx.begin();
            final T result = execute(hibernateCallback, _sessionLazy);
            _trx.commit();
            return result;
        } catch (final HibernateException ex) {
            tryRollback(ex);
            try {
                if (_sessionLazy != null && _sessionLazy.isOpen()) {
                    _sessionLazy.close();
                }
            } finally {
                _sessionLazy = null;
            }
            throw new PersistenceException(ex);
        }
    }

    @CheckForNull
    final <T> T execute(@Nonnull final IHibernateCallback callback, @Nonnull final Session sess) {
        return callback.execute(sess);
    }

    @Nonnull
    abstract AnnotationConfiguration getCfg();

    private void initSessionFactoryDevDB() {
        if (_sessionFactoryDevDB != null && !_sessionFactoryDevDB.isClosed()) {
            return;
        }
        buildConfig();
        try {
            final SessionFactory buildSessionFactory = getCfg().buildSessionFactory();
            setSessionFactory(buildSessionFactory);
            setChanged();
            notifyObservers();
        } catch (final HibernateException e) {
            LOG.error("Can't init device database:",e);
            throw e;
        }
    }

    /**
     * @return
     */
    @Override
    public final boolean isConnected() {
        return _sessionFactoryDevDB != null ? _sessionFactoryDevDB.isClosed() : false;
    }

    public final void setSessionFactory(@Nullable final SessionFactory sf) {
        synchronized (HibernateManager.class) {
            _sessionFactoryDevDB = sf;
        }
    }

    /**
     *
     * @param timeout
     *            set the DB Timeout.
     */
    public final void setTimeout(final int timeout) {
        _timeout = timeout;
    }

    /**
     * @param ex
     * @throws PersistenceException
     */
    final void tryRollback(@Nonnull final HibernateException ex) {
        notifyObservers(ex);
        if (_trx != null) {
            try {
                _trx.rollback();
            } catch (final HibernateException exRb) {
                LOG.error("Can't rollback", exRb);
            }
        }
        LOG.error("Rollback! Exception was thrown: {}", ex);
    }

    @Nonnull
    public static Set<Class<?>> getClasses() {
        return CLASSES;
    }
}
