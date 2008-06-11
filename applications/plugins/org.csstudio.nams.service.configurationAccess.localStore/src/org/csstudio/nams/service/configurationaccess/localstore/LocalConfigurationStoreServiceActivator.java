package org.csstudio.nams.service.configurationaccess.localstore;


import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiBundleDeactivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterGruppenZuAlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
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
		configuration = configuration
				.addAnnotatedClass(ReplicationStateDTO.class);

		configuration = configuration
				.addAnnotatedClass(AlarmbearbeiterDTO.class);
		configuration = configuration
				.addAnnotatedClass(AlarmbearbeiterGruppenDTO.class);
		configuration = configuration
				.addAnnotatedClass(AlarmbearbeiterGruppenZuAlarmbearbeiterDTO.class);
		configuration = configuration
				.addAnnotatedClass(FilterConditionDTO.class);
		configuration = configuration
				.addAnnotatedClass(FilterConditionTypeDTO.class);
		configuration = configuration.addAnnotatedClass(TopicDTO.class);

		configuration = configuration.addAnnotatedClass(StringFilterConditionDTO.class);
		configuration = configuration.addAnnotatedClass(StringArrayFilterConditionDTO.class);
		configuration = configuration.addAnnotatedClass(StringArrayFilterConditionCompareValuesDTO.class);
		configuration = configuration.addAnnotatedClass(JunctorConditionDTO.class);
		configuration = configuration.addAnnotatedClass(ProcessVariableFilterConditionDTO.class);

		final AnnotationConfiguration configured = configuration.configure();
		this.sessionFactory = configured.buildSessionFactory();
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
