package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
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
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasJoinedElements;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Implementation für Hibernate.
 * 
 * TODO Rename to ConfigurationStoreServiceHibernateImpl
 */
class LocalStoreConfigurationServiceImpl implements
		LocalStoreConfigurationService {

	private final Logger logger;
	private final SessionFactory sessionFactory;

	/**
	 * 
	 * @param session
	 *            The session to work on; the session will be treated as
	 *            exclusive instance and be closed on finalization of this
	 *            service instance.
	 * @param logger
	 */
	public LocalStoreConfigurationServiceImpl(
			final SessionFactory sessionFactory, final Logger logger) {
		this.sessionFactory = sessionFactory;
		this.logger = logger;
	}

	private void addUsersToGroups(
			final Session session,
			final Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen,
			final List<User2UserGroupDTO> alleUser2UserGroupMappings) {
		final HashMap<Integer, AlarmbearbeiterGruppenDTO> gruppen = new HashMap<Integer, AlarmbearbeiterGruppenDTO>();
		for (final AlarmbearbeiterGruppenDTO gruppe : alleAlarmbearbeiterGruppen) {
			gruppen.put(gruppe.getUserGroupId(), gruppe);
		}
		for (final User2UserGroupDTO map : alleUser2UserGroupMappings) {
			try {
				gruppen.get(map.getUser2UserGroupPK().getIUserGroupRef())
						.alarmbearbeiterZuordnen(map);
			} catch (final NullPointerException npe) {
				session.delete(map);
				this.logger.logWarningMessage(this,
						"Deleted invalid User To UserGroup mapping, group "
								+ map.getUser2UserGroupPK().getIUserGroupRef()
								+ " doesn't exist");
			}
		}
	}

	private void closeSession(Session session) throws HibernateException {
		if (session != null && session.isOpen()) {
			try {
				session.flush();
				session.close();
			} catch (final HibernateException he) {
				throw new StorageError("session could not be closed", he);
			}
		}
	}
	
	public void deleteAlarmbearbeiterGruppenDTO(
			final AlarmbearbeiterGruppenDTO dto) throws StorageError,
			StorageException, InconsistentConfigurationException {
		Session session = null;
		Transaction tx = null;
		try {
			session = this.openNewSession();
			tx = session.beginTransaction();
			final Collection<User2UserGroupDTO> user2GroupMappings = session
					.createCriteria(User2UserGroupDTO.class).list();

			for (final User2UserGroupDTO a : user2GroupMappings) {
				if (a.getUser2UserGroupPK().getIUserGroupRef() == dto
						.getUserGroupId()) {
					session.delete(a);
				}
			}
			session.delete(dto);
			tx.commit();
		} catch (final Throwable e) {
			new InconsistentConfigurationException("Could not delete " + dto);
		} finally {
			closeSession(session);
		}
	}

	public void deleteAlarmtopicDTO(final TopicDTO dto) throws StorageError,
			StorageException, InconsistentConfigurationException {
		this.deleteDTO(dto);
	}

	public void deleteDTO(final NewAMSConfigurationElementDTO dto)
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		Transaction transaction = null;
		Session session = null;
		try {
			session = this.openNewSession();
			transaction = session.beginTransaction();
			this.deleteDTONoTransaction(session, dto);

			transaction.commit();
		} catch (final Throwable t) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw new StorageException(
					"failed to delete configuration element of type "
							+ dto.getClass().getSimpleName(), t);
		} finally {
			closeSession(session);
		}
	}

	void deleteDTONoTransaction(final Session session,
			final NewAMSConfigurationElementDTO dto) throws Throwable {
		if (dto instanceof HasJoinedElements) {
			((HasJoinedElements<?>) dto).deleteJoinLinkData(session);
		}

		session.delete(dto);
	}

	public void deleteFilterConditionDTO(final FilterConditionDTO dto)
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		this.deleteDTO(dto);
	}

	public void deleteFilterDTO(final FilterDTO dto)
			throws InconsistentConfigurationException, StorageError,
			StorageException {
		// FIXME fertigstellen: Die Joins/OR/AND/NOTS...
		this.deleteDTO(dto);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.sessionFactory.close();
	}

	public ReplicationStateDTO getCurrentReplicationState()
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		ReplicationStateDTO result = null;
		Session session = null;
		try {
			session = this.openNewSession();
			final Transaction newTransaction = session.beginTransaction();
			final List<?> messages = session.createQuery(
					"from ReplicationStateDTO r where r.flagName = '"
							+ ReplicationStateDTO.DB_FLAG_NAME + "'").list();

			if (!messages.isEmpty()) {
				result = (ReplicationStateDTO) messages.get(0);
			}
			newTransaction.commit();
			session.close();
		} catch (final Throwable t) {
			new StorageError("Failed to write replication flag", t);
		} finally {
			closeSession(session);
		}

		if (result == null) {
			throw new InconsistentConfigurationException(
					"Replication state unavailable.");
		}

		return result;
	}

	public Configuration getEntireConfiguration() throws StorageError,
			StorageException, InconsistentConfigurationException {

		Configuration result = null;
		Transaction transaction = null;
		Session session = null;
		try {
			session = this.openNewSession();

			transaction = session.beginTransaction();
			// 
			Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
			Collection<TopicDTO> alleAlarmtopics;
			Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
			Collection<FilterDTO> allFilters;
			Collection<FilterConditionsToFilterDTO> allFilterConditionMappings;

			Collection<FilterConditionDTO> allFilterConditions;
			Collection<RubrikDTO> alleRubriken;
			List<User2UserGroupDTO> alleUser2UserGroupMappings;
			Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues;

			// PUBLICs
			alleAlarmbarbeiter = session.createCriteria(
					AlarmbearbeiterDTO.class).addOrder(Order.asc("userId"))
					.list();
			alleAlarmbearbeiterGruppen = session.createCriteria(
					AlarmbearbeiterGruppenDTO.class).addOrder(
					Order.asc("userGroupId")).list();

			alleAlarmtopics = session.createCriteria(TopicDTO.class).list();
			allFilters = session.createCriteria(FilterDTO.class).list();

			allFilterConditions = session.createCriteria(
					FilterConditionDTO.class).addOrder(
					Order.asc("iFilterConditionID")).list();

			// Nots befüllen
			for (final FilterConditionDTO filterConditionDTO : allFilterConditions) {
				if (filterConditionDTO instanceof NegationConditionForFilterTreeDTO) {
					final NegationConditionForFilterTreeDTO notCond = (NegationConditionForFilterTreeDTO) filterConditionDTO;
					for (final FilterConditionDTO possibleCondition : allFilterConditions) {
						if (notCond.getINegatedFCRef() == possibleCondition
								.getIFilterConditionID()) {
							notCond
									.setNegatedFilterCondition(possibleCondition);
							break;
						}
					}
				}
			}

			alleRubriken = session.createCriteria(RubrikDTO.class).list();

			alleUser2UserGroupMappings = session.createCriteria(
					User2UserGroupDTO.class).list();

			allFilterConditionMappings = session.createCriteria(
					FilterConditionsToFilterDTO.class).list();
			this
					.pruefeUndOrdnerFilterDieFilterConditionsZu(
							allFilterConditionMappings, allFilterConditions,
							allFilters);
			this.setChildFilterConditionsInJunctorDTOs(allFilterConditions);
			allCompareValues = session.createCriteria(
					StringArrayFilterConditionCompareValuesDTO.class).list();
			this.setStringArrayCompareValues(allCompareValues,
					allFilterConditions);

			final Collection<FilterConditionDTO> allFCs = new HashSet<FilterConditionDTO>(
					allFilterConditions);
			for (final FilterConditionDTO fc : allFCs) {
				if (fc instanceof JunctorConditionForFilterTreeDTO) {
					final JunctorConditionForFilterTreeDTO jcfft = (JunctorConditionForFilterTreeDTO) fc;
					try {
						jcfft.loadJoinData(session, allFCs);
					} catch (final Throwable e) {
						throw new InconsistentConfigurationException(
								"unable to load joined conditions of JunctionConditionForFilters",
								e);
					}
				}
			}
			this.addUsersToGroups(session, alleAlarmbearbeiterGruppen,
					alleUser2UserGroupMappings);

			//
			result = new Configuration(alleAlarmbarbeiter, alleAlarmtopics,
					alleAlarmbearbeiterGruppen, allFilters,
					allFilterConditionMappings, allFilterConditions,
					alleRubriken, alleUser2UserGroupMappings, allCompareValues);
			transaction.commit();
			session.close();
		} catch (final Throwable t) {
			if (transaction != null) {
				transaction.rollback();
			}
			t.printStackTrace();
		} finally {
			closeSession(session);
		}
		return result;
	}

	public FilterConditionDTO getFilterConditionForId(final int id,
			final Collection<FilterConditionDTO> allFilterConditions) {
		for (final FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition.getIFilterConditionID() == id) {
				return filterCondition;
			}
		}
		return null;
	}

	private Session openNewSession() throws HibernateException {
		final Session result = this.sessionFactory.openSession();
		result.setCacheMode(CacheMode.IGNORE);
		result.setFlushMode(FlushMode.COMMIT);

		return result;
	}

	public void prepareSynchonization() throws StorageError, StorageException,
			InconsistentConfigurationException {
		// TODO Hier die Syn-Tabellen anlegen / Datgen kopieren / GGf. über ein
		// HSQL-Statement.
		throw new UnsupportedOperationException("not implemented yet.");
	}

	private void pruefeUndOrdnerFilterDieFilterConditionsZu(
			final Collection<FilterConditionsToFilterDTO> allFilterConditionToFilter,
			final Collection<FilterConditionDTO> allFilterConditions,
			final Collection<FilterDTO> allFilters) {
		final Map<Integer, FilterDTO> filters = new HashMap<Integer, FilterDTO>();
		for (final FilterDTO filter : allFilters) {
			final List<FilterConditionDTO> list = filter.getFilterConditions();
			list.clear();
			filter.setFilterConditions(list);
			filters.put(filter.getIFilterID(), filter);
		}
		for (final FilterConditionsToFilterDTO filterConditionsToFilterDTO : allFilterConditionToFilter) {
			final FilterDTO filterDTO = filters.get(filterConditionsToFilterDTO
					.getIFilterRef());
			if (filterDTO == null) {
				this.logger.logWarningMessage(this, "no filter found for id: "
						+ filterConditionsToFilterDTO.getIFilterRef());
			} else {
				final List<FilterConditionDTO> filterConditions = filterDTO
						.getFilterConditions();
				filterConditions.add(this.getFilterConditionForId(
						filterConditionsToFilterDTO.getIFilterConditionRef(),
						allFilterConditions));
				filterDTO.setFilterConditions(filterConditions);
			}
		}
	}

	public AlarmbearbeiterDTO saveAlarmbearbeiterDTO(
			final AlarmbearbeiterDTO alarmbearbeiterDTO) throws StorageError,
			StorageException, InconsistentConfigurationException {
		this.saveDTO(alarmbearbeiterDTO);
		return alarmbearbeiterDTO;
	}

	public AlarmbearbeiterGruppenDTO saveAlarmbearbeiterGruppenDTO(
			final AlarmbearbeiterGruppenDTO dto)
			throws InconsistentConfigurationException {

		Transaction tx = null;
		Session session = null;
		try {
			session = this.openNewSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(dto);

			final Collection<User2UserGroupDTO> user2GroupMappings = session
					.createCriteria(User2UserGroupDTO.class).list();

			for (final User2UserGroupDTO a : user2GroupMappings) {
				if (a.getUser2UserGroupPK().getIUserGroupRef() == dto
						.getUserGroupId()) {
					session.delete(a);
				}
			}
			// for all used FC
			// get the mappingDTO, if no DTO exists, create one
			final Set<User2UserGroupDTO> zugehoerigeAlarmbearbeiter = dto
					.gibZugehoerigeAlarmbearbeiter();

			// save the used Mappings
			for (final User2UserGroupDTO map : zugehoerigeAlarmbearbeiter) {
				session.save(map);
			}
			tx.commit();
		} catch (final Throwable e) {
			tx.rollback();
			throw new InconsistentConfigurationException(e.getMessage());
		} finally {
			closeSession(session);
		}
		return dto;
	}

	public void saveCurrentReplicationState(
			final ReplicationStateDTO currentState) throws StorageError,
			StorageException, UnknownConfigurationElementError {
		Transaction newTransaction = null;
		Session session = null;
		try {
			session = this.openNewSession();
			newTransaction = session.beginTransaction();
			session.saveOrUpdate(currentState);
			newTransaction.commit();
			session.close();
		} catch (final Throwable t) {
			if (newTransaction != null) {
				newTransaction.rollback();
			}
			throw new StorageException("unable to save replication state", t);
		} finally {
			closeSession(session);
		}
	}

	public void saveDTO(final NewAMSConfigurationElementDTO dto)
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		Transaction transaction = null;
		Session session = null;
		try {
			session = this.openNewSession();
			transaction = session.beginTransaction();
			this.saveDTONoTransaction(session, dto);
			if (dto instanceof StringArrayFilterConditionDTO) {
				final StringArrayFilterConditionDTO filterDto = (StringArrayFilterConditionDTO) dto;
				final List<StringArrayFilterConditionCompareValuesDTO> list = session
						.createCriteria(
								StringArrayFilterConditionCompareValuesDTO.class)
						.list();
				for (final StringArrayFilterConditionCompareValuesDTO stringArrayFilterConditionCompareValuesDTO : list) {
					if (filterDto.getIFilterConditionID() == stringArrayFilterConditionCompareValuesDTO
							.getFilterConditionRef()) {
						session
								.delete(stringArrayFilterConditionCompareValuesDTO);
					}
				}
				for (final StringArrayFilterConditionCompareValuesDTO compValue : filterDto
						.getCompareValueList()) {
					compValue.setPK(filterDto.getIFilterConditionID());
					session.saveOrUpdate(compValue);
				}
			}
			transaction.commit();
		} catch (final Throwable t) {
			if (transaction != null) {
				transaction.rollback();
			}
			throw new StorageException(
					"failed to save configuration element of type "
							+ dto.getClass().getSimpleName(), t);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * Für interne Verwendung innerhalb einer Transaction, da Transaction in
	 * JDBC nicht verschachtelt werden dürfen. ACHTUNG: Führt KEIN
	 * {@link org.hibernate.Session#flush()} aus!
	 * 
	 * @see {@link #saveDTO(NewAMSConfigurationElementDTO)}
	 */
	protected void saveDTONoTransaction(final Session session,
			final NewAMSConfigurationElementDTO dto) throws Throwable {
		session.saveOrUpdate(dto);

		if (dto instanceof HasJoinedElements) {
			((HasJoinedElements<?>) dto).storeJoinLinkData(session);
		}
	}

	@SuppressWarnings("unchecked")
	public FilterDTO saveFilterDTO(final FilterDTO dto) throws StorageError,
			StorageException, InconsistentConfigurationException {
		Session session = null;
		Transaction tx = null;
		try {
			session = this.openNewSession();
			tx = session.beginTransaction();

			session.saveOrUpdate(dto);

			final List<FilterConditionDTO> filterConditions = dto
					.getFilterConditions();

			final Set<FilterConditionsToFilterDTO> zuSpeicherndeJoins = new HashSet<FilterConditionsToFilterDTO>();

			// erzeugen der zu schreibenden join daten
			for (final FilterConditionDTO filterConditionDTO : filterConditions) {
				final FilterConditionsToFilterDTO joinData = new FilterConditionsToFilterDTO();
				joinData.setIFilterRef(dto.getIFilterID());
				joinData.setIFilterConditionRef(filterConditionDTO
						.getIFilterConditionID());

				zuSpeicherndeJoins.add(joinData);

			}

			// clean up join data
			final Collection<FilterConditionsToFilterDTO> conditionMappings = session
					.createCriteria(FilterConditionsToFilterDTO.class).list();

			for (final FilterConditionsToFilterDTO joinElement : conditionMappings) {
				if (joinElement.getIFilterRef() == dto.getIFilterID()) {
					final int filterConditionRef = joinElement
							.getIFilterConditionRef();

					if (!zuSpeicherndeJoins.contains(joinElement)) {
						session.delete(joinElement); // nicht mehr verwendete
						// Knoten löschen
					}

					final Collection<JunctorConditionForFilterTreeDTO> junctorConditions = session
							.createCriteria(
									JunctorConditionForFilterTreeDTO.class)
							.add(Restrictions.idEq(filterConditionRef)).list();
					if ((junctorConditions != null)
							&& (junctorConditions.size() > 0)) {
						for (final JunctorConditionForFilterTreeDTO junctorConditionForFilterTreeDTO : junctorConditions) {
							if (!filterConditions
									.contains(junctorConditionForFilterTreeDTO)) {
								this.deleteDTONoTransaction(session,
										junctorConditionForFilterTreeDTO);
							}
						}
					}

					final Collection<NegationConditionForFilterTreeDTO> notConditions = session
							.createCriteria(
									NegationConditionForFilterTreeDTO.class)
							.add(Restrictions.idEq(filterConditionRef)).list();
					if ((notConditions != null) && (notConditions.size() > 0)) {
						for (final NegationConditionForFilterTreeDTO notConditionForFilterTreeDTO : notConditions) {
							if (!filterConditions
									.contains(notConditionForFilterTreeDTO)) {
								this.deleteDTONoTransaction(session,
										notConditionForFilterTreeDTO);
							}
						}
					}
				}
			}

			// join speichern
			for (final FilterConditionDTO filterConditionDTO : filterConditions) {
				if ((filterConditionDTO instanceof JunctorConditionForFilterTreeDTO)
						|| (filterConditionDTO instanceof NegationConditionForFilterTreeDTO)) {
					// Diese Condition speichern, da sie von Editor angelegt
					// wird.
					this.saveDTONoTransaction(session, filterConditionDTO);
				}

				final FilterConditionsToFilterDTO joinData = new FilterConditionsToFilterDTO();
				joinData.setIFilterRef(dto.getIFilterID());
				joinData.setIFilterConditionRef(filterConditionDTO
						.getIFilterConditionID());

				if (!zuSpeicherndeJoins.contains(joinData)) {
					this.saveDTONoTransaction(session, joinData);
				}
			}

			tx.commit();
		} catch (final Throwable e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			throw new InconsistentConfigurationException(e.getMessage());
		} finally {
			closeSession(session);
		}
		return dto;
	}

	public void saveHistoryDTO(final HistoryDTO historyDTO)
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		Transaction newTransaction = null;
		Session session = null;
		try {
			session = this.openNewSession();
			newTransaction = session.beginTransaction();
			session.saveOrUpdate(historyDTO);
			newTransaction.commit();
			session.close();
		} catch (final Throwable t) {
			if (newTransaction != null) {
				newTransaction.rollback();
			}
			throw new StorageException("unable to save history element", t);
		} finally {
			closeSession(session);
		}
	}

	public RubrikDTO saveRubrikDTO(final RubrikDTO dto) throws StorageError,
			StorageException, InconsistentConfigurationException {
		this.saveDTO(dto);
		return dto;
	}

	public TopicDTO saveTopicDTO(final TopicDTO topicDTO) throws StorageError,
			StorageException, InconsistentConfigurationException {
		this.saveDTO(topicDTO);
		return topicDTO;

	}

	private void setChildFilterConditionsInJunctorDTOs(
			final Collection<FilterConditionDTO> allFilterConditions) {
		for (final FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition instanceof JunctorConditionDTO) {
				final JunctorConditionDTO junctorConditionDTO = (JunctorConditionDTO) filterCondition;
				final FilterConditionDTO firstFilterCondition = this
						.getFilterConditionForId(junctorConditionDTO
								.getFirstFilterConditionRef(),
								allFilterConditions);
				final FilterConditionDTO secondFilterCondition = this
						.getFilterConditionForId(junctorConditionDTO
								.getSecondFilterConditionRef(),
								allFilterConditions);

				junctorConditionDTO
						.setFirstFilterCondition(firstFilterCondition);
				junctorConditionDTO
						.setSecondFilterCondition(secondFilterCondition);
			}
		}
	}

	private void setStringArrayCompareValues(
			final Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues,
			final Collection<FilterConditionDTO> allFilterConditions) {
		final Map<Integer, StringArrayFilterConditionDTO> stringAFC = new HashMap<Integer, StringArrayFilterConditionDTO>();
		for (final FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition instanceof StringArrayFilterConditionDTO) {
				stringAFC.put(filterCondition.getIFilterConditionID(),
						(StringArrayFilterConditionDTO) filterCondition);
				final StringArrayFilterConditionDTO sFC = (StringArrayFilterConditionDTO) filterCondition;
				sFC
						.setCompareValues(new LinkedList<StringArrayFilterConditionCompareValuesDTO>());
			}
		}
		for (final StringArrayFilterConditionCompareValuesDTO stringArrayFilterConditionCompareValuesDTO : allCompareValues) {
			final StringArrayFilterConditionDTO conditionDTO = stringAFC
					.get(stringArrayFilterConditionCompareValuesDTO
							.getFilterConditionRef());
			final List<StringArrayFilterConditionCompareValuesDTO> list = conditionDTO
					.getCompareValueList();
			list.add(stringArrayFilterConditionCompareValuesDTO);
			conditionDTO.setCompareValues(list);
		}

	}

}
