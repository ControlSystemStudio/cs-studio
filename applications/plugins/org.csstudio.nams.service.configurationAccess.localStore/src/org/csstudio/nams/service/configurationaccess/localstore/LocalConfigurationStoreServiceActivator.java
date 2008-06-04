package org.csstudio.nams.service.configurationaccess.localstore;


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

	public void start(BundleContext context) throws Exception {
		try {
			initializeHibernate();
			session = sessionFactory.openSession();
			
		} catch (Throwable t) {
			throw new RuntimeException("Failed to start LocalConfigurationStoreService's bundle", t);
		}
	}

	private void initializeHibernate() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
//    	configuration= configuration.addAnnotatedClass(TopicDTO.class);
    	
    	AnnotationConfiguration configured = configuration.configure();
    	sessionFactory = configured.buildSessionFactory();
    	
	}

	public void stop(BundleContext context) throws Exception {
		session.close();
		sessionFactory.close();
	}
}

