package org.csstudio.nams.service.configurationaccess.localstore;


import java.util.Iterator;
import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.configurationElements.TopicDTO;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
			test();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to start LocalConfigurationStoreService's bundle", t);
		}
	}

	private void initializeHibernate() {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
    	configuration= configuration.addAnnotatedClass(TopicDTO.class);
    	
    	AnnotationConfiguration configured = configuration.configure();
    	sessionFactory = configured.buildSessionFactory();
	}

	public void stop(BundleContext context) throws Exception {
		session.close();
		sessionFactory.close();
	}
	
	private void test() 
	{
		Transaction tx = session.beginTransaction();
		TopicDTO message = new TopicDTO();
		Integer msgId = (Integer) session.save(message);
		System.out.println("New TOPIC id: " + msgId);
		tx.commit();


		// Second unit of work

		Transaction newTransaction = session.beginTransaction();
		List<?> messages = session.createQuery(
				"from TopicDTO t order by t.id asc").list();
		System.out.println(messages.size() + " TOPIC(s) found:");

		for (Iterator<?> iter = messages.iterator(); iter.hasNext();) {
			TopicDTO loadedMsg = (TopicDTO) iter.next();
			System.out.println(loadedMsg.toString());
		}
		newTransaction.commit();		
	}
}

