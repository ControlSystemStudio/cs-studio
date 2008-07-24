package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.Collection;
import java.util.List;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

public class ConfigurationStoreService2Impl {
	static interface UnitOfWork<T> {
		public T doWork(Session session) throws Throwable;
	}
	private final Logger logger;

	private final SessionFactory sessionFactory;
	
	public ConfigurationStoreService2Impl(final SessionFactory sessionFactory,
			final Logger logger) {
		this.sessionFactory = sessionFactory;
		this.logger = logger;
	}
	
	public RubrikDTO createNewCategory(String string, RubrikTypeEnum topic) {
		return new RubrikDTO();
	}

	public <T> Collection<T> findAll(final Class<T> clasz) throws StorageError, StorageException, InconsistentConfigurationException {
		Contract.requireNotNull("clasz", clasz);
		
		Collection<T> result = null;
		
		result = doInTransaction(new UnitOfWork<Collection<T>>() {
			public Collection<T> doWork(Session session)
					throws Throwable {
				return loadAll(session, clasz);
			}
		});
		logger.logDebugMessage(this, "found " + result.size() + " elements of type " + clasz.getName());
			
			
		Contract.ensureResultNotNull(result);
		return result;
	}

	private void closeSession(Session session) throws StorageError {
		if (session != null && session.isOpen()) {
			try {
				session.flush();
				session.close();
			} catch (final HibernateException he) {
				throw new StorageError("session could not be closed", he);
			}
		}
	}
	
	private <T> T doInTransaction(UnitOfWork<T> work) throws StorageException, StorageError, InconsistentConfigurationException {
		Contract.requireNotNull("runnable", work);
		Session session = null;
		Transaction tx = null;
		T result = null;
		try {
			session = this.openNewSession();
			tx = session.beginTransaction();
			tx.begin();
			
			result = work.doWork(session);
			
			tx.commit();
		} catch (final Throwable e) {
			new StorageException("failed to process unit of work", e);
		} finally {
			closeSession(session);
		}
		
		Contract.ensureResultNotNull(result);
		return result;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> loadAll(
			Session session, Class<T> clasz) throws Throwable {
		List<T> result
		= session.createCriteria(clasz).list();

		return result;
	}

	private Session openNewSession() throws HibernateException {
		Session result = null;
		result = this.sessionFactory.openSession();
		result.setCacheMode(CacheMode.IGNORE);
		result.setFlushMode(FlushMode.COMMIT);

		return result;
	}
}
