package org.csstudio.nams.service.configurationaccess.localstore;


import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.AlarmbearbeiterZuAlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class LocalConfigurationStoreServiceActivator extends
		AbstractBundleActivator implements BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.configurationAccess.localStore";
	private SessionFactory sessionFactory;
	private Session session;

	private void initializeHibernate() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration
				.addAnnotatedClass(ReplicationStateDTO.class)
				.addAnnotatedClass(AlarmbearbeiterDTO.class)
				.addAnnotatedClass(AlarmbearbeiterGruppenDTO.class)
				.addAnnotatedClass(FilterConditionDTO.class)
				.addAnnotatedClass(FilterConditionTypeDTO.class)
				.addAnnotatedClass(TopicDTO.class)
				.addAnnotatedClass(AlarmbearbeiterZuAlarmbearbeiterGruppenDTO.class)
				.addAnnotatedClass(FilterConditionsToFilterDTO.class)

				.addAnnotatedClass(JunctorConditionDTO.class)
				.addAnnotatedClass(StringFilterConditionDTO.class)
				.addAnnotatedClass(StringArrayFilterConditionDTO.class)
				
//				.addAnnotatedClass(StringArrayFilterConditionCompareValuesDTO.class) // TODO Mapping in Configuration!!!
				.addAnnotatedClass(TimeBasedFilterConditionDTO.class)
				.addAnnotatedClass(ProcessVariableFilterConditionDTO.class)
				.addAnnotatedClass(FilterDTO.class)
//				.setProperty("hibernate.connection.driver_class", "org.apache.derby.jdbc.ClientDriver")
				.setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
//				.setProperty("hibernate.connection.url", "jdbc:derby://134.100.12.94:1527/amsdb")
				.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))")
				.setProperty("hibernate.connection.username", "DESY")
				.setProperty("hibernate.connection.password", "DESY")
				.setProperty("hibernate.connection.pool_size", "1")
//				.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect")
				.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
				.setProperty("current_session_context_class", "thread")
				.setProperty("cache.provider_class", "org.hibernate.cache.NoCacheProvider")
				.setProperty("show_sql", "true")
				.setProperty("hbm2ddl.auto", "update")
				.setProperty("hibernate.mapping.precedence", "class");

//		final AnnotationConfiguration configured = configuration.configure();
		this.sessionFactory = configuration.buildSessionFactory();
	}

	@OSGiBundleActivationMethod
	public OSGiServiceOffers startBundle() throws Exception {
		OSGiServiceOffers result = new OSGiServiceOffers();
		try {
			// TODO Extension point auslesen
			// TODO configuration abfragen
			// TODO configuration Hibernate mitteilen.

			this.initializeHibernate();
			this.session = this.sessionFactory.openSession();

			result.put(LocalStoreConfigurationService.class,
					new LocalStoreConfigurationServiceImpl(this.session));
		} catch (final Throwable t) {
			throw new RuntimeException(
					"Failed to start LocalConfigurationStoreService's bundle",
					t);
		}
		return result;
	}

	@OSGiBundleDeactivationMethod
	public void stopBundle() throws Exception {
		this.session.close();
		this.sessionFactory.close();
	}
}
