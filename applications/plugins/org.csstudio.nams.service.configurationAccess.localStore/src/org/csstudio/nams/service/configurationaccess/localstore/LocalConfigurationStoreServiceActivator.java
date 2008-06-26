package org.csstudio.nams.service.configurationaccess.localstore;


import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ConfigurationServiceFactory;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class LocalConfigurationStoreServiceActivator extends
		AbstractBundleActivator implements BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.configurationAccess.localStore";
	private ConfigurationServiceFactoryImpl configurationServiceFactoryImpl;

	@OSGiBundleActivationMethod
	public OSGiServiceOffers startBundle() throws Exception {
		OSGiServiceOffers result = new OSGiServiceOffers();
		try {
			//  Extension point auslesen
			//  configuration abfragen
			//  configuration Hibernate mitteilen.

//			this.initializeHibernate();
//			this.session = this.sessionFactory.openSession();
//
//			result.put(LocalStoreConfigurationService.class,
//					new LocalStoreConfigurationServiceImpl(this.session));
			
			configurationServiceFactoryImpl = new ConfigurationServiceFactoryImpl();
			result.put(ConfigurationServiceFactory.class, configurationServiceFactoryImpl);
		} catch (final Throwable t) {
			throw new RuntimeException(
					"Failed to start LocalConfigurationStoreService's bundle",
					t);
		}
		return result;
	}

	@OSGiBundleDeactivationMethod
	public void stopBundle() throws Exception {
		configurationServiceFactoryImpl.closeSessions();
//		this.session.close();
//		this.sessionFactory.close();
	}
	
	
	
//	private SessionFactory sessionFactory;
//	private Session session;
//
//	private void initializeHibernate() {
//		AnnotationConfiguration configuration = new AnnotationConfiguration();
//		configuration
//				.addAnnotatedClass(ReplicationStateDTO.class)
//				.addAnnotatedClass(AlarmbearbeiterDTO.class)
//				.addAnnotatedClass(AlarmbearbeiterGruppenDTO.class)
//				.addAnnotatedClass(FilterConditionDTO.class)
//				.addAnnotatedClass(FilterConditionTypeDTO.class)
//				.addAnnotatedClass(TopicDTO.class)
//				.addAnnotatedClass(AlarmbearbeiterZuAlarmbearbeiterGruppenDTO.class)
//				.addAnnotatedClass(FilterConditionsToFilterDTO.class)
//
//				.addAnnotatedClass(JunctorConditionDTO.class)
//				.addAnnotatedClass(StringFilterConditionDTO.class)
//				.addAnnotatedClass(StringArrayFilterConditionDTO.class)
//				
//				.addAnnotatedClass(StringArrayFilterConditionCompareValuesDTO.class) // TODO Mapping in Configuration!!!
//				.addAnnotatedClass(TimeBasedFilterConditionDTO.class)
//				.addAnnotatedClass(ProcessVariableFilterConditionDTO.class)
//				.addAnnotatedClass(FilterDTO.class)
//				.addAnnotatedClass(HistoryDTO.class)
//
////				.setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
////				.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))")
////				.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
////				.setProperty("hibernate.connection.username", "DESY")
////				.setProperty("hibernate.connection.password", "DESY")
//
//				.setProperty("hibernate.connection.driver_class", "org.apache.derby.jdbc.ClientDriver")
//				.setProperty("hibernate.connection.url", "jdbc:derby://localhost:1527/amsdb")
//				.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect")
//				.setProperty("hibernate.connection.username", "APP")
//				.setProperty("hibernate.connection.password", "APP")
//
//				.setProperty("hibernate.connection.pool_size", "1")
//				.setProperty("current_session_context_class", "thread")
//				.setProperty("cache.provider_class", "org.hibernate.cache.NoCacheProvider")
//				.setProperty("show_sql", "true")
//				.setProperty("hbm2ddl.auto", "update")
//				.setProperty("hibernate.mapping.precedence", "class");
//
//		//TODO in die config auslagern
//		Logger.getLogger("org.hibernate").setLevel(Level.WARN);
//		
////		final AnnotationConfiguration configured = configuration.configure();
//		this.sessionFactory = configuration.buildSessionFactory();
//	}


}
