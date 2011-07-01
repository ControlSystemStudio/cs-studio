
package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.Collection;
import java.util.List;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.HistoryDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.ReplicationStateDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.TopicDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.UnknownConfigurationElementError;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

/**
 * Implementation für Hibernate.
 * 
 * TODO Rename to ConfigurationStoreServiceHibernateImpl
 */
class LocalStoreConfigurationServiceImpl implements
		LocalStoreConfigurationService {

	private final Logger _logger;
	private final SessionFactory _sessionFactory;

	private Session _sessionWorkingOn = null;

	private final TransactionProcessor transactionProcessor;

	/**
	 * 
	 * @param sessionFactory
	 *            The session to work on; the session will be treated as
	 *            exclusive instance and be closed on finalization of this
	 *            service instance.
	 * @param logger
	 */
	public LocalStoreConfigurationServiceImpl(
			final SessionFactory sessionFactory, final Logger logger) {
		this._sessionFactory = sessionFactory;
		this._logger = logger;

		this.transactionProcessor = new TransactionProcessor(sessionFactory,
				logger);
	}

	@Override
    public void deleteDTO(final NewAMSConfigurationElementDTO dto)
			throws StorageError, StorageException,
			InconsistentConfigurationException {

		final UnitOfWork<Object> loadEntireConfigurationWork = new UnitOfWork<Object>() {
			@Override
            public Object doWork(Mapper mapper) throws Throwable {
				mapper.delete(dto);
				return dto;
			}
		};

		try {
			this.transactionProcessor
					.doInTransaction(loadEntireConfigurationWork);
		} catch (final InterruptedException e) {
			this._logger.logWarningMessage(this, "Delete of DTO interrupted", e);
			throw new StorageException("Delete of DTO interrupted", e);
		}
	}

	@Override
    public ReplicationStateDTO getCurrentReplicationState()
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		ReplicationStateDTO result = null;
		Session session = null;
		try {
			session = this.openNewSession();
			final Transaction newTransaction = session.beginTransaction();
			newTransaction.begin();
			final List<?> messages = session.createQuery(
					"from ReplicationStateDTO r where r.flagName = '"
							+ ReplicationStateDTO.DB_FLAG_NAME + "'").list();

			if (!messages.isEmpty()) {
				result = (ReplicationStateDTO) messages.get(0);
			}
			newTransaction.commit();
		} catch (final Throwable t) {
			new StorageError("Failed to write replication flag", t);
		} finally {
			this.closeSession(session);
		}

		if (result == null) {
			throw new InconsistentConfigurationException(
					"Replication state unavailable.");
		}

		return result;
	}

	@Override
    public Configuration getEntireConfiguration() throws StorageError,
			StorageException, InconsistentConfigurationException {

		Configuration result = null;

		final UnitOfWork<Configuration> loadEntireConfigurationWork = new UnitOfWork<Configuration>() {
			@Override
            public Configuration doWork(Mapper mapper) throws Throwable {
				Configuration resultOfUnit = null;

				Collection<RubrikDTO> alleRubriken = mapper.loadAll(
						RubrikDTO.class, true); 
				
				Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter = mapper
						.loadAll(AlarmbearbeiterDTO.class, true);
				Collection<TopicDTO> alleAlarmtopics = mapper.loadAll(
						TopicDTO.class, true);
				Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen = mapper
						.loadAll(AlarmbearbeiterGruppenDTO.class, true);
				Collection<FilterConditionDTO> allFilterConditions = mapper
						.loadAll(FilterConditionDTO.class, true);
				Collection<FilterDTO> allFilters = mapper.loadAll(
						FilterDTO.class, true);
				Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO = mapper
						.loadAll(DefaultFilterTextDTO.class, true);

				resultOfUnit = new Configuration(alleAlarmbarbeiter,
						alleAlarmtopics, alleAlarmbearbeiterGruppen,
						allFilters, allFilterConditions, alleRubriken,
						allDefaultFilterTextDTO);

				return resultOfUnit;
			}
		};

		try {
			result = this.transactionProcessor
					.doInTransaction(loadEntireConfigurationWork);
		} catch (final InterruptedException e) {
			this._logger.logWarningMessage(this,
					"Load of entire configuration interrupted", e);
			throw new StorageException(
					"Load of entire configuration interrupted", e);
		}

		return result;
	}

	@Override
    public FilterConfiguration getEntireFilterConfiguration()
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		
//		FilterConfiguration conf = null;
//
//		final UnitOfWork<FilterConfiguration> loadEntireConfigurationWork = new UnitOfWork<FilterConfiguration>() {
//			public FilterConfiguration doWork(Mapper mapper) throws Throwable {
//				FilterConfiguration resultOfUnit = null;
//
////				Collection<RubrikDTO> alleRubriken = Collections.emptySet();
////				
////				Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter = mapper
////						.loadAll(AlarmbearbeiterDTO.class, true);
////				Collection<TopicDTO> alleAlarmtopics = mapper.loadAll(
////						TopicDTO.class, true);
////				Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen = mapper
////						.loadAll(AlarmbearbeiterGruppenDTO.class, true);
////				Collection<FilterConditionDTO> allFilterConditions = mapper
////						.loadAll(FilterConditionDTO.class, true);
//				Collection<FilterDTO> allFilters = mapper.loadAll(
//						FilterDTO.class, true);
////				Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO = Collections.emptySet();
//
//				resultOfUnit = new FilterConfiguration(allFilters);
//
//				return resultOfUnit;
//			}
//		};
//
//		try {
//			conf = this.transactionProcessor
//					.doInTransaction(loadEntireConfigurationWork);
//		} catch (final InterruptedException e) {
//			this.logger.logWarningMessage(this,
//					"Load of entire configuration interrupted", e);
//			throw new StorageException(
//					"Load of entire configuration interrupted", e);
//		}
//		
//		FilterConfiguration result = conf;

		
		FilterConfiguration result = null;
		final UnitOfWork<FilterConfiguration> loadEntireFilterConfigurationWork = new UnitOfWork<FilterConfiguration>() {
			@Override
            public FilterConfiguration doWork(Mapper mapper) throws Throwable {
				FilterConfiguration resultOfUnit = null;

				Collection<FilterDTO> allFilters = mapper.loadAll(
						FilterDTO.class, true);

				resultOfUnit = new FilterConfiguration(allFilters);

				return resultOfUnit;
			}
		};

		try {
			result = this.transactionProcessor
					.doInTransaction(loadEntireFilterConfigurationWork);
		} catch (final InterruptedException e) {
			this._logger.logWarningMessage(this,
					"Load of entire configuration interrupted", e);
			throw new StorageException(
					"Load of entire configuration interrupted", e);
		}

		return result;
	}
	
	@Override
    public void prepareSynchonization() throws StorageError, StorageException,
			InconsistentConfigurationException {
		// Hier die Syn-Tabellen anlegen / Datgen kopieren / GGf. über ein
		// HSQL-Statement.
		
		Transaction newTransaction = null;
		Session session = null;
		try {
			session = this.openNewSession();
			newTransaction = session.beginTransaction();
			newTransaction.begin();

			SQLQuery query = null;
			String[] tabellen = new String[] { "AMS_FILTER",
					"AMS_FILTERACTION", "AMS_FILTERACTIONTYPE",
					"AMS_FILTERCONDITION", "AMS_FILTERCONDITIONTYPE",
					"AMS_FILTERCONDITION_PV", "AMS_FILTERCONDITION_STRING",
					"AMS_FILTERCOND_ARRSTR", "AMS_FILTERCOND_ARRSTRVAL",
					"AMS_FILTERCOND_CONJ_COMMON", "AMS_FILTERCOND_TIMEBASED",
					"AMS_FILTER_FILTERACTION", "AMS_FILTER_FILTERCONDITION",
					"AMS_TOPIC", "AMS_USER", "AMS_USERGROUP",
					"AMS_USERGROUP_USER", "AMS_FILTERCOND_JUNCTION",
					"AMS_FILTERCOND_FILTERCOND", "AMS_FILTERCOND_NEGATION" };

			for (String tabelle : tabellen) {
				query = session.createSQLQuery("delete from " + tabelle
						+ "_SYN");
				query.executeUpdate();

				query = session.createSQLQuery("INSERT INTO " + tabelle
						+ "_SYN SELECT * FROM " + tabelle);
				query.executeUpdate();
			}

			newTransaction.commit();
		} catch (final Throwable t) {
			if (newTransaction != null) {
				newTransaction.rollback();
			}
			throw new StorageException("unable to save replication state", t);
		} finally {
		    if (session != null) {
		        session.flush();
		        session.close();
		    }
		}
	}

	@Override
    public void saveCurrentReplicationState(
			final ReplicationStateDTO currentState) throws StorageError,
			StorageException, UnknownConfigurationElementError {
		Transaction newTransaction = null;
		Session session = null;
		try {
			session = this.openNewSession();
			newTransaction = session.beginTransaction();
			newTransaction.begin();
			session.saveOrUpdate(currentState);
			newTransaction.commit();
		} catch (final Throwable t) {
			if (newTransaction != null) {
				newTransaction.rollback();
			}
			throw new StorageException("unable to save replication state", t);
		} finally {
			this.closeSession(session);
		}
	}

	@Override
    public void saveDTO(final NewAMSConfigurationElementDTO dto)
			throws StorageError, StorageException,
			InconsistentConfigurationException {

		final UnitOfWork<NewAMSConfigurationElementDTO> saveWork = new UnitOfWork<NewAMSConfigurationElementDTO>() {
			@Override
            public NewAMSConfigurationElementDTO doWork(Mapper mapper)
					throws Throwable {

				mapper.save(dto); // performs "deep" save

				return dto;
			}
		};

		try {
			this.transactionProcessor.doInTransaction(saveWork);
		} catch (final InterruptedException e) {
			this._logger.logWarningMessage(this, "save has been interrupted", e);
			throw new StorageException("save has been interrupted", e);
		}
	}

	@Override
    public void saveHistoryDTO(final HistoryDTO historyDTO)
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		Transaction newTransaction = null;
		Session session = null;
		try {
			session = this.openNewSession();
			newTransaction = session.beginTransaction();
			newTransaction.begin();
			session.saveOrUpdate(historyDTO);
			newTransaction.commit();
		} catch (final Throwable t) {
			if (newTransaction != null) {
				newTransaction.rollback();
			}
			throw new StorageException("unable to save history element", t);
		} finally {
			this.closeSession(session);
		}
	}

	/**
	 * Für Tests.
	 */
	SessionFactory getSessionFactory() {
		return this._sessionFactory;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this._sessionFactory.close();
	}

	private void closeSession(final Session session) throws HibernateException {
		if ((session != null) && session.isOpen()) {
			try {
				session.flush();
				// session.close();
			} catch (final HibernateException he) {
				this._sessionWorkingOn.close();
				this._sessionWorkingOn = null;
				throw new StorageError("session could not be closed", he);
			}
		}
	}

	private Session openNewSession() throws HibernateException {
		Session result = null;
		if (this._sessionWorkingOn == null) {
			result = this._sessionFactory.openSession();
			result.setCacheMode(CacheMode.IGNORE);
			result.setFlushMode(FlushMode.COMMIT);
		} else {
			result = this._sessionWorkingOn;
		}
		return result;
	}

}
