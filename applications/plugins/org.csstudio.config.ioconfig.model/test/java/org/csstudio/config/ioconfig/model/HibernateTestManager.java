/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.model;

import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Hibernate Manager for jUnit tests!
 *
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.14 $
 * @since 03.06.2009
 */
public final class HibernateTestManager extends Observable implements IHibernateManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(HibernateTestManager.class);
    
    private static HibernateTestManager _INSTANCE;
    
    private SessionFactory _sessionFactoryDevDB;
    private AnnotationConfiguration _cfg;
    
    /**
     * The timeout in sec.
     */
    private int _timeout = 10;
    private Transaction _trx;
    private final Set<Class<?>> _classes = new HashSet<Class<?>>();
    private Session _sessionLazy;
    
    /**
     *
     * @param timeout
     *            set the DB Timeout.
     */
    public void setTimeout(final int timeout) {
        _timeout = timeout;
    }
    
    HibernateTestManager() {
        // Default constructor
    }
    
    public void setSessionFactory(@Nonnull final SessionFactory sf) {
        synchronized (HibernateManager.class) {
            _sessionFactoryDevDB = sf;
        }
    }
    
    private void initSessionFactoryDevDB() {
        if( (_sessionFactoryDevDB != null) && !_sessionFactoryDevDB.isClosed()) {
            return;
        }
        buildConifg();
        try {
            SessionFactory buildSessionFactory = _cfg.buildSessionFactory();
            setSessionFactory(buildSessionFactory);
            notifyObservers();
        } catch (HibernateException e) {
            LOG.error("Can't build DB Session", e);
        }
    }
    
    private void buildConifg() {
        _classes.add(NodeImageDBO.class);
        _classes.add(ChannelDBO.class);
        _classes.add(ChannelStructureDBO.class);
        _classes.add(ModuleDBO.class);
        _classes.add(SlaveDBO.class);
        _classes.add(MasterDBO.class);
        _classes.add(ProfibusSubnetDBO.class);
        _classes.add(GSDModuleDBO.class);
        _classes.add(IocDBO.class);
        _classes.add(FacilityDBO.class);
        _classes.add(AbstractNodeDBO.class);
        _classes.add(GSDFileDBO.class);
        _classes.add(ModuleChannelPrototypeDBO.class);
        _classes.add(DocumentDBO.class);
        _classes.add(SearchNodeDBO.class);
        _classes.add(SensorsDBO.class);
        _classes.add(PV2IONameMatcherModelDBO.class);
        _cfg = new AnnotationConfiguration();
        for (Class<?> clazz : _classes) {
            _cfg.addAnnotatedClass(clazz);
        }
        _cfg.setProperty("org.hibernate.cfg.Environment.MAX_FETCH_DEPTH", "0")
                .setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
                .setProperty("hibernate.order_updates", "false")
                .setProperty("hibernate.connection.url",
                             "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA = (SERVER = DEDICATED)(SERVICE_NAME = desy_db.desy.de)(FAILOVER_MODE = (TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))")
                .setProperty("hibernate.connection.username", "KRYKMANT")
                .setProperty("hibernate.connection.password", "KRYKMANT")
                .setProperty("transaction.factory_class",
                             "org.hibernate.transaction.JDBCTransactionFactory")
                .setProperty("hibernate.cache.provider_class",
                             "org.hibernate.cache.HashtableCacheProvider")
                .setProperty("hibernate.cache.use_minimal_puts", "true")
                .setProperty("hibernate.cache.use_query_cache", "true")
                // connection Pool
                .setProperty("hibernate.connection.provider_class",
                             "org.hibernate.connection.C3P0ConnectionProvider")
                .setProperty("c3p0.min_size", "1").setProperty("c3p0.max_size", "3")
                .setProperty("c3p0.timeout", "1800").setProperty("c3p0.acquire_increment", "1")
                .setProperty("c3p0.idle_test_period", "100")
                // sec
                .setProperty("c3p0.max_statements", "1")
                //                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "false");
        //                .setProperty("hibernate.format_sql", "true")
        //                .setProperty("hibernate.use_sql_comments", "true")
        //	              .setProperty("hibernate.cache.use_second_level_cache", "true");
    }
    
    public void addClasses(@Nonnull final List<Class<?>> classes) {
        _classes.addAll(classes);
    }
    
    public void removeClasses(@Nonnull final List<Class<?>> classes) {
        _classes.removeAll(classes);
    }
    
    @Override
    @CheckForNull
    public <T> T doInDevDBHibernateEager(@Nonnull final HibernateCallback hibernateCallback) throws PersistenceException {
        initSessionFactoryDevDB();
        _trx = null;
        Session sessionEager = _sessionFactoryDevDB.openSession();
        T result;
        try {
            _trx = sessionEager.getTransaction();
            _trx.setTimeout(_timeout);
            _trx.begin();
            result = execute(hibernateCallback, sessionEager);
            _trx.commit();
        } catch (HibernateException ex) {
            notifyObservers(ex);
            tryRollback(ex);
            throw new PersistenceException(ex);
        } finally {
            if(sessionEager != null) {
                sessionEager.close();
                sessionEager = null;
            }
        }
        return result;
    }
    
    /**
     * @param ex
     * @throws PersistenceException
     */
    private void tryRollback(@Nonnull HibernateException ex) throws PersistenceException {
        notifyObservers(ex);
        if(_trx != null) {
            try {
                _trx.rollback();
            } catch (HibernateException exRb) {
                LOG.error("Can't rollback", exRb);
            }
        }
        LOG.error("Rollback! Exception was thrown: {}", ex);
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
    public <T> T doInDevDBHibernateLazy(@Nonnull final HibernateCallback hibernateCallback) throws PersistenceException {
        initSessionFactoryDevDB();
        _trx = null;
        if(_sessionLazy == null) {
            _sessionLazy = _sessionFactoryDevDB.openSession();
        }
        try {
            _trx = _sessionLazy.getTransaction();
            _trx.setTimeout(_timeout);
            _trx.begin();
            T result = execute(hibernateCallback, _sessionLazy);
            _trx.commit();
            return result;
        } catch (HibernateException ex) {
            tryRollback(ex);
            try {
                if(_sessionLazy != null && _sessionLazy.isOpen()) {
                    _sessionLazy.close();
                }
            } finally {
                _sessionLazy = null;
            }
            throw new PersistenceException(ex);
        }
    }
    
    @CheckForNull
    private <T> T execute(@Nonnull final HibernateCallback callback, @Nonnull final Session sess) {
        return callback.execute(sess);
    }
    
    @Override
    public synchronized void closeSession() {
        if( (_sessionLazy != null) && _sessionLazy.isOpen()) {
            _sessionLazy.close();
            _sessionLazy = null;
        }
        if( (_sessionFactoryDevDB != null) && !_sessionFactoryDevDB.isClosed()) {
            _sessionFactoryDevDB.close();
            _sessionFactoryDevDB = null;
        }
        LOG.info("DB Session  Factory closed");
        
    }
    
    @Nonnull
    public static synchronized HibernateTestManager getInstance() {
        if(_INSTANCE == null) {
            _INSTANCE = new HibernateTestManager();
        }
        return _INSTANCE;
    }
    
    @Nonnull
    protected AnnotationConfiguration getCfg() {
        return _cfg;
    }
    
    /**
     * @return
     */
    @Override
    public boolean isConnected() {
        return _sessionFactoryDevDB != null ? _sessionFactoryDevDB.isClosed() : false;
    }
    
}
