package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicDTO;
import org.hibernate.Session;

@Entity
public class Configuration {
	private Collection<FilterDTO> allFilters;
	private Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	private Collection<TopicDTO> alleAlarmtopics;
	
	public Configuration(Session session) {
		alleAlarmbarbeiter = session.createCriteria(AlarmbearbeiterDTO.class).list();
		alleAlarmtopics = session.createCriteria(TopicDTO.class).list();
		
		
		allFilters = session.createCriteria(FilterDTO.class).list();
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
}
