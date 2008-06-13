package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;

import javax.persistence.Entity;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterDTO;
import org.hibernate.Session;

@Entity
public class Configuration {
	// TODO implement methods(move methods in the corresponding DTO's)

	private Collection<FilterDTO> allFilters;
	private final Session session;
	
	public Configuration(Session session) {
		this.session = session;
		allFilters = this.session.createCriteria(FilterDTO.class).list();//.createQuery("from FilterDTO t").list();
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
