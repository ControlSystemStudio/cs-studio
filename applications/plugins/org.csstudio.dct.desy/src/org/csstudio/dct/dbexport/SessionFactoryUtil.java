package org.csstudio.dct.dbexport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryUtil {
	private static SessionFactoryUtil instance;
	
	private org.hibernate.SessionFactory sessionFactory;

	private SessionFactoryUtil() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	public static SessionFactory getInstance() {
		if(instance==null) {
			instance = new SessionFactoryUtil();
		}
		return instance.getSessionFactory();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public org.hibernate.SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void close() {
		if (sessionFactory != null)
			sessionFactory.close();
		sessionFactory = null;

	}
}
