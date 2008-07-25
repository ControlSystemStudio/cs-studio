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
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.DefaultFilterTextDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;
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

		transactionProcessor = new TransactionProcessor(sessionFactory, logger);
	}

	public void deleteAlarmbearbeiterGruppenDTO(
			final AlarmbearbeiterGruppenDTO dto) throws StorageError,
			StorageException, InconsistentConfigurationException {
		Session session = null;
		Transaction tx = null;
		try {
			session = this.openNewSession();
			tx = session.beginTransaction();
			tx.begin();
			final Collection<User2UserGroupDTO> user2GroupMappings = loadAll(
					session, User2UserGroupDTO.class);

			for (final User2UserGroupDTO a : user2GroupMappings) {
				if (a.getUser2UserGroupPK().getIUserGroupRef() == dto
						.getUserGroupId()) {
					deleteDTONoTransaction(session, a);
				}
			}
			deleteDTONoTransaction(session, dto);
			tx.commit();
		} catch (final Throwable e) {
			new InconsistentConfigurationException("Could not delete " + dto);
		} finally {
			closeSession(session);
		}
	}

	public void deleteDTO(final NewAMSConfigurationElementDTO dto)
			throws StorageError, StorageException,
			InconsistentConfigurationException {
		Transaction transaction = null;
		Session session = null;
		try {
			session = this.openNewSession();

			transaction = session.beginTransaction();
			transaction.begin();
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

	public void deleteFilterDTO(final FilterDTO dto)
			throws InconsistentConfigurationException, StorageError,
			StorageException {
		// FIXME fertigstellen: Die Joins/OR/AND/NOTS...
		this.deleteDTO(dto);
	}

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

		UnitOfWork<Configuration> loadEntireConfigurationWork = new UnitOfWork<Configuration>() {
			public Configuration doWork(Mapper mapper) throws Throwable {
				Configuration resultOfUnit = null;

				Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
				Collection<TopicDTO> alleAlarmtopics;
				Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
				Collection<FilterDTO> allFilters;
				Collection<FilterConditionsToFilterDTO> allFilterConditionMappings;
				Collection<FilterConditionDTO> allFilterConditions;
				Collection<RubrikDTO> alleRubriken;
				List<User2UserGroupDTO> alleUser2UserGroupMappings;
				Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues;
				Collection<DefaultFilterTextDTO> allDefaultFilterTextDTO;

				alleAlarmbarbeiter = mapper.loadAll(AlarmbearbeiterDTO.class, true);
				alleAlarmbearbeiterGruppen = mapper
						.loadAll(AlarmbearbeiterGruppenDTO.class, true);
				alleAlarmtopics = mapper.loadAll(TopicDTO.class, true);
				allFilters = mapper.loadAll(FilterDTO.class, true);
				allFilterConditions = mapper.loadAll(FilterConditionDTO.class, true);
				alleRubriken = mapper.loadAll(RubrikDTO.class, true);
				alleUser2UserGroupMappings = mapper
						.loadAll(User2UserGroupDTO.class, true);
				allFilterConditionMappings = mapper
						.loadAll(FilterConditionsToFilterDTO.class, true);
				allCompareValues = mapper
						.loadAll(StringArrayFilterConditionCompareValuesDTO.class, true);
				allDefaultFilterTextDTO = mapper.loadAll(DefaultFilterTextDTO.class, true);

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

				setChildFilterConditionsInJunctorDTOs(allFilterConditions);

				pruefeUndOrdnerFilterDieFilterConditionsZu(
						allFilterConditionMappings, allFilterConditions,
						allFilters);


				setStringArrayCompareValues(allCompareValues,
						allFilterConditions);

				final Collection<FilterConditionDTO> allFCs = new HashSet<FilterConditionDTO>(
						allFilterConditions);

				for (final FilterConditionDTO fc : allFCs) {
					if (fc instanceof JunctorConditionForFilterTreeDTO) {
						final JunctorConditionForFilterTreeDTO jcfft = (JunctorConditionForFilterTreeDTO) fc;
						try {
							jcfft.loadJoinData(mapper);
						} catch (final Throwable e) {
							logger.logErrorMessage(this, "unable to load joined conditions of JunctionConditionForFilters",
									e);
							throw new InconsistentConfigurationException(
									"unable to load joined conditions of JunctionConditionForFilters",
									e);
						}
					}
				}

				addUsersToGroups(alleAlarmbearbeiterGruppen,
						alleUser2UserGroupMappings, logger);

				resultOfUnit = new Configuration(alleAlarmbarbeiter,
						alleAlarmtopics, alleAlarmbearbeiterGruppen,
						allFilters, allFilterConditionMappings,
						allFilterConditions, alleRubriken,
						alleUser2UserGroupMappings, allCompareValues, 
						allDefaultFilterTextDTO);

				return resultOfUnit;
			}
		};

		try {
			result = this.transactionProcessor
					.doInTransaction(loadEntireConfigurationWork);
		} catch (InterruptedException e) {
			logger.logWarningMessage(this,
					"Load of entire configuration interrupted", e);
			throw new StorageException(
					"Load of entire configuration interrupted", e);
		}

		// Transaction transaction = null;
		// Session outerSession = null;
		// try {
		// final Session session = this.openNewSession();
		// outerSession = session;
		//
		// transaction = session.beginTransaction();
		// transaction.begin();
		// 
		// Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
		// Collection<TopicDTO> alleAlarmtopics;
		// Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
		// Collection<FilterDTO> allFilters;
		// Collection<FilterConditionsToFilterDTO> allFilterConditionMappings;
		//
		// Collection<FilterConditionDTO> allFilterConditions;
		// Collection<RubrikDTO> alleRubriken;
		// List<User2UserGroupDTO> alleUser2UserGroupMappings;
		// Collection<StringArrayFilterConditionCompareValuesDTO>
		// allCompareValues;
		//
		// // PUBLICs
		// alleAlarmbarbeiter = loadAll(session, AlarmbearbeiterDTO.class);
		// alleAlarmbearbeiterGruppen = loadAll(session,
		// AlarmbearbeiterGruppenDTO.class);

		// alleAlarmtopics = loadAll(session, TopicDTO.class);
		// allFilters = loadAll(session, FilterDTO.class);
		//
		// allFilterConditions = loadAll(session, FilterConditionDTO.class);

		// // Nots befüllen
		// for (final FilterConditionDTO filterConditionDTO :
		// allFilterConditions) {
		// if (filterConditionDTO instanceof NegationConditionForFilterTreeDTO)
		// {
		// final NegationConditionForFilterTreeDTO notCond =
		// (NegationConditionForFilterTreeDTO) filterConditionDTO;
		// for (final FilterConditionDTO possibleCondition :
		// allFilterConditions) {
		// if (notCond.getINegatedFCRef() == possibleCondition
		// .getIFilterConditionID()) {
		// notCond
		// .setNegatedFilterCondition(possibleCondition);
		// break;
		// }
		// }
		// }
		// }

		// alleRubriken = loadAll(session, RubrikDTO.class);
		//
		// alleUser2UserGroupMappings = loadAll(session,
		// User2UserGroupDTO.class);
		//
		// allFilterConditionMappings = loadAll(session,
		// FilterConditionsToFilterDTO.class);
		// this
		// .pruefeUndOrdnerFilterDieFilterConditionsZu(
		// allFilterConditionMappings, allFilterConditions,
		// allFilters);
		// this.setChildFilterConditionsInJunctorDTOs(allFilterConditions);
		// allCompareValues = loadAll(session,
		// StringArrayFilterConditionCompareValuesDTO.class);
		// this.setStringArrayCompareValues(allCompareValues,
		// allFilterConditions);

		// final Collection<FilterConditionDTO> allFCs = new
		// HashSet<FilterConditionDTO>(
		// allFilterConditions);
		// for (final FilterConditionDTO fc : allFCs) {
		// if (fc instanceof JunctorConditionForFilterTreeDTO) {
		// final JunctorConditionForFilterTreeDTO jcfft =
		// (JunctorConditionForFilterTreeDTO) fc;
		// try {
		// jcfft.loadJoinData(new Mapper() {
		//
		// public void delete(
		// NewAMSConfigurationElementDTO element)
		// throws Throwable {
		// deleteDTONoTransaction(session, element);
		// }
		//
		// @SuppressWarnings("unchecked")
		// public <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
		// Class<T> clasz) throws Throwable {
		// if( FilterConditionDTO.class.isAssignableFrom(clasz)) {
		// return (List<T>) Arrays.asList(allFCs.toArray());
		// }
		//								
		// return Collections.emptyList();
		// }
		//
		// public void save(
		// NewAMSConfigurationElementDTO element)
		// throws Throwable {
		// saveDTONoTransaction(session, element);
		// }
		//							
		// });
		// } catch (final Throwable e) {
		// throw new InconsistentConfigurationException(
		// "unable to load joined conditions of JunctionConditionForFilters",
		// e);
		// }
		// }
		// }
		// this.addUsersToGroups(session, alleAlarmbearbeiterGruppen,
		// alleUser2UserGroupMappings);

		//
		// result = new Configuration(alleAlarmbarbeiter, alleAlarmtopics,
		// alleAlarmbearbeiterGruppen, allFilters,
		// allFilterConditionMappings, allFilterConditions,
		// alleRubriken, alleUser2UserGroupMappings, allCompareValues);
		// transaction.commit();
		// } catch (final Throwable t) {
		// if (transaction != null) {
		// transaction.rollback();
		// }
		// t.printStackTrace();
		// } finally {
		// closeSession(outerSession);
		// }
		return result;
	}

	public static FilterConditionDTO getFilterConditionForId(final int id,
			final Collection<FilterConditionDTO> allFilterConditions) {
		for (final FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition.getIFilterConditionID() == id) {
				return filterCondition;
			}
		}
		return null;
	}

	public void prepareSynchonization() throws StorageError, StorageException,
			InconsistentConfigurationException {
		// TODO Hier die Syn-Tabellen anlegen / Datgen kopieren / GGf. über ein
		// HSQL-Statement.
		throw new UnsupportedOperationException("not implemented yet.");
	}

	public AlarmbearbeiterGruppenDTO saveAlarmbearbeiterGruppenDTO(
			final AlarmbearbeiterGruppenDTO dto)
			throws InconsistentConfigurationException {

		Transaction tx = null;
		Session session = null;
		try {
			session = this.openNewSession();
			tx = session.beginTransaction();
			tx.begin();
			session.saveOrUpdate(dto);

			final Collection<User2UserGroupDTO> user2GroupMappings = loadAll(
					session, User2UserGroupDTO.class);

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
			for (final User2UserGroupDTO user2userGroup : zugehoerigeAlarmbearbeiter) {
				saveDTONoTransaction(session, user2userGroup);
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
			newTransaction.begin();
			session.saveOrUpdate(currentState);
			newTransaction.commit();
		} catch (final Throwable t) {
			if (newTransaction != null) {
				newTransaction.rollback();
			}
			throw new StorageException("unable to save replication state", t);
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
			Session session, Class<T> clasz) {
		List<T> list = session.createCriteria(clasz).list();

		return list;
	}

	public void saveDTO(final NewAMSConfigurationElementDTO dto)
			throws StorageError, StorageException,
			InconsistentConfigurationException {

		UnitOfWork<NewAMSConfigurationElementDTO> saveWork = new UnitOfWork<NewAMSConfigurationElementDTO>() {
			public NewAMSConfigurationElementDTO doWork(Mapper mapper)
					throws Throwable {

				mapper.save(dto); // performs "deep" save

				return dto;
			}
		};

		try {
			this.transactionProcessor.doInTransaction(saveWork);
		} catch (InterruptedException e) {
			logger.logWarningMessage(this, "save has been interrupted", e);
			throw new StorageException("save has been interrupted", e);
		}

		// Transaction transaction = null;
		// Session session = null;
		// try {
		// session = this.openNewSession();
		// transaction = session.beginTransaction();
		// transaction.begin();
		//			
		// this.saveDTONoTransaction(session, dto);
		// if (dto instanceof StringArrayFilterConditionDTO) {
		// final StringArrayFilterConditionDTO filterDto =
		// (StringArrayFilterConditionDTO) dto;
		// final List<StringArrayFilterConditionCompareValuesDTO> list =
		// loadAll(
		// session,
		// StringArrayFilterConditionCompareValuesDTO.class);
		// for (final StringArrayFilterConditionCompareValuesDTO
		// stringArrayFilterConditionCompareValuesDTO : list) {
		// if (filterDto.getIFilterConditionID() ==
		// stringArrayFilterConditionCompareValuesDTO
		// .getFilterConditionRef()) {
		// deleteDTONoTransaction(session,
		// stringArrayFilterConditionCompareValuesDTO);
		// }
		// }
		// for (final StringArrayFilterConditionCompareValuesDTO compValue :
		// filterDto
		// .getCompareValueList()) {
		// compValue.setPK(filterDto.getIFilterConditionID());
		// saveDTONoTransaction(session, compValue);
		// }
		// }
		// transaction.commit();
		// } catch (final Throwable t) {
		// if (transaction != null) {
		// transaction.rollback();
		// }
		// throw new StorageException(
		// "failed to save configuration element of type "
		// + dto.getClass().getSimpleName(), t);
		// } finally {
		// closeSession(session);
		// }
	}

	public FilterDTO saveFilterDTO(final FilterDTO dto) throws StorageError,
			StorageException, InconsistentConfigurationException {
		saveDTO(dto);
		
//		Session session = null;
//		Transaction tx = null;
//		try {
//			session = this.openNewSession();
//			tx = session.beginTransaction();
//			tx.begin();
//
//			saveDTONoTransaction(session, dto);
//
//			final List<FilterConditionDTO> filterConditions = dto
//					.getFilterConditions();
//
//			final Set<FilterConditionsToFilterDTO> zuSpeicherndeJoins = new HashSet<FilterConditionsToFilterDTO>();
//
//			// erzeugen der zu schreibenden join daten
//			for (final FilterConditionDTO filterConditionDTO : filterConditions) {
//				final FilterConditionsToFilterDTO joinData = new FilterConditionsToFilterDTO();
//				joinData.setIFilterRef(dto.getIFilterID());
//				joinData.setIFilterConditionRef(filterConditionDTO
//						.getIFilterConditionID());
//
//				zuSpeicherndeJoins.add(joinData);
//
//			}
//
//			// clean up join data
//			final Collection<FilterConditionsToFilterDTO> conditionMappings = loadAll(
//					session, FilterConditionsToFilterDTO.class);
//
//			for (final FilterConditionsToFilterDTO joinElement : conditionMappings) {
//				if (joinElement.getIFilterRef() == dto.getIFilterID()) {
//					final int filterConditionRef = joinElement
//							.getIFilterConditionRef();
//
//					if (!zuSpeicherndeJoins.contains(joinElement)) {
//						deleteDTONoTransaction(session, joinElement); // nicht
//						// mehr
//						// verwendete
//						// Knoten löschen
//					}
//
//					final Collection<JunctorConditionForFilterTreeDTO> junctorConditions = loadAll(
//							session, JunctorConditionForFilterTreeDTO.class);
//					if ((junctorConditions != null)
//							&& (junctorConditions.size() > 0)) {
//						for (final JunctorConditionForFilterTreeDTO junctorConditionForFilterTreeDTO : junctorConditions) {
//							if (junctorConditionForFilterTreeDTO
//									.getIFilterConditionID() != filterConditionRef) {
//								continue;
//							}
//
//							if (!filterConditions
//									.contains(junctorConditionForFilterTreeDTO)) {
//								this.deleteDTONoTransaction(session,
//										junctorConditionForFilterTreeDTO);
//							}
//						}
//					}
//
//					final Collection<NegationConditionForFilterTreeDTO> notConditions = loadAll(
//							session, NegationConditionForFilterTreeDTO.class);
//					if ((notConditions != null) && (notConditions.size() > 0)) {
//						for (final NegationConditionForFilterTreeDTO notConditionForFilterTreeDTO : notConditions) {
//							if (notConditionForFilterTreeDTO
//									.getIFilterConditionID() != filterConditionRef) {
//								continue;
//							}
//
//							if (!filterConditions
//									.contains(notConditionForFilterTreeDTO)) {
//								this.deleteDTONoTransaction(session,
//										notConditionForFilterTreeDTO);
//							}
//						}
//					}
//				}
//			}
//
//			// join speichern
//			for (final FilterConditionDTO filterConditionDTO : filterConditions) {
//				if ((filterConditionDTO instanceof JunctorConditionForFilterTreeDTO)
//						|| (filterConditionDTO instanceof NegationConditionForFilterTreeDTO)) {
//					// Diese Condition speichern, da sie von Editor angelegt
//					// wird.
//					this.saveDTONoTransaction(session, filterConditionDTO);
//				}
//
//				final FilterConditionsToFilterDTO joinData = new FilterConditionsToFilterDTO();
//				joinData.setIFilterRef(dto.getIFilterID());
//				joinData.setIFilterConditionRef(filterConditionDTO
//						.getIFilterConditionID());
//
//				if (!zuSpeicherndeJoins.contains(joinData)) {
//					this.saveDTONoTransaction(session, joinData);
//				}
//			}
//
//			tx.commit();
//		} catch (final Throwable e) {
//			e.printStackTrace();
//			if (tx != null) {
//				tx.rollback();
//			}
//			throw new InconsistentConfigurationException(e.getMessage());
//		} finally {
//			closeSession(session);
//		}
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
			newTransaction.begin();
			session.saveOrUpdate(historyDTO);
			newTransaction.commit();
		} catch (final Throwable t) {
			if (newTransaction != null) {
				newTransaction.rollback();
			}
			throw new StorageException("unable to save history element", t);
		} finally {
			closeSession(session);
		}
	}

	void deleteDTONoTransaction(final Session session,
			final NewAMSConfigurationElementDTO dto) throws Throwable {

		if (dto instanceof HasManuallyJoinedElements) {
			((HasManuallyJoinedElements) dto).deleteJoinLinkData(new Mapper() {

				public void delete(NewAMSConfigurationElementDTO element)
						throws Throwable {
					deleteDTONoTransaction(session, element);
				}

				public <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
						Class<T> clasz, boolean loadManuallyJoinedMappingsIfAvailable) throws Throwable {
					List<T> allLoaded = LocalStoreConfigurationServiceImpl.this
							.loadAll(session, clasz);
					allLoaded.remove(dto); // Muss raus sonst landen wir immer
											// wieder hier! ;)
					return allLoaded;
				}

				public void save(NewAMSConfigurationElementDTO element)
						throws Throwable {
					saveDTONoTransaction(session, element);
				}

			});
		}

		session.delete(dto);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.sessionFactory.close();
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

		if (dto instanceof HasManuallyJoinedElements) {
			((HasManuallyJoinedElements) dto).storeJoinLinkData(new Mapper() {

				public <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
						Class<T> clasz, boolean loadManuallyJoinedMappingsIfAvailable) throws Throwable {
					List<T> allLoaded = LocalStoreConfigurationServiceImpl.this
							.loadAll(session, clasz);
					allLoaded.remove(dto); // Muss raus, sonst landen wir immer
											// wieder hier! ;)
					return allLoaded;
				}

				public void save(NewAMSConfigurationElementDTO element)
						throws Throwable {
					saveDTONoTransaction(session, element);
				}

				public void delete(NewAMSConfigurationElementDTO element)
						throws Throwable {
					deleteDTONoTransaction(session, element);
				}

			});
		}
	}

	private static void addUsersToGroups(
			final Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen,
			final List<User2UserGroupDTO> alleUser2UserGroupMappings,
			Logger logger) {
		final HashMap<Integer, AlarmbearbeiterGruppenDTO> gruppen = new HashMap<Integer, AlarmbearbeiterGruppenDTO>();
		for (final AlarmbearbeiterGruppenDTO gruppe : alleAlarmbearbeiterGruppen) {
			gruppen.put(gruppe.getUserGroupId(), gruppe);
		}
		for (final User2UserGroupDTO map : alleUser2UserGroupMappings) {
			try {
				gruppen.get(map.getUser2UserGroupPK().getIUserGroupRef())
						.alarmbearbeiterZuordnen(map);
			} catch (final NullPointerException npe) {

				// logger.logErrorMessage(this,
				// "Contains invalid User To UserGroup mapping, group "
				// + map.getUser2UserGroupPK()
				// .getIUserGroupRef()
				// + " doesn't exist", npe);

			}
		}
	}

	private Session sessionWorkingOn = null;
	private TransactionProcessor transactionProcessor;

	private void closeSession(Session session) throws HibernateException {
		if (session != null && session.isOpen()) {
			try {
				session.flush();
				// session.close();
			} catch (final HibernateException he) {
				sessionWorkingOn.close();
				sessionWorkingOn = null;
				throw new StorageError("session could not be closed", he);
			}
		}
	}

	private Session openNewSession() throws HibernateException {
		Session result = null;
		if (sessionWorkingOn == null) {
			result = this.sessionFactory.openSession();
			result.setCacheMode(CacheMode.IGNORE);
			result.setFlushMode(FlushMode.COMMIT);
		} else {
			result = sessionWorkingOn;
		}
		return result;
	}

	private static <T extends FilterConditionDTO> T findForId(int id, Collection<T> fcs) {
		for (T t : fcs) {
			if( t.getIFilterConditionID() == id ) {
				return t;
			}
		}
		return null;
	}
	private static <T extends FilterConditionsToFilterDTO> Collection<T> findAssignmentToFilterForFilterId(int id, Collection<T> fcs) {
		Collection<T> result = new HashSet<T>();
		
		for (T t : fcs) {
			if( t.getIFilterRef() == id ) {
				result.add(t);
			}
		}
		return result;
	}
	
	private static void pruefeUndOrdnerFilterDieFilterConditionsZu(
			final Collection<FilterConditionsToFilterDTO> allFilterConditionToFilter,
			final Collection<FilterConditionDTO> allFilterConditions,
			final Collection<FilterDTO> allFilters) {
		
		for (FilterDTO filterDTO : allFilters) {
			Collection<FilterConditionsToFilterDTO> zuordnungenDiesesFilters = findAssignmentToFilterForFilterId(filterDTO.getIFilterID(), allFilterConditionToFilter);
			
			List<FilterConditionDTO> operanden = new LinkedList<FilterConditionDTO>();
			for (FilterConditionsToFilterDTO zuorndungen : zuordnungenDiesesFilters) {
				operanden.add(findForId(zuorndungen.getIFilterConditionRef(), allFilterConditions));
			}
			filterDTO.setFilterConditions(operanden);
		}
		
//		
//		
//		final Map<Integer, FilterDTO> filters = new HashMap<Integer, FilterDTO>();
//		for (final FilterDTO filter : allFilters) {
//			final List<FilterConditionDTO> list = filter.getFilterConditions();
//			list.clear();
//			filter.setFilterConditions(list);
//			filters.put(filter.getIFilterID(), filter);
//		}
//		for (final FilterConditionsToFilterDTO filterConditionsToFilterDTO : allFilterConditionToFilter) {
//			final FilterDTO filterDTO = filters.get(filterConditionsToFilterDTO
//					.getIFilterRef());
//			if (filterDTO == null) {
//				// this.logger.logWarningMessage(this, "no filter found for id:
//				// "
//				// + filterConditionsToFilterDTO.getIFilterRef());
//			} else {
//				final List<FilterConditionDTO> filterConditions = filterDTO
//						.getFilterConditions();
//				filterConditions.add(getFilterConditionForId(
//						filterConditionsToFilterDTO.getIFilterConditionRef(),
//						allFilterConditions));
//				filterDTO.setFilterConditions(filterConditions);
//			}
//		}
	}

	private static void setChildFilterConditionsInJunctorDTOs(
			final Collection<FilterConditionDTO> allFilterConditions) {
		for (final FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition instanceof JunctorConditionDTO) {
				final JunctorConditionDTO junctorConditionDTO = (JunctorConditionDTO) filterCondition;
				final FilterConditionDTO firstFilterCondition = getFilterConditionForId(
						junctorConditionDTO.getFirstFilterConditionRef(),
						allFilterConditions);
				final FilterConditionDTO secondFilterCondition = getFilterConditionForId(
						junctorConditionDTO.getSecondFilterConditionRef(),
						allFilterConditions);

				junctorConditionDTO
						.setFirstFilterCondition(firstFilterCondition);
				junctorConditionDTO
						.setSecondFilterCondition(secondFilterCondition);
			}
		}
	}

	private static void setStringArrayCompareValues(
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
