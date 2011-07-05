
package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.DatabaseType;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbEmailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenEmailBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenEmailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenSMSBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenSMSFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenVMailBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbGruppenVMailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbSMSFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbVoiceMailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.TopicFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterAction2FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.JunctorConditionForFilterTreeConditionJoinDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationCondForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVarFiltCondDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StrgArFiltCondCompValDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

public class ConfigurationServiceFactoryImpl implements
		ConfigurationServiceFactory {

	private class ConnectionData {
		private final String _connectionDriver;
		private final String _connectionURL;
		private final String _dialect;
		private final String _username;
		private final String _password;
		private final DatabaseType _databaseType;

		public ConnectionData(final String connectionDriver,
				final String connectionURL, final String dialect,
				final String username, final String password,
				final DatabaseType databaseType) {
			super();
			this._connectionDriver = connectionDriver;
			this._connectionURL = connectionURL;
			this._dialect = dialect;
			this._username = username;
			this._password = password;
			this._databaseType = databaseType;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ConnectionData)) {
				return false;
			}
			final ConnectionData other = (ConnectionData) obj;
			if (this._connectionDriver == null) {
				if (other._connectionDriver != null) {
					return false;
				}
			} else if (!this._connectionDriver.equals(other._connectionDriver)) {
				return false;
			}
			if (this._connectionURL == null) {
				if (other._connectionURL != null) {
					return false;
				}
			} else if (!this._connectionURL.equals(other._connectionURL)) {
				return false;
			}
			if (this._databaseType == null) {
				if (other._databaseType != null) {
					return false;
				}
			} else if (!this._databaseType.equals(other._databaseType)) {
				return false;
			}
			if (this._dialect == null) {
				if (other._dialect != null) {
					return false;
				}
			} else if (!this._dialect.equals(other._dialect)) {
				return false;
			}
			if (this._password == null) {
				if (other._password != null) {
					return false;
				}
			} else if (!this._password.equals(other._password)) {
				return false;
			}
			if (this._username == null) {
				if (other._username != null) {
					return false;
				}
			} else if (!this._username.equals(other._username)) {
				return false;
			}
			return true;
		}

		public String getConnectionDriver() {
			return this._connectionDriver;
		}

		public String getConnectionURL() {
			return this._connectionURL;
		}

		public DatabaseType getDatabaseType() {
			return this._databaseType;
		}

		public String getDialect() {
			return this._dialect;
		}

		public String getPassword() {
			return this._password;
		}

		public String getUsername() {
			return this._username;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((this._connectionDriver == null) ? 0
							: this._connectionDriver.hashCode());
			result = prime
					* result
					+ ((this._connectionURL == null) ? 0 : this._connectionURL
							.hashCode());
			result = prime
					* result
					+ ((this._databaseType == null) ? 0 : this._databaseType
							.hashCode());
			result = prime * result
					+ ((this._dialect == null) ? 0 : this._dialect.hashCode());
			result = prime * result
					+ ((this._password == null) ? 0 : this._password.hashCode());
			result = prime * result
					+ ((this._username == null) ? 0 : this._username.hashCode());
			return result;
		}

	}

	private final Map<ConnectionData, LocalStoreConfigurationService> services = new HashMap<ConnectionData, LocalStoreConfigurationService>();
	private final List<SessionFactory> sessionFactoryList = new LinkedList<SessionFactory>();

	// private List<Session> sessionList = new LinkedList<Session>();
	private final org.csstudio.nams.service.logging.declaration.Logger _logger;

	public ConfigurationServiceFactoryImpl(
			final org.csstudio.nams.service.logging.declaration.Logger logger) {
		this._logger = logger;
	}

	// TODO Add custom connection provider
	// TODO Add property for auto create: "hibernate.hbm2ddl.auto" =
	// "create-drop" für HSQL
	// ADD Configuration cfg = new Configuration().configure();
	// SchemaExport schemaExport = new SchemaExport(cfg);
	// schemaExport.create(false, true);
	// OR
	// Configuration cfg = new Configuration().configure();
	// SchemaUpdate schemaUpdate = new SchemaUpdate(cfg);
	// schemaUpdate.execute(false);
	// CHECK
	// Configuration cfg = new Configuration().configure();
	// new SchemaValidator(cfg).validate();
	//
	// copied from: http://www.manning.com/bauer2/chapter2.pdf
	// SEE PAGE 75 !!!!!!!!!!
	// Download EM http://www.hibernate.org/30.html

	public void closeSessions() {
		// for (Session session : sessionList) {
		// session.close();
		// }
		// sessionList.clear();
		for (final SessionFactory sessionFactory : this.sessionFactoryList) {
			sessionFactory.close();
		}
		this.sessionFactoryList.clear();
		this.services.clear();
	}

	@Override
    public LocalStoreConfigurationService getConfigurationService(
			final String connectionURL, final DatabaseType dbType,
			final String username, final String password) {
		Contract.requireNotNull("dbType", dbType);

		Contract.requireNotNull("connectionURL", connectionURL);
		Contract.require(connectionURL.length() > 0,
				"connectionURL.length() > 0");

		Contract.requireNotNull("username", username);
		Contract.require(username.length() > 0, "username.length() > 0");

		Contract.requireNotNull("password", password);
		// Passwords of length 0 ok for development databaseses!
		// Contract.require(password.length() > 0, "password.length() > 0");

		final ConnectionData connectionData = new ConnectionData(dbType
				.getDriverName(), connectionURL, dbType.getHibernateDialect()
				.getName(), username, password, dbType);
		LocalStoreConfigurationService service = this.services
				.get(connectionData);

		if (service == null) {
			final SessionFactory sessionFactory = this
					.createSessionFactory(connectionData);
			// Session session = sessionFactory.openSession();
			// session.setFlushMode(FlushMode.COMMIT);
			service = new LocalStoreConfigurationServiceImpl(sessionFactory,
					this._logger);
			this.sessionFactoryList.add(sessionFactory);
			// sessionList.add(session);
			this.services.put(connectionData, service);
		}

		return service;
	}

	private SessionFactory createSessionFactory(
			final ConnectionData connectionData) {
		final AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration
				.addAnnotatedClass(ReplicationStateDTO.class)
				.addAnnotatedClass(AlarmbearbeiterDTO.class)
				.addAnnotatedClass(AlarmbearbeiterGruppenDTO.class)
				.addAnnotatedClass(FilterConditionDTO.class)
				.addAnnotatedClass(FilterConditionTypeDTO.class)
				.addAnnotatedClass(TopicDTO.class)
				.addAnnotatedClass(FilterConditionsToFilterDTO.class)

				.addAnnotatedClass(JunctorConditionDTO.class)
				.addAnnotatedClass(StringFilterConditionDTO.class)
				.addAnnotatedClass(StringArFilterConditionDTO.class)

				.addAnnotatedClass(
						StrgArFiltCondCompValDTO.class)
				// TODO Mapping in Configuration!!!
				.addAnnotatedClass(TimeBasedFilterConditionDTO.class)
				.addAnnotatedClass(ProcessVarFiltCondDTO.class)
				.addAnnotatedClass(FilterDTO.class).addAnnotatedClass(
						HistoryDTO.class).addAnnotatedClass(RubrikDTO.class)
				.addAnnotatedClass(NegationCondForFilterTreeDTO.class)
				.addAnnotatedClass(JunctorCondForFilterTreeDTO.class)
				.addAnnotatedClass(
						JunctorConditionForFilterTreeConditionJoinDTO.class)
				.addAnnotatedClass(User2UserGroupDTO.class).addAnnotatedClass(
						NegationCondForFilterTreeDTO.class)

				.addAnnotatedClass(FilterActionDTO.class).addAnnotatedClass(
						TopicFilterActionDTO.class).addAnnotatedClass(
						AlarmbEmailFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbGruppenEmailBestFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbGruppenEmailFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbGruppenSMSBestFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbGruppenSMSFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbGruppenVMailBestFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbGruppenVMailFilterActionDTO.class)
				.addAnnotatedClass(AlarmbSMSFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbVoiceMailFilterActionDTO.class)
				.addAnnotatedClass(FilterAction2FilterDTO.class)

				.addAnnotatedClass(DefaultFilterTextDTO.class)

				.setProperty("hibernate.connection.driver_class",
						connectionData.getConnectionDriver()).setProperty(
						"hibernate.connection.url",
						connectionData.getConnectionURL()).setProperty(
						"hibernate.dialect", connectionData.getDialect())
				.setProperty("hibernate.connection.username",
						connectionData.getUsername()).setProperty(
						"hibernate.connection.password",
						connectionData.getPassword())

				.setProperty("hibernate.cache.provider_class",
						"org.hibernate.cache.NoCacheProvider").setProperty(
						"hibernate.cache.use_minimal_puts", "false")
				.setProperty("hibernate.cache.use_query_cache", "false")
				.setProperty("hibernate.cache.use_second_level_cache", "false")

				.setProperty("hibernate.connection.pool_size", "1")
				.setProperty("current_session_context_class", "thread")
				.setProperty("show_sql", "true").setProperty("hbm2ddl.auto",
						"update").setProperty("hibernate.mapping.precedence",
						"class");

		if (connectionData.getDatabaseType().equals(
				DatabaseType.HSQL_1_8_0_FOR_TEST)) {
			try {
				Class.forName(DatabaseType.HSQL_1_8_0_FOR_TEST.getDriverName())
						.newInstance();
				final SchemaUpdate schemaUpdate = new SchemaUpdate(
						configuration);
				schemaUpdate.execute(false, true);
			} catch (final Throwable t) {
				_logger.logErrorMessage(this, "Failed to load HSQL-Driver for testing purposes", t);
			}
		}

		// TODO in die config auslagern
		Logger.getLogger("org.hibernate").setLevel(Level.WARN);

		return configuration.buildSessionFactory();
	}
}
