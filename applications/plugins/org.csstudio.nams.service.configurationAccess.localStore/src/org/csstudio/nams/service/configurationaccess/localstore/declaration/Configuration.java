package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterDTO;
import org.hibernate.Session;

@Entity
public class Configuration {
	private Collection<FilterDTO> allFilters;
	private Collection<AlarmbearbeiterDTO> alleAlarmbarbeiter;
	
	public Configuration(Session session) {
		alleAlarmbarbeiter = session.createCriteria(AlarmbearbeiterDTO.class).list();
		allFilters = session.createCriteria(FilterDTO.class).list();
	}
	
	/**
	 * Returns a list of all FilterDTO's
	 */
	public Collection<FilterDTO> getAllFilters() {
		return allFilters;
	}
	
	public Collection<AlarmbearbeiterDTO> gibAlleAlarmbearbeiter() {
		return alleAlarmbarbeiter;
	}
}
