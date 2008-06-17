package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfiguration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.AlarmbearbeiterZuAlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@Entity
public class Configuration implements FilterConditionForIdProvider{
	private Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private Collection<TopicDTO> alleAlarmtopics;
	private Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
	private Collection<FilterDTO> allFilters;

	private Collection<FilterConditionDTO> allFilterConditions;

	@SuppressWarnings("unchecked")
	public Configuration(Session session) throws InconsistentConfiguration,
			StorageError, StorageException {

		// PUBLICs
		alleAlarmbarbeiter = session.createCriteria(AlarmbearbeiterDTO.class)
				.addOrder(Order.asc("userId")).list();
		alleAlarmbearbeiterGruppen = session.createCriteria(
				AlarmbearbeiterGruppenDTO.class).addOrder(
				Order.asc("userGroupId")).list();

		alleAlarmtopics = session.createCriteria(TopicDTO.class).list();
		allFilters = session.createCriteria(FilterDTO.class).list();
		
		allFilterConditions = session.createCriteria(FilterConditionDTO.class).addOrder(Order.asc("iFilterConditionID")).list();

		
		// PRIVATEs
		Collection<AlarmbearbeiterZuAlarmbearbeiterGruppenDTO> allUserUserGroupAggregation = session
		.createCriteria(
				AlarmbearbeiterZuAlarmbearbeiterGruppenDTO.class)
				.list();
		pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu(allUserUserGroupAggregation);

		Collection<FilterConditionTypeDTO> allFilterConditionsTypes = session
				.createCriteria(FilterConditionTypeDTO.class).list();
		pruefeUndOrdneTypenDenFilterConditionsZu(allFilterConditionsTypes);

		Collection<FilterConditionsToFilterDTO> allFilterConditionToFilter = session
		.createCriteria(FilterConditionsToFilterDTO.class).list();
		pruefeUndOrdnerFilterDieFilterConditionsZu(allFilterConditionToFilter);
		setChildFilterConditionsInJunctorDTOs();
	}

	private void setChildFilterConditionsInJunctorDTOs(){
		for (FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition instanceof JunctorConditionDTO){
				JunctorConditionDTO junctorConditionDTO = (JunctorConditionDTO) filterCondition;
				junctorConditionDTO.injectYourselfYourChildren(this);
			}
		}
	}
	private void pruefeUndOrdnerFilterDieFilterConditionsZu(
			Collection<FilterConditionsToFilterDTO> allFilterConditionToFilter) {
		// TODO Auto-generated method stub
		
	}

	private void pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu(
			Collection<AlarmbearbeiterZuAlarmbearbeiterGruppenDTO> allUserUserGroupAggregation)
			throws InconsistentConfiguration {
		System.out.println("Configuration.pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu()"+allUserUserGroupAggregation.size());
		for (AlarmbearbeiterZuAlarmbearbeiterGruppenDTO alarmbearbeiterZuAlarmbearbeiterGruppe : allUserUserGroupAggregation) {
			System.out.println("Configuration.pruefeUndOrdneAlarmbearbeiterDenAlarmbearbeiterGruppenZu()"+alarmbearbeiterZuAlarmbearbeiterGruppe.toString());

			int alarmbearbeiterId = alarmbearbeiterZuAlarmbearbeiterGruppe
					.getUserRef();
			int alarmbearbeiterGruppenId = alarmbearbeiterZuAlarmbearbeiterGruppe
					.getUserGroupRef();
			for (AlarmbearbeiterGruppenDTO alarmbearbeiterGruppe : alleAlarmbearbeiterGruppen) {
				int gruppenId = alarmbearbeiterGruppe.getUserGroupId();

				if (gruppenId == alarmbearbeiterGruppenId) {
					for (AlarmbearbeiterDTO alarmbearbeiter : alleAlarmbarbeiter) {
						if (alarmbearbeiterId == alarmbearbeiter.getUserId()) {
							alarmbearbeiterGruppe.alarmbearbeiterZuordnen(alarmbearbeiter);
						}
					}
				}
			}
		}
	}
	/**
	 * @deprecated "FilterConditionType is a redundant information"
	 * @param allFilterConditionsTypes
	 * @throws InconsistentConfiguration
	 */
	@Deprecated
	private void pruefeUndOrdneTypenDenFilterConditionsZu(
			Collection<FilterConditionTypeDTO> allFilterConditionsTypes)
			throws InconsistentConfiguration {
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

	public Collection<FilterConditionDTO> getFilterConditionsOfFilter(
			FilterDTO filter) {
		return null;
	}
/**
 * @return null if there is no Filter with this id
 */
	public FilterConditionDTO getFilterForId(int id) {
		for (FilterConditionDTO filterCondition : allFilterConditions) {
			if (filterCondition.getIFilterConditionID() == id)
			{
				return filterCondition;
			}
		}
		return null;
	}

}
