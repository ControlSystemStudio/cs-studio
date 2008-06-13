package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicDTO;
import org.hibernate.Session;

@Entity
public class Configuration {
	private Collection<FilterDTO> allFilters;
	private Collection<FilterConditionDTO> allFilterCondition;
	private Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private Collection<TopicDTO> alleAlarmtopics;
	private Collection<AlarmbearbeiterGruppenDTO> alleAlarmbearbeiterGruppen;
	
	public Configuration(Session session) {
		alleAlarmbarbeiter = session.createCriteria(AlarmbearbeiterDTO.class).list();
		alleAlarmtopics = session.createCriteria(TopicDTO.class).list();
		alleAlarmbearbeiterGruppen = session.createCriteria(AlarmbearbeiterGruppenDTO.class).list();
		allFilters = session.createCriteria(FilterDTO.class).list();
		
		
		// TODO
		allFilterCondition = Collections.emptyList(); //session.createCriteria(FilterConditionDTO.class).list();
	}
	
	/**
	 * Returns a list of all FilterDTO's
	 */
	public Collection<FilterDTO> getAllFilters() {
		return allFilters;
	}

	public Collection<TopicDTO> getAllTopics() {
		return alleAlarmtopics;
	}
	
	public Collection<AlarmbearbeiterDTO> gibAlleAlarmbearbeiter() {
		return alleAlarmbarbeiter;
	}
	public Collection<AlarmbearbeiterDTO> gibAlarmbearbeiterFuerAlarmbearbeiterGruppe(AlarmbearbeiterGruppenDTO gruppe) {
		Collection<AlarmbearbeiterDTO> result = new LinkedList<AlarmbearbeiterDTO>();
		
		for (AlarmbearbeiterDTO alarmbearbeiterDTO : gibAlleAlarmbearbeiter()) {
			if( gruppe.istBearbeiterEnthalten(alarmbearbeiterDTO) ) {
				result.add(alarmbearbeiterDTO);
			}
		}
		return result;
	}

	public Collection<AlarmbearbeiterGruppenDTO> gibAlleAlarmbearbeiterGruppen() {
		return alleAlarmbearbeiterGruppen;
	}

	public Collection<FilterConditionDTO> getFilterConditionsOfFilter(FilterDTO filter) {
		return null;
	}
	public Collection<FilterConditionDTO> getAllFilterConditions() {
		return allFilterCondition;
	}
}
