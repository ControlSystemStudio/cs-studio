package org.csstudio.nams.service.configurationaccess.localstore;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterGruppenZuAlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LocalConfigurationStoreServiceActivator implements BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.configurationAccess.localStore";
	private SessionFactory sessionFactory;
	private Session session;

	private void initializeHibernate() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration = configuration.addAnnotatedClass(ReplicationStateDTO.class);

		configuration = configuration.addAnnotatedClass(AlarmbearbeiterDTO.class);
		configuration = configuration.addAnnotatedClass(AlarmbearbeiterGruppenDTO.class);
		configuration = configuration.addAnnotatedClass(AlarmbearbeiterGruppenZuAlarmbearbeiterDTO.class);
		configuration = configuration.addAnnotatedClass(FilterConditionDTO.class);
		configuration = configuration.addAnnotatedClass(FilterConditionTypeDTO.class);
		configuration = configuration.addAnnotatedClass(TopicDTO.class);

		configuration = configuration.addAnnotatedClass(StringFilterConditionDTO.class);
		configuration = configuration.addAnnotatedClass(StringArrayFilterConditionDTO.class);
		configuration = configuration.addAnnotatedClass(StringArrayFilterConditionCompareValuesDTO.class);
		
		final AnnotationConfiguration configured = configuration.configure();
		this.sessionFactory = configured.buildSessionFactory();
	}

	public void start(final BundleContext context) throws Exception {
		try {
			// TODO Extension point auslesen
			// TODO configuration abfragen
			// TODO configuration Hibernate mitteilen.

			this.initializeHibernate();
			this.session = this.sessionFactory.openSession();

			context.registerService(LocalStoreConfigurationService.class
					.getName(), new LocalStoreConfigurationServiceImpl(
					this.session), null);
		} catch (final Throwable t) {
			throw new RuntimeException(
					"Failed to start LocalConfigurationStoreService's bundle",
					t);
		}
	}

	public void stop(final BundleContext context) throws Exception {
		this.session.close();
		this.sessionFactory.close();
	}
}
