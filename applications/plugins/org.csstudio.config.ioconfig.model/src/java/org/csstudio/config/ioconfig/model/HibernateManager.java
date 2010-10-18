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

import org.csstudio.config.ioconfig.model.preference.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
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
public final class HibernateManager {

	private static final class SessionWatchDog extends Job {
		private SessionFactory _sessionFactory;
		private int _sessionUseCounter;
		private final long _timeToCloseSession = (3600000 * 5);

		private SessionWatchDog(final String name) {
			super(name);
			setPriority(DECORATE);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			boolean watch = true;
			Date date = new Date();
			while (watch) {
			    try {
			        this.getThread();
			        // Sleep 5 min.
			        Thread.sleep(30000);
			    } catch (InterruptedException e) {
			    }
				if ((_sessionFactory == null) || _sessionFactory.isClosed()) {
					break;
				}
				if (_sessionUseCounter == 0) {
					Date now = new Date();
					if (now.getTime() - date.getTime() > getTimeToCloseSession()) {
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
			monitor = null;

			return Status.OK_STATUS;
		}

		public void setSessionFactory(final SessionFactory sessionFactory) {
			_sessionFactory = sessionFactory;

		}

		public void useSession() {
			_sessionUseCounter++;
		}

		public long getTimeToCloseSession() {
			return _timeToCloseSession;
		}
	}

	private static SessionFactory _SESSION_FACTORY_DEV_DB;
	private static AnnotationConfiguration _CFG;

	/**
	 * The timeout in sec.
	 */
	private static int _TIMEOUT = 10;
	private static Session _SESSION;
	private static Transaction _TRX;
	private static SessionWatchDog _SESSION_WATCH_DOG;
    private static List<Class<?>> _CLASSES = new ArrayList<Class<?>>();

	/**
	 *
	 * @param timeout
	 *            set the DB Timeout.
	 */
	public static void setTimeout(final int timeout) {
		_TIMEOUT = timeout;
	}

	private HibernateManager() {
	}

	public static void setSessionFactory(final SessionFactory sf) {
		synchronized (HibernateManager.class) {
			_SESSION_FACTORY_DEV_DB = sf;
		}
	}

	private static void initSessionFactoryDevDB() {
		if ((_SESSION_FACTORY_DEV_DB != null) && !_SESSION_FACTORY_DEV_DB.isClosed()) {
			return;
		}
		buildConifg();
		try {
		    HibernateManager.setSessionFactory(_CFG.buildSessionFactory());
		} catch (HibernateException e) {
		    CentralLogger.getInstance().error(HibernateManager.class.getName(), e);
        }
	}

	private static void buildConifg() {
		new InstanceScope().getNode(IOConifgActivator.getDefault().getPluginId())
				.addPreferenceChangeListener(new IPreferenceChangeListener() {

					@Override
					public void preferenceChange(final PreferenceChangeEvent event) {
						setProperty(event.getKey(), event.getNewValue());
						HibernateManager.setSessionFactory(_CFG
								.buildSessionFactory());
					}
				});

		IPreferencesService prefs = Platform.getPreferencesService();
		String pluginId = IOConifgActivator.getDefault().getPluginId();
        _CFG = new AnnotationConfiguration();
        for (Class<?> clazz : _CLASSES) {
            _CFG.addAnnotatedClass(clazz);
        }
        _CFG.setProperty("org.hibernate.cfg.Environment.MAX_FETCH_DEPTH", "0")
            .setProperty("hibernate.connection.driver_class",
                    prefs.getString(pluginId, HIBERNATE_CONNECTION_DRIVER_CLASS, "", null))
            .setProperty("hibernate.dialect", prefs.getString(pluginId, DIALECT, "", null))
			.setProperty("hibernate.order_updates", "false")
			.setProperty("hibernate.connection.url",
					prefs.getString(pluginId, HIBERNATE_CONNECTION_URL, "",null))
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
			.setProperty("c3p0.min_size", "1")
			.setProperty("c3p0.max_size", "3")
			.setProperty("c3p0.timeout", "1800")
			.setProperty("c3p0.acquire_increment", "1")
			.setProperty("c3p0.idel_test_period", "100") // sec
			.setProperty("c3p0.max_statements", "1")
				.setProperty("hibernate.show_sql", "false");
//                .setProperty("hibernate.format_sql", "true")
//                .setProperty("hibernate.use_sql_comments", "true")
//			.setProperty("hibernate.cache.use_second_level_cache", "true");
		// .setProperty("hibernate.hbm2ddl.auto", "update");
        setTimeout(prefs.getInt(pluginId, DDB_TIMEOUT, 90, null));
	}

    public static void addClasses(final List<Class<?>> classes){
        _CLASSES.addAll(classes);
    }

    public static void removeClasses(final List<Class<?>> classes) {
        _CLASSES.removeAll(classes);
    }

	/**
	 * Set a Hibernate Property.
	 *
	 * @param property
	 *            the Property to set a new Value.
	 * @param value
	 *            the value for the Property.
	 */
	protected static void setProperty(final String property, final Object value) {
		if (property.equals(PreferenceConstants.DDB_TIMEOUT)) {
			if (value instanceof Integer) {
				setTimeout((Integer) value);
			} else if (value instanceof String) {
				setTimeout(Integer.parseInt((String) value));
			}
		} else if (value instanceof String) {
			String stringValue = ((String) value).trim();

			if (property.equals(DDB_PASSWORD)) {
				_CFG.setProperty("hibernate.connection.password", stringValue);
			} else if (property.equals(DDB_USER_NAME)) {
				_CFG.setProperty("hibernate.connection.username", stringValue);
			} else if (property.equals(DIALECT)) {
				_CFG.setProperty("hibernate.dialect", stringValue);
			} else if (property.equals(HIBERNATE_CONNECTION_DRIVER_CLASS)) {
				_CFG.setProperty("hibernate.connection.driver_class",
						stringValue);
			} else if (property.equals(HIBERNATE_CONNECTION_URL)) {
				_CFG.setProperty("hibernate.connection.url", stringValue);

			} else if (property.equals(SHOW_SQL)) {
				_CFG.setProperty("hibernate.show_sql", stringValue);
				_CFG.setProperty("hibernate.format_sql", stringValue);
                _CFG.setProperty("hibernate.use_sql_comments", stringValue);

			}
		}
	}

	/**
	 *
	 * @param <T>
	 *            The result Object type.
	 * @param hibernateCallback
	 *            The Hibernate call back.
	 * @return the Session resulte.
	 */
	public static <T> T doInDevDBHibernate(final HibernateCallback hibernateCallback) {

		if ((_SESSION == null) ||!_SESSION.isConnected() || !_SESSION.isOpen()) {
		    if (_SESSION_WATCH_DOG == null) {
	            _SESSION_WATCH_DOG = new SessionWatchDog("Session Watch Dog");
	            _SESSION_WATCH_DOG.setSystem(true);
	        }
		    initSessionFactoryDevDB();
			_SESSION = _SESSION_FACTORY_DEV_DB.openSession();
		}
		_SESSION_WATCH_DOG.setSessionFactory(_SESSION_FACTORY_DEV_DB);
        _SESSION_WATCH_DOG.schedule(30000);
        _SESSION_WATCH_DOG.useSession();
		_TRX = null;
		try {
			CentralLogger.getInstance().debug(
					HibernateManager.class.getSimpleName(),
					"session is " + _SESSION);
			_TRX = _SESSION.getTransaction();
			_TRX.setTimeout(_TIMEOUT);
			_TRX.begin();
			T result = execute( hibernateCallback, _SESSION);
			_TRX.commit();
			return result;
		} catch (HibernateException ex) {
			if (_TRX != null) {
				try {
					_TRX.rollback();
				} catch (HibernateException exRb) {
					CentralLogger.getInstance().error(
							HibernateManager.class.getSimpleName(), exRb);
				}
			}
			CentralLogger.getInstance().error(
					HibernateManager.class.getSimpleName(), ex);
			throw ex;
		}
	}

	@SuppressWarnings("unchecked")
    private static <T> T execute(final HibernateCallback callback, final Session sess) {
        return (T) callback.execute(_SESSION);
    }

    public static void closeSession() {
	    if((_SESSION!=null)&&_SESSION.isOpen()) {
	        _SESSION.close();
	        _SESSION=null;
	    }
	    if((_SESSION_FACTORY_DEV_DB!=null)&&!_SESSION_FACTORY_DEV_DB.isClosed()) {
	        _SESSION_FACTORY_DEV_DB.close();
	        _SESSION_FACTORY_DEV_DB=null;
	    }
	    if(_SESSION_WATCH_DOG!=null) {
	        _SESSION_WATCH_DOG.cancel();
	        _SESSION_WATCH_DOG=null;
	    }
	    CentralLogger.getInstance().info(HibernateManager.class, "DB Session closed");

    }

}
