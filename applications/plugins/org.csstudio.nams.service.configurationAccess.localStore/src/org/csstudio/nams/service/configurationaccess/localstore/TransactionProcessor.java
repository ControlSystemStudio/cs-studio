
package org.csstudio.nams.service.configurationaccess.localstore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

/**
 * Performs {@link UnitOfWork}s in a Hibernate sessions transaction.
 */
public class TransactionProcessor {

	/**
	 * A implementation of Mapper working on current session of this processors.
	 */
	private class MapperImpl implements Mapper {
		private final Session _session;

		private final Map<Class<?>, Map<Serializable, NewAMSConfigurationElementDTO>>idCacheMap;
		private final Map<Class<?>,List<NewAMSConfigurationElementDTO>> loadAllCacheMap;
		
		/**
		 * Creates a new mapper with given session. No check is be done on
		 * working if session is open!
		 */
		public MapperImpl(final Session session) {
			this._session = session;
			idCacheMap = new HashMap<Class<?>,Map<Serializable, NewAMSConfigurationElementDTO>>();
			loadAllCacheMap = new HashMap<Class<?>, List<NewAMSConfigurationElementDTO>>();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
        public void delete(final NewAMSConfigurationElementDTO element)
				throws Throwable {
			if (element instanceof HasManuallyJoinedElements) {
				final HasManuallyJoinedElements elementAsElementWithJoins = (HasManuallyJoinedElements) element;
				elementAsElementWithJoins.deleteJoinLinkData(this);
			}

			this._session.delete(element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
        public <T extends NewAMSConfigurationElementDTO> T findForId(
				final Class<T> clasz, final Serializable id,
				final boolean loadManuallyJoinedMappingsIfAvailable)
				throws Throwable {
			final T result = this.loadForId(this._session, clasz, id);

			if (loadManuallyJoinedMappingsIfAvailable) {
				if (result instanceof HasManuallyJoinedElements) {
					final HasManuallyJoinedElements elementAsElementWithJoins = (HasManuallyJoinedElements) result;
					elementAsElementWithJoins.loadJoinData(this);
				}
			}

			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
        public <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
				final Class<T> clasz,
				final boolean loadManuallyJoinedMappingsIfAvailable)
				throws Throwable {
			final List<T> result = this.loadAll(this._session, clasz);

			if (loadManuallyJoinedMappingsIfAvailable) {
				for (final T element : result) {
					if (element instanceof HasManuallyJoinedElements) {
						final HasManuallyJoinedElements elementAsElementWithJoins = (HasManuallyJoinedElements) element;
						elementAsElementWithJoins.loadJoinData(this);
					}
				}
			}

			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
        public void save(final NewAMSConfigurationElementDTO element)
				throws Throwable {
			this._session.saveOrUpdate(element);

			if (element instanceof HasManuallyJoinedElements) {
				final HasManuallyJoinedElements elementAsElementWithJoins = (HasManuallyJoinedElements) element;
				elementAsElementWithJoins.storeJoinLinkData(this);
			}
		}
		
		/**
		 * Loads a list of all elements from session and performs the unsafe
		 * cast.
		 */
		@SuppressWarnings("unchecked")
		private <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
				final Session session, final Class<T> clasz) throws Throwable {
			
			List<NewAMSConfigurationElementDTO> dtoList;
			if(loadAllCacheMap.containsKey(clasz)) {
				dtoList = loadAllCacheMap.get(clasz);
			} else {
				dtoList = session.createCriteria(clasz).list();
				loadAllCacheMap.put(clasz, dtoList);
			}
			return (List<T>) dtoList;
		}

		/**
		 * Loads an element from session and performs the unsafe cast.
		 */
		@SuppressWarnings("unchecked")
		private <T extends NewAMSConfigurationElementDTO> T loadForId(
				final Session session, final Class<T> clasz,
				final Serializable id) throws Throwable {

			NewAMSConfigurationElementDTO result;
			Map<Serializable, NewAMSConfigurationElementDTO> idToDtoMap;
			if(idCacheMap.containsKey(clasz)) {
				idToDtoMap = idCacheMap.get(clasz);
			} else {
				idToDtoMap = new HashMap<Serializable, NewAMSConfigurationElementDTO>();
				idCacheMap.put(clasz, idToDtoMap);
			}
			if(idToDtoMap.containsKey(id)) {
				result = idToDtoMap.get(id);
			} else {
				result = (T) session.createCriteria(clasz).add(Restrictions.idEq(id))
						.uniqueResult();
				idToDtoMap.put(id, result);
			}
			return (T) result;
		}
	}

	/**
	 * The session factory used to open sessions.
	 */
	private final SessionFactory _sessionFactory;

	/**
	 * The lock used to lock the transactive behaviour of unit of works.
	 */
	private final ReentrantLock lock;

	/**
	 * The logger to log to. TODO Produce log output
	 */
	private final ILogger _logger;

	/**
	 * Creates an instance for given Hibernate {@link SessionFactory}.
	 */
	public TransactionProcessor(final SessionFactory sessionFactory,
			final ILogger logger) {
		this._sessionFactory = sessionFactory;
		this._logger = logger;
		this.lock = new ReentrantLock(true);
	}

	/**
	 * Performs given one unit of work n a Hibernate session transaction.
	 * Performs only one unit at time! (Using {@link ReentrantLock})
	 * 
	 * TODO Rename to doExclusiveInTransaction
	 */
	public <T> T doInTransaction(final UnitOfWork<T> work)
			throws StorageException, StorageError,
			InconsistentConfigurationException, InterruptedException {
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

			this._logger.logDebugMessage(this, "Beginning unit of work of type "
					+ work.getClass().getName() + "...");
			long startTime = System.currentTimeMillis();
			result = work.doWork(new MapperImpl(session));
			this._logger.logDebugMessage(this, "Time to complete unit of work: "+((System.currentTimeMillis()-startTime)/1000.0) + " seconds.");

			tx.commit();
		} catch (final Throwable e) {
			this._logger.logInfoMessage(this,
					"Error occurred in work process...", e);
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
			throw new StorageException("failed to process unit of work", e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
			this.closeSession(session);
		}

		Contract.ensureResultNotNull(result);
		return result;
	}

	/**
	 * Closes given session if session is open.
	 */
	private void closeSession(final Session session) throws StorageError {
		if ((session != null) && session.isOpen()) {
			try {
				session.flush();
				session.close();
			} catch (final HibernateException he) {
				throw new StorageError("session could not be closed", he);
			}
		}
	}

	/**
	 * Opens a new session.
	 */
	private Session openNewSession() throws Throwable {
		Session result = null;
		result = this._sessionFactory.openSession();
		result.setCacheMode(CacheMode.IGNORE);
		result.setFlushMode(FlushMode.COMMIT);
		return result;
	}
}
