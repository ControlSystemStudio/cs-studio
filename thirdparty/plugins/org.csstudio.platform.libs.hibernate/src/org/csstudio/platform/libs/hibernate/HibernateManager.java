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
package org.csstudio.platform.libs.hibernate;


import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DDB_PASSWORD;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DDB_TIMEOUT;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DDB_USER_NAME;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.DIALECT;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.HIBERNATE_CONNECTION_URL;
import static org.csstudio.platform.libs.hibernate.preferences.PreferenceConstants.SHOW_SQL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 03.06.2009
 */
public final class HibernateManager {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateManager.class);
    
    private static final class SessionWatchDog extends Job {
        private SessionFactory _sessionFactory;
        private int _sessionUseCounter;
        private long _timeToCloseSession = (36000000 * 5);

        private SessionWatchDog(String name) {
            super(name);
            setPriority(DECORATE);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            boolean watch = true;
            Date date = new Date();
            System.out.println(date+": Starte WatchDog!");
            while (watch) {
                if (_sessionFactory == null || _sessionFactory.isClosed()) {
                    break;
                }
                if (_sessionUseCounter == 0) {
                    Date now = new Date();
                    if (now.getTime() - date.getTime() > getTimeToCloseSession()) {
                        _sessionFactory.close();
                        _sessionFactory = null;
                        System.out.println(now+": Session wurde geschlossen!!!");
                        break;
                    }

                } else {
                    date = new Date();
                    System.out.println(date+": SessionUseCounter :"+_sessionUseCounter+" to 0");
                    _sessionUseCounter = 0;
                }
                try {
                    this.getThread();
                    // Sleep 5 min.
                    Date now = new Date();
                    System.out.println(now+": IOConfig: go Sleeping");
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                }
            }
            monitor.done();
            Date now = new Date();
            System.out.println(now+": WatchDog gestoppt!");
            return Job.ASYNC_FINISH;
        }

        public void setSessionFactory(SessionFactory sessionFactory) {
            _sessionFactory = sessionFactory;

        }

        public void useSession() {
            _sessionUseCounter++;
        }

        /**
         * Set the time to close the DB Session in millisecond.
         * 
         * @param timeToCloseSession
         *            the time to close the Session.
         */
        public void setTimeToCloseSession(long timeToCloseSession) {
            _timeToCloseSession = timeToCloseSession;
        }

        public long getTimeToCloseSession() {
            return _timeToCloseSession;
        }
    }

    private static SessionFactory _sessionFactoryDevDB;
    private static int openTransactions = 0;
    private static AnnotationConfiguration _cfg;

    /**
     * The timeout in sec.
     */
    private static int _timeout = 10;
    private static org.hibernate.classic.Session _sess;
    private static Transaction _trx;
    private static SessionWatchDog _sessionWatchDog;
    private static List<Class> _classes = new ArrayList<Class>();

    /**
     * 
     * @param timeout
     *            set the DB Timeout.
     */
    public static void setTimeout(int timeout) {
        _timeout = timeout;
    }

    private HibernateManager() {
    }

    public static void setSessionFactory(SessionFactory sf) {
        synchronized (HibernateManager.class) {
            _sessionFactoryDevDB = sf;
        }
    }

    private static void initSessionFactoryDevDB() {
        if (_sessionFactoryDevDB != null && !_sessionFactoryDevDB.isClosed()) {
            return;
        }

        if (_cfg == null) {
            buildConifg();
        }
        HibernateManager.setSessionFactory(_cfg.buildSessionFactory());
    }

    private static void buildConifg() {
        new InstanceScope().getNode(Activator.PLUGIN_ID)
                .addPreferenceChangeListener(new IPreferenceChangeListener() {

                    @Override
                    public void preferenceChange(PreferenceChangeEvent event) {
                        setProperty(event.getKey(), event.getNewValue());
                        HibernateManager.setSessionFactory(_cfg.buildSessionFactory());
                    }
                });

        IPreferencesService prefs = Platform.getPreferencesService();
        String pluginId = Activator.PLUGIN_ID;
        _cfg = new AnnotationConfiguration();
        for (Class clazz : _classes) {
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
            .setProperty("hibernate.show_sql", "false")
                // prefs.getString(pluginId, SHOW_SQL, "",// null));//
            .setProperty("transaction.factory_class",
                        "org.hibernate.transaction.JDBCTransactionFactory")
            .setProperty("hibernate.cache.provider_class",
                        "org.hibernate.cache.HashtableCacheProvider")
            .setProperty("hibernate.cache.use_minimal_puts", "true")
            .setProperty("hibernate.cache.use_query_cache", "true")
            .setProperty("hibernate.cache.use_second_level_cache", "true")
            .setProperty("hibernate.hbm2ddl.auto", "update");
        setTimeout(prefs.getInt(pluginId, DDB_TIMEOUT, 90, null));
    }
    
    public static void addClasses(List<Class> classes){
        _classes.addAll(classes);
    }

    public static void removeClasses(ArrayList<Class> classes) {
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
    protected static void setProperty(String property, Object value) {
        if (property.equals(DDB_TIMEOUT)) {
            if (value instanceof Integer) {
                setTimeout((Integer) value);
            } else if (value instanceof String) {
                setTimeout(Integer.parseInt((String) value));
            }
        } else if (value instanceof String) {
            String stringValue = ((String) value).trim();

            if (property.equals(DDB_PASSWORD)) {
                _cfg.setProperty("hibernate.connection.password", stringValue);
            } else if (property.equals(DDB_USER_NAME)) {
                _cfg.setProperty("hibernate.connection.username", stringValue);
            } else if (property.equals(DIALECT)) {
                _cfg.setProperty("hibernate.dialect", stringValue);
            } else if (property.equals(HIBERNATE_CONNECTION_DRIVER_CLASS)) {
                _cfg.setProperty("hibernate.connection.driver_class", stringValue);
            } else if (property.equals(HIBERNATE_CONNECTION_URL)) {
                _cfg.setProperty("hibernate.connection.url", stringValue);
            } else if (property.equals(SHOW_SQL)) {
                _cfg.setProperty("hibernate.show_sql", stringValue);
            }
        }
    }

    /**
     * 
     * @param <T>
     *            The result Object type.
     * @param callback
     *            The Hibernate call back.
     * @return the Session resulte.
     */
    @SuppressWarnings("unchecked")
    public static <T> T doInDevDBHibernate(HibernateCallback callback) {
        initSessionFactoryDevDB();
        if (_sessionWatchDog == null) {
            _sessionWatchDog = new SessionWatchDog("Session Watch Dog");
            _sessionWatchDog.setSessionFactory(_sessionFactoryDevDB);
            _sessionWatchDog.schedule(300000);

        }
        _sessionWatchDog.useSession();

        _trx = null;
        try {

            openTransactions++;
            // if(openTransactions<2) {
            _sess = _sessionFactoryDevDB.openSession();
            // }
            LOG.debug("Open a Session: {}", openTransactions);
            // }
            LOG.debug("session is {}", _sess);
            _trx = _sess.getTransaction();
            _trx.setTimeout(_timeout);
            _trx.begin();
            Object result = callback.execute(_sess);
            _trx.commit();
            return (T) result;
        } catch (HibernateException ex) {
            if (_trx != null) {
                try {
                    _trx.rollback();
                } catch (HibernateException exRb) {
                    LOG.error("", exRb);
                    exRb.printStackTrace();
                }
            }
            LOG.error("", ex);
            ex.printStackTrace();
            throw ex;
        } finally {
            try {
                openTransactions--;
                if (_sess != null) {// && openTransactions<1) {
                    _sess.close();
                    _sess = null;
                }
            } catch (Exception exCl) {
                exCl.printStackTrace();
            }
        }
    }

}
