package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;
import org.hibernate.Session;

@Entity
public class Configuration {
	private Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private Collection<TopicDTO> alleAlarmtopics;
	private Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
	private Collection<FilterDTO> allFilters;

	
	private Collection<FilterConditionDTO> allFilterCondition;
	
	public Configuration(Session session) {
		
		// PUBLICs
		alleAlarmbarbeiter = session.createCriteria(AlarmbearbeiterDTO.class).list();
		alleAlarmtopics = session.createCriteria(TopicDTO.class).list();
		alleAlarmbearbeiterGruppen = session.createCriteria(AlarmbearbeiterGruppenDTO.class).list();
		allFilters = session.createCriteria(FilterDTO.class).list();
		
		// PRIVATEs
		Collection<FilterConditionTypeDTO> allFilterConditionsTypes = session.createCriteria(FilterConditionTypeDTO.class).list(); // TODO zurordnen.
		
		
		
		
		// TODO
		allFilterCondition = Collections.emptyList();//session.createCriteria(FilterConditionDTO.class).list();
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

	//-------------

	

	
//	public Collection<AlarmbearbeiterDTO> gibAlarmbearbeiterFuerAlarmbearbeiterGruppe(AlarmbearbeiterGruppenDTO gruppe) {
//		Collection<AlarmbearbeiterDTO> result = new LinkedList<AlarmbearbeiterDTO>();
//		
//		for (AlarmbearbeiterDTO alarmbearbeiterDTO : gibAlleAlarmbearbeiter()) {
//			if( gruppe.istBearbeiterEnthalten(alarmbearbeiterDTO) ) {
//				result.add(alarmbearbeiterDTO);
//			}
//		}
//		return result;
//	}


	public Collection<FilterConditionDTO> getFilterConditionsOfFilter(FilterDTO filter) {
		return null;
	}
	public Collection<FilterConditionDTO> getAllFilterConditions() {
		return allFilterCondition;
	}
}
