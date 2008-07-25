package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

/**
 * Performs {@link UnitOfWork}s in a Hibernate sessions transaction.
 */
public class TransactionProcessor {

	/**
	 * The session factory used to open sessions.
	 */
	private final SessionFactory sessionFactory;

	/**
	 * The lock used to lock the transactive behaviour of unit of works.
	 */
	private ReentrantLock lock;

	/**
	 * The logger to log to. TODO Produce log output
	 */
	@SuppressWarnings("unused")
	private final Logger logger;

	/**
	 * Creates an instance for given Hibernate {@link SessionFactory}.
	 */
	public TransactionProcessor(SessionFactory sessionFactory, Logger logger) {
		this.sessionFactory = sessionFactory;
		this.logger = logger;
		this.lock = new ReentrantLock(true);
	}

	/**
	 * Performs given one unit of work n a Hibernate session transaction.
	 * Performs only one unit at time! (Using {@link ReentrantLock})
	 * 
	 * TODO Rename to doExclusiveInTransaction
	 */
	public <T> T doInTransaction(UnitOfWork<T> work) throws StorageException,
			StorageError, InconsistentConfigurationException,
			InterruptedException {
		Contract.requireNotNull("work", work);

		Session session = null;
		Transaction tx = null;
		T result = null;
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			session = this.openNewSession();
			tx = session.beginTransaction();
			tx.begin();

			logger.logInfoMessage(this, "Beginning work...");
			result = work.doWork(new MapperImpl(session));
			logger.logInfoMessage(this, "... done.");

			tx.commit();
		} catch (final Throwable e) {
			logger.logInfoMessage(this, "Error occurred in work process...", e);
			try {
				tx.rollback();
			} catch (final Throwable t) {
				new StorageError("unable to roll back failed transaction", t);
			} finally {
				if (lock.isHeldByCurrentThread()) {
					lock.unlock();
				}
				// There is no need to close this session cause after this error
				// everything is to be stopped and checked!
				// - closeSession(session);
			}
			new StorageException("failed to process unit of work", e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
			closeSession(session);
		}

		Contract.ensureResultNotNull(result);
		return result;
	}

	/**
	 * A implementation of Mapper working on current session of this processors.
	 */
	private class MapperImpl implements Mapper {
		private final Session session;

		/**
		 * Creates a new mapper with given session. No check is be done on
		 * working if session is open!
		 */
		public MapperImpl(Session session) {
			this.session = session;
		}

		/**
		 * {@inheritDoc}
		 */
		public void delete(NewAMSConfigurationElementDTO element)
				throws Throwable {
			if (element instanceof HasManuallyJoinedElements) {
				HasManuallyJoinedElements elementAsElementWithJoins = (HasManuallyJoinedElements) element;
				elementAsElementWithJoins.deleteJoinLinkData(this);
			}

			session.delete(element);
		}

		/**
		 * {@inheritDoc}
		 */
		public <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
				Class<T> clasz, boolean loadManuallyJoinedMappingsIfAvailable)
				throws Throwable {
			List<T> result = loadAll(session, clasz);

			if (loadManuallyJoinedMappingsIfAvailable) {
				for (T element : result) {
					if (element instanceof HasManuallyJoinedElements) {
						HasManuallyJoinedElements elementAsElementWithJoins = (HasManuallyJoinedElements) element;
						elementAsElementWithJoins.loadJoinData(this);
					}
				}
			}

			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		public void save(NewAMSConfigurationElementDTO element)
				throws Throwable {
			session.saveOrUpdate(element);

			if (element instanceof HasManuallyJoinedElements) {
				HasManuallyJoinedElements elementAsElementWithJoins = (HasManuallyJoinedElements) element;
				elementAsElementWithJoins.storeJoinLinkData(this);
			}
		}

		/**
		 * Loads a list of all elements from session and performs the unsafe
		 * cast.
		 */
		@SuppressWarnings("unchecked")
		private <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
				Session session, Class<T> clasz) throws Throwable {
			return session.createCriteria(clasz).list();
		}
	}

	/**
	 * Opens a new session.
	 */
	private Session openNewSession() throws Throwable {
		Session result = null;
		result = this.sessionFactory.openSession();
		result.setCacheMode(CacheMode.IGNORE);
		result.setFlushMode(FlushMode.COMMIT);
		return result;
	}

	/**
	 * Closes given session if session is open.
	 */
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
}
