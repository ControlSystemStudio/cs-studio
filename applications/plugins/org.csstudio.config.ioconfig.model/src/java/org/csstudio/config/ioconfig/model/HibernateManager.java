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

import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_PASSWORD;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_TIMEOUT;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_USER_NAME;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DIALECT;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_URL;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.SHOW_SQL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.preference.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.14 $
 * @since 03.06.2009
 */
public final class HibernateManager extends Observable {
    
    private static HibernateManager _INSTANCE;
    
    /**
     * 
     * This class is a Watchdog over the Session Time. At DESY DB connection was after 24h rested: 
     * 
     * @author Rickens Helge
     * @author $Author: $
     * @since 16.12.2010
     */
    private static final class SessionWatchDog extends Job {
        private SessionFactory _sessionFactory;
        private int _sessionUseCounter;
        private final long _timeToCloseSession = (3600000 * 5);
        
        protected SessionWatchDog(@Nonnull final String name) {
            super(name);
            setPriority(DECORATE);
        }
        
        @Override
        @Nonnull
        protected IStatus run(@Nonnull IProgressMonitor monitor) {
            boolean watch = true;
            Date date = new Date();
            while (watch) {
                try {
                    this.getThread();
                    // Sleep 5 min.
                    Thread.sleep(30000);
                    // CHECKSTYLE OFF: EmptyBlock
                } catch (InterruptedException e) {
                    // Ignore Interrupt
                }
                // CHECKSTYLE ON: EmptyBlock
                if( (_sessionFactory == null) || _sessionFactory.isClosed()) {
                    break;
                }
                if(_sessionUseCounter == 0) {
                    Date now = new Date();
                    if(now.getTime() - date.getTime() > getTimeToCloseSession()) {
                        CentralLogger.getInstance().info(this, "DB Session closed by watchdog");
                        _sessionFactory.close();
                        _sessionFactory = null;
                        break;
                    }
                    
                } else {
                    date = new Date();
                    _sessionUseCounter = 0;
                }
            }
            monitor.done();
            return Status.OK_STATUS;
        }
        
        public void setSessionFactory(@Nonnull final SessionFactory sessionFactory) {
            _sessionFactory = sessionFactory;
            
        }
        
        public void useSession() {
            _sessionUseCounter++;
        }
        
        public long getTimeToCloseSession() {
            return _timeToCloseSession;
        }
        
    }
    
    private SessionFactory _sessionFactoryDevDB;
    private AnnotationConfiguration _cfg;
    
    /**
     * The timeout in sec.
     */
    private int _timeout = 10;
    private Transaction _trx;
    private final SessionWatchDog _sessionWatchDog = new SessionWatchDog("Session Watch Dog");
    private final List<Class<?>> _classes = new ArrayList<Class<?>>();
    private Session _sessionLazy;
    
    /**
     *
     * @param timeout
     *            set the DB Timeout.
     */
    public void setTimeout(final int timeout) {
        _timeout = timeout;
    }
    
    private HibernateManager() {
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
            CentralLogger.getInstance().error(HibernateManager.class.getName(), e);
        }
    }
    
    private void buildConifg() {
        new InstanceScope().getNode(IOConifgActivator.getDefault().getPluginId())
                .addPreferenceChangeListener(new IPreferenceChangeListener() {
                    
                    @Override
                    public void preferenceChange(@Nonnull final PreferenceChangeEvent event) {
                        setProperty(event.getKey(), event.getNewValue());
                        setSessionFactory(getCfg().buildSessionFactory());
                    }
                });
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String pluginId = IOConifgActivator.getDefault().getPluginId();
        _cfg = new AnnotationConfiguration();
        for (Class<?> clazz : _classes) {
            _cfg.addAnnotatedClass(clazz);
        }
        _cfg.setProperty("org.hibernate.cfg.Environment.MAX_FETCH_DEPTH", "0")
                .setProperty("hibernate.connection.driver_class",
                             prefs.getString(pluginId, HIBERNATE_CONNECTION_DRIVER_CLASS, "", null))
                .setProperty("hibernate.dialect", prefs.getString(pluginId, DIALECT, "", null))
                .setProperty("hibernate.order_updates", "false")
                .setProperty("hibernate.connection.url",
                             prefs.getString(pluginId, HIBERNATE_CONNECTION_URL, "", null))
                .setProperty("hibernate.connection.username",
                             prefs.getString(pluginId, DDB_USER_NAME, "", null))
                .setProperty("hibernate.connection.password",
                             prefs.getString(pluginId, DDB_PASSWORD, "", null))
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
                .setProperty("c3p0.timeout", "1800")
                .setProperty("c3p0.acquire_increment", "1")
                .setProperty("c3p0.idle_test_period", "100")
                // sec
                .setProperty("c3p0.max_statements", "1")
                .setProperty("hibernate.hbm2ddl.auto", "update")
//                .setProperty("hibernate.hbm2ddl.auto", "validate")
                .setProperty("hibernate.show_sql", "false");
        //                .setProperty("hibernate.format_sql", "true")
        //                .setProperty("hibernate.use_sql_comments", "true")
        //			.setProperty("hibernate.cache.use_second_level_cache", "true");
        setTimeout(prefs.getInt(pluginId, DDB_TIMEOUT, 90, null));
    }
    
    public void addClasses(@Nonnull final List<Class<?>> classes) {
        _classes.addAll(classes);
    }
    
    public void removeClasses(@Nonnull final List<Class<?>> classes) {
        _classes.removeAll(classes);
    }
    
    /**
     * Set a Hibernate Property.
     *
     * @param property
     *            the Property to set a new Value.
     * @param value
     *            the value for the Property.
     */
    protected void setProperty(@Nonnull final String property, @Nonnull final Object value) {
        if(property.equals(PreferenceConstants.DDB_TIMEOUT)) {
            if(value instanceof Integer) {
                setTimeout((Integer) value);
            } else if(value instanceof String) {
                setTimeout(Integer.parseInt((String) value));
            }
        } else if(value instanceof String) {
            setStringProperty(property, value);
        }
    }
    
    /**
     * @param property
     * @param value
     */
    private void setStringProperty(@Nonnull final String property, @Nonnull final Object value) {
        String stringValue = ((String) value).trim();
        
        if(property.equals(DDB_PASSWORD)) {
            _cfg.setProperty("hibernate.connection.password", stringValue);
        } else if(property.equals(DDB_USER_NAME)) {
            _cfg.setProperty("hibernate.connection.username", stringValue);
        } else if(property.equals(DIALECT)) {
            _cfg.setProperty("hibernate.dialect", stringValue);
        } else if(property.equals(HIBERNATE_CONNECTION_DRIVER_CLASS)) {
            _cfg.setProperty("hibernate.connection.driver_class", stringValue);
        } else if(property.equals(HIBERNATE_CONNECTION_URL)) {
            _cfg.setProperty("hibernate.connection.url", stringValue);
            
        } else if(property.equals(SHOW_SQL)) {
            _cfg.setProperty("hibernate.show_sql", stringValue);
            _cfg.setProperty("hibernate.format_sql", stringValue);
            _cfg.setProperty("hibernate.use_sql_comments", stringValue);
            
        }
    }
    
    @CheckForNull
    public <T> T doInDevDBHibernateEager(@Nonnull final HibernateCallback hibernateCallback) throws PersistenceException {
        initSessionFactoryDevDB();
        _sessionWatchDog.setSessionFactory(_sessionFactoryDevDB);
        _sessionWatchDog.schedule(30000);
        _sessionWatchDog.useSession();
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
                CentralLogger.getInstance().error(HibernateManager.class.getSimpleName(), exRb);
            }
        }
        CentralLogger.getInstance().error(HibernateManager.class.getSimpleName(), ex);
    }
    
    /**
     *
     * @param <T>
     *            The result Object type.
     * @param hibernateCallback
     *            The Hibernate call back.
     * @return the Session resulte.
     */
    @CheckForNull
    public <T> T doInDevDBHibernateLazy(@Nonnull final HibernateCallback hibernateCallback) throws PersistenceException {
        initSessionFactoryDevDB();
        _sessionWatchDog.setSessionFactory(_sessionFactoryDevDB);
        _sessionWatchDog.schedule(30000);
        _sessionWatchDog.useSession();
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
    
    public synchronized void closeSession() {
        if( (_sessionLazy != null) && _sessionLazy.isOpen()) {
            _sessionLazy.close();
            _sessionLazy = null;
        }
        if( (_sessionFactoryDevDB != null) && !_sessionFactoryDevDB.isClosed()) {
            _sessionFactoryDevDB.close();
            _sessionFactoryDevDB = null;
        }
        CentralLogger.getInstance().info(HibernateManager.class, "DB Session  Factory closed");
        
    }
    
    @Nonnull
    public static synchronized HibernateManager getInstance() {
        if(_INSTANCE == null) {
            _INSTANCE = new HibernateManager();
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
    public boolean isConnected() {
        return _sessionFactoryDevDB != null ? _sessionFactoryDevDB.isClosed() : false;
    }
    
}
