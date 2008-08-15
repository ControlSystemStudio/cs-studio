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
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterEmailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterGruppenEmailBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterGruppenEmailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterGruppenSMSBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterGruppenSMSFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterGruppenVMailBestFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterGruppenVMailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterSMSFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.AlarmbearbeiterVoiceMailFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions.TopicFilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterAction2FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.JunctorConditionForFilterTreeConditionJoinDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

public class ConfigurationServiceFactoryImpl implements
		ConfigurationServiceFactory {

	private class ConnectionData {
		private final String connectionDriver;
		private final String connectionURL;
		private final String dialect;
		private final String username;
		private final String password;
		private final DatabaseType databaseType;

		public ConnectionData(final String connectionDriver,
				final String connectionURL, final String dialect,
				final String username, final String password,
				final DatabaseType databaseType) {
			super();
			this.connectionDriver = connectionDriver;
			this.connectionURL = connectionURL;
			this.dialect = dialect;
			this.username = username;
			this.password = password;
			this.databaseType = databaseType;
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
			if (this.connectionDriver == null) {
				if (other.connectionDriver != null) {
					return false;
				}
			} else if (!this.connectionDriver.equals(other.connectionDriver)) {
				return false;
			}
			if (this.connectionURL == null) {
				if (other.connectionURL != null) {
					return false;
				}
			} else if (!this.connectionURL.equals(other.connectionURL)) {
				return false;
			}
			if (this.databaseType == null) {
				if (other.databaseType != null) {
					return false;
				}
			} else if (!this.databaseType.equals(other.databaseType)) {
				return false;
			}
			if (this.dialect == null) {
				if (other.dialect != null) {
					return false;
				}
			} else if (!this.dialect.equals(other.dialect)) {
				return false;
			}
			if (this.password == null) {
				if (other.password != null) {
					return false;
				}
			} else if (!this.password.equals(other.password)) {
				return false;
			}
			if (this.username == null) {
				if (other.username != null) {
					return false;
				}
			} else if (!this.username.equals(other.username)) {
				return false;
			}
			return true;
		}

		public String getConnectionDriver() {
			return this.connectionDriver;
		}

		public String getConnectionURL() {
			return this.connectionURL;
		}

		public DatabaseType getDatabaseType() {
			return this.databaseType;
		}

		public String getDialect() {
			return this.dialect;
		}

		public String getPassword() {
			return this.password;
		}

		public String getUsername() {
			return this.username;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((this.connectionDriver == null) ? 0
							: this.connectionDriver.hashCode());
			result = prime
					* result
					+ ((this.connectionURL == null) ? 0 : this.connectionURL
							.hashCode());
			result = prime
					* result
					+ ((this.databaseType == null) ? 0 : this.databaseType
							.hashCode());
			result = prime * result
					+ ((this.dialect == null) ? 0 : this.dialect.hashCode());
			result = prime * result
					+ ((this.password == null) ? 0 : this.password.hashCode());
			result = prime * result
					+ ((this.username == null) ? 0 : this.username.hashCode());
			return result;
		}

	}

	private final Map<ConnectionData, LocalStoreConfigurationService> services = new HashMap<ConnectionData, LocalStoreConfigurationService>();
	private final List<SessionFactory> sessionFactoryList = new LinkedList<SessionFactory>();

	// private List<Session> sessionList = new LinkedList<Session>();
	private final org.csstudio.nams.service.logging.declaration.Logger logger;

	public ConfigurationServiceFactoryImpl(
			final org.csstudio.nams.service.logging.declaration.Logger logger) {
		this.logger = logger;
	}

	// TODO Add curstom connection provider
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
					this.logger);
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
				.addAnnotatedClass(StringArrayFilterConditionDTO.class)

				.addAnnotatedClass(
						StringArrayFilterConditionCompareValuesDTO.class)
				// TODO Mapping in Configuration!!!
				.addAnnotatedClass(TimeBasedFilterConditionDTO.class)
				.addAnnotatedClass(ProcessVariableFilterConditionDTO.class)
				.addAnnotatedClass(FilterDTO.class).addAnnotatedClass(
						HistoryDTO.class).addAnnotatedClass(RubrikDTO.class)
				.addAnnotatedClass(NegationConditionForFilterTreeDTO.class)
				.addAnnotatedClass(JunctorConditionForFilterTreeDTO.class)
				.addAnnotatedClass(
						JunctorConditionForFilterTreeConditionJoinDTO.class)
				.addAnnotatedClass(User2UserGroupDTO.class).addAnnotatedClass(
						NegationConditionForFilterTreeDTO.class)

				.addAnnotatedClass(FilterActionDTO.class).addAnnotatedClass(
						TopicFilterActionDTO.class).addAnnotatedClass(
						AlarmbearbeiterEmailFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbearbeiterGruppenEmailBestFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbearbeiterGruppenEmailFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbearbeiterGruppenSMSBestFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbearbeiterGruppenSMSFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbearbeiterGruppenVMailBestFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbearbeiterGruppenVMailFilterActionDTO.class)
				.addAnnotatedClass(AlarmbearbeiterSMSFilterActionDTO.class)
				.addAnnotatedClass(
						AlarmbearbeiterVoiceMailFilterActionDTO.class)
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
				logger.logErrorMessage(this, "Failed to load HSQL-Driver for testing purposes", t);
			}
		}

		// TODO in die config auslagern
		Logger.getLogger("org.hibernate").setLevel(Level.WARN);

		return configuration.buildSessionFactory();
	}
}
