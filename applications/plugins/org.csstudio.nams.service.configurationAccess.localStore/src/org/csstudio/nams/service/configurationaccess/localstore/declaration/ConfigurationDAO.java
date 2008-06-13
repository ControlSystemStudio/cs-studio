package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterDTO;
import org.hibernate.Session;

@Entity
public class ConfigurationDAO {
	// TODO implement methods(move methods in the corresponding DTO's)

	private Collection<FilterDTO> allFilters;
	private final Session session;
	
	public ConfigurationDAO(Session session) {
		this.session = session;
		allFilters = session.createCriteria(FilterDTO.class).list();
	}
	
	/**
	 * Returns a list of all FilterDTO's
	 * 
	 * @return
	 */
	public Collection<FilterDTO> getAllFilters() {
		
		return allFilters;
	}
}
