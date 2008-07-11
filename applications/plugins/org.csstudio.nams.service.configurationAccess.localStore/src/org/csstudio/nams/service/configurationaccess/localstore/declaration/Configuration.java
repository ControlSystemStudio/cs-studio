package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.RubrikDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.AlarmbearbeiterZuAlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionCompareValuesDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@Entity
public class Configuration implements FilterConditionForIdProvider {
	private Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private Collection<TopicDTO> alleAlarmtopics;
	private Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
	private Collection<FilterDTO> allFilters;
	private Collection<FilterConditionsToFilterDTO> allFilterConditionMappings;

	private Collection<FilterConditionDTO> allFilterConditions;
	private Collection<RubrikDTO> alleRubriken;

	@SuppressWarnings("unchecked")
	public Configuration(Session session)
			throws InconsistentConfigurationException, StorageError,
			StorageException {

		// PUBLICs
		alleAlarmbarbeiter = session.createCriteria(AlarmbearbeiterDTO.class)
				.addOrder(Order.asc("userId")).list();
		alleAlarmbearbeiterGruppen = session.createCriteria(
				AlarmbearbeiterGruppenDTO.class).addOrder(
				Order.asc("userGroupId")).list();

		alleAlarmtopics = session.createCriteria(TopicDTO.class).list();
		allFilters = session.createCriteria(FilterDTO.class).list();

		allFilterConditions = session.createCriteria(FilterConditionDTO.class)
				.addOrder(Order.asc("iFilterConditionID")).list();
		alleRubriken = session.createCriteria(RubrikDTO.class).list();

		// PRIVATEs
		Collection<AlarmbearbeiterZuAlarmbearbeiterGruppenDTO> allUserUserGroupAggregation = session
				.createCriteria(
						AlarmbearbeiterZuAlarmbearbeiterGruppenDTO.class)
				.list();
		pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu(allUserUserGroupAggregation);

		// Collection<FilterConditionTypeDTO> allFilterConditionsTypes = session
		// .createCriteria(FilterConditionTypeDTO.class).list();
		// pruefeUndOrdneTypenDenFilterConditionsZu(allFilterConditionsTypes);

		allFilterConditionMappings = session.createCriteria(
				FilterConditionsToFilterDTO.class).list();
		pruefeUndOrdnerFilterDieFilterConditionsZu(allFilterConditionMappings);
		setChildFilterConditionsInJunctorDTOs();
		Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues = session
				.createCriteria(
						StringArrayFilterConditionCompareValuesDTO.class)
				.list();
		setStringArrayCompareValues(allCompareValues);
		
		Collection<FilterConditionDTO> allFCs = new HashSet<FilterConditionDTO>(allFilterConditions);
		for (FilterConditionDTO fc : allFCs) {
			if( fc instanceof JunctorConditionForFilterTreeDTO ) {
				JunctorConditionForFilterTreeDTO jcfft = (JunctorConditionForFilterTreeDTO)fc;
				try {
					jcfft.loadJoinData(session, allFCs);
				} catch (Throwable e) {
					throw new InconsistentConfigurationException("unable to load joined conditions of JunctionConditionForFilters", e);
				}
			}
		}
	}

	private void setStringArrayCompareValues(
			Collection<StringArrayFilterConditionCompareValuesDTO> allCompareValues) {
		Map<Integer, StringArrayFilterConditionDTO> stringAFC = new HashMap<Integer, StringArrayFilterConditionDTO>();
		for (FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition instanceof StringArrayFilterConditionDTO) {
				stringAFC.put(filterCondition.getIFilterConditionID(),
						(StringArrayFilterConditionDTO) filterCondition);
			}
		}
		for (StringArrayFilterConditionCompareValuesDTO stringArrayFilterConditionCompareValuesDTO : allCompareValues) {
			StringArrayFilterConditionDTO conditionDTO = stringAFC
					.get(stringArrayFilterConditionCompareValuesDTO
							.getFilterConditionRef());
			conditionDTO.getCompareValueList().add(
					stringArrayFilterConditionCompareValuesDTO.getCompValue());
		}

	}

	private void setChildFilterConditionsInJunctorDTOs() {
		for (FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition instanceof JunctorConditionDTO) {
				JunctorConditionDTO junctorConditionDTO = (JunctorConditionDTO) filterCondition;
				junctorConditionDTO.injectYourselfYourChildren(this);
			}
		}
	}

	private void pruefeUndOrdnerFilterDieFilterConditionsZu(
			Collection<FilterConditionsToFilterDTO> allFilterConditionToFilter) {
		Map<Integer, FilterDTO> filters = new HashMap<Integer, FilterDTO>();
		for (FilterDTO filter : allFilters) {
			List<FilterConditionDTO> list = filter.getFilterConditions();
			list.clear();
			filter.setFilterConditions(list);
			filters.put(filter.getIFilterID(), filter);
		}
		for (FilterConditionsToFilterDTO filterConditionsToFilterDTO : allFilterConditionToFilter) {
			FilterDTO filterDTO = filters.get(filterConditionsToFilterDTO
					.getIFilterRef());
			List<FilterConditionDTO> filterConditions = filterDTO
					.getFilterConditions();
			filterConditions
					.add(getFilterConditionForId(filterConditionsToFilterDTO
							.getIFilterConditionRef()));
			filterDTO.setFilterConditions(filterConditions);
		}
	}

	private void pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu(
			Collection<AlarmbearbeiterZuAlarmbearbeiterGruppenDTO> allUserUserGroupAggregation)
			throws InconsistentConfigurationException {
		System.out
				.println("Configuration.pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu()"
						+ allUserUserGroupAggregation.size());
		for (AlarmbearbeiterZuAlarmbearbeiterGruppenDTO alarmbearbeiterZuAlarmbearbeiterGruppe : allUserUserGroupAggregation) {
			System.out
					.println("Configuration.pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu()"
							+ alarmbearbeiterZuAlarmbearbeiterGruppe.toString());

			int alarmbearbeiterId = alarmbearbeiterZuAlarmbearbeiterGruppe
					.getUserRef();
			int alarmbearbeiterGruppenId = alarmbearbeiterZuAlarmbearbeiterGruppe
					.getUserGroupRef();
			for (AlarmbearbeiterGruppenDTO alarmbearbeiterGruppe : alleAlarmbearbeiterGruppen) {
				int gruppenId = alarmbearbeiterGruppe.getUserGroupId();

				if (gruppenId == alarmbearbeiterGruppenId) {
					for (AlarmbearbeiterDTO alarmbearbeiter : alleAlarmbarbeiter) {
						if (alarmbearbeiterId == alarmbearbeiter.getUserId()) {
							alarmbearbeiterGruppe
									.alarmbearbeiterZuordnen(alarmbearbeiter);
						}
					}
				}
			}
		}
	}

	/**
	 * @deprecated "FilterConditionType is a redundant information"
	 * @param allFilterConditionsTypes
	 * @throws InconsistentConfigurationException
	 */
	@Deprecated
	private void pruefeUndOrdneTypenDenFilterConditionsZu(
			Collection<FilterConditionTypeDTO> allFilterConditionsTypes)
			throws InconsistentConfigurationException {
		// TODO Hier die Typen den FC zuweisen (manuelles ManyToOne mapping!
	}

	public Collection<AlarmbearbeiterDTO> gibAlleAlarmbearbeiter() {
		return alleAlarmbarbeiter;
	}

	public Collection<TopicDTO> gibAlleAlarmtopics() {
		return alleAlarmtopics;
	}

	public Collection<AlarmbearbeiterGruppenDTO> gibAlleAlarmbearbeiterGruppen() {
		return alleAlarmbearbeiterGruppen;
	}

	/**
	 * Returns a list of all FilterDTO's
	 */
	public Collection<FilterDTO> gibAlleFilter() {
		return allFilters;
	}

	public Collection<FilterConditionDTO> gibAlleFilterConditions() {
		return allFilterConditions;
	}

	// -------------

	// public Collection<AlarmbearbeiterDTO>
	// gibAlarmbearbeiterFuerAlarmbearbeiterGruppe(AlarmbearbeiterGruppenDTO
	// gruppe) {
	// Collection<AlarmbearbeiterDTO> result = new
	// LinkedList<AlarmbearbeiterDTO>();
	//		
	// for (AlarmbearbeiterDTO alarmbearbeiterDTO : gibAlleAlarmbearbeiter()) {
	// if( gruppe.istBearbeiterEnthalten(alarmbearbeiterDTO) ) {
	// result.add(alarmbearbeiterDTO);
	// }
	// }
	// return result;
	// }

	/**
	 * @return null if there is no Filter with this id
	 */
	public FilterConditionDTO getFilterConditionForId(int id) {
		for (FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition.getIFilterConditionID() == id) {
				return filterCondition;
			}
		}
		return null;
	}

	public Collection<FilterConditionsToFilterDTO> getAllFilterConditionMappings() {
		return allFilterConditionMappings;
	}

	public Collection<RubrikDTO> gibAlleRubriken() {
		return alleRubriken;
	}

}
