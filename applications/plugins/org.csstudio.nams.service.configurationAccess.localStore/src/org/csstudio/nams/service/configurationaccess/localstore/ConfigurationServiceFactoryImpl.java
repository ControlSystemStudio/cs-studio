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
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.JunctorConditionForFilterTreeConditionJoinDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;

public class ConfigurationServiceFactoryImpl implements
		ConfigurationServiceFactory {

	private Map<ConnectionData, LocalStoreConfigurationService> services = new HashMap<ConnectionData, LocalStoreConfigurationService>();
	private List<SessionFactory> sessionFactoryList = new LinkedList<SessionFactory>();
	private List<Session> sessionList = new LinkedList<Session>();
	private final org.csstudio.nams.service.logging.declaration.Logger logger;
	
	public ConfigurationServiceFactoryImpl(
			org.csstudio.nams.service.logging.declaration.Logger logger) {
				this.logger = logger;
	}

	public LocalStoreConfigurationService getConfigurationService(String connectionURL, DatabaseType dbType, String username, String password)
	{
		Contract.requireNotNull("dbType", dbType);
		
		Contract.requireNotNull("connectionURL", connectionURL);
		Contract.require(connectionURL.length() > 0, "connectionURL.length() > 0");
		
		Contract.requireNotNull("username", username);
		Contract.require(username.length() > 0, "username.length() > 0");
		
		Contract.requireNotNull("password", password);
		//Passwords of length 0 ok for development databaseses! Contract.require(password.length() > 0, "password.length() > 0");
		
		/*-if( dbType == DatabaseType.HSQL_1_8_0 ) {
			try {
				DriverManager.registerDriver(new jdbcDriver());
			} catch (Throwable t) {
				// TODO logging....
				System.out
						.println("ConfigurationServiceFactoryImpl.getConfigurationService() " + t);
			}
		}*/
		
		ConnectionData connectionData = new ConnectionData(dbType.getDriverName(), connectionURL, dbType.getHibernateDialect().getName(), username, password);
		LocalStoreConfigurationService service = services.get(connectionData);
		
		if (service == null) {
			SessionFactory sessionFactory = createSessionFactory(connectionData);
			Session session = sessionFactory.openSession();
			service = new LocalStoreConfigurationServiceImpl(session, logger);
			sessionFactoryList.add(sessionFactory);
			sessionList.add(session);
			services.put(connectionData, service);
		}
		
		return service;
	}
	
	private SessionFactory createSessionFactory(ConnectionData connectionData) {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
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
				
				.addAnnotatedClass(StringArrayFilterConditionCompareValuesDTO.class) // TODO Mapping in Configuration!!!
				.addAnnotatedClass(TimeBasedFilterConditionDTO.class)
				.addAnnotatedClass(ProcessVariableFilterConditionDTO.class)
				.addAnnotatedClass(FilterDTO.class)
				.addAnnotatedClass(HistoryDTO.class)
				.addAnnotatedClass(RubrikDTO.class)
				.addAnnotatedClass(JunctorConditionForFilterTreeDTO.class)
				.addAnnotatedClass(JunctorConditionForFilterTreeConditionJoinDTO.class)
				.addAnnotatedClass(User2UserGroupDTO.class)
				
				.setProperty("hibernate.connection.driver_class", connectionData.getConnectionDriver())
				.setProperty("hibernate.connection.url", connectionData.getConnectionURL())
				.setProperty("hibernate.dialect", connectionData.getDialect())
				.setProperty("hibernate.connection.username", connectionData.getUsername())
				.setProperty("hibernate.connection.password", connectionData.getPassword())

				.setProperty("hibernate.connection.pool_size", "1")
				.setProperty("current_session_context_class", "thread")
				.setProperty("cache.provider_class", "org.hibernate.cache.NoCacheProvider")
				.setProperty("show_sql", "true")
				.setProperty("hbm2ddl.auto", "update") 
				.setProperty("hibernate.mapping.precedence", "class");

		//TODO in die config auslagern
		Logger.getLogger("org.hibernate").setLevel(Level.WARN);
		
		return configuration.buildSessionFactory();
	}
	
	public void closeSessions() {
		for (Session session : sessionList) {
			session.close();
		}
		sessionList.clear();
		for (SessionFactory sessionFactory : sessionFactoryList) {
			sessionFactory.close();
		}
		sessionFactoryList.clear();
		services.clear();
	}

	
	private class ConnectionData {
		private String connectionDriver;
		private String connectionURL;
		private String dialect;
		private String username;
		private String password;
		public ConnectionData(String connectionDriver, String connectionURL,
				String dialect, String username, String password) {
			super();
			this.connectionDriver = connectionDriver;
			this.connectionURL = connectionURL;
			this.dialect = dialect;
			this.username = username;
			this.password = password;
		}
		public String getConnectionDriver() {
			return connectionDriver;
		}
		public String getConnectionURL() {
			return connectionURL;
		}
		public String getDialect() {
			return dialect;
		}
		public String getUsername() {
			return username;
		}
		public String getPassword() {
			return password;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((connectionDriver == null) ? 0 : connectionDriver
							.hashCode());
			result = prime * result
					+ ((connectionURL == null) ? 0 : connectionURL.hashCode());
			result = prime * result
					+ ((dialect == null) ? 0 : dialect.hashCode());
			result = prime * result
					+ ((password == null) ? 0 : password.hashCode());
			result = prime * result
					+ ((username == null) ? 0 : username.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ConnectionData other = (ConnectionData) obj;
			if (connectionDriver == null) {
				if (other.connectionDriver != null)
					return false;
			} else if (!connectionDriver.equals(other.connectionDriver))
				return false;
			if (connectionURL == null) {
				if (other.connectionURL != null)
					return false;
			} else if (!connectionURL.equals(other.connectionURL))
				return false;
			if (dialect == null) {
				if (other.dialect != null)
					return false;
			} else if (!dialect.equals(other.dialect))
				return false;
			if (password == null) {
				if (other.password != null)
					return false;
			} else if (!password.equals(other.password))
				return false;
			if (username == null) {
				if (other.username != null)
					return false;
			} else if (!username.equals(other.username))
				return false;
			return true;
		}
		
		
	}
}
