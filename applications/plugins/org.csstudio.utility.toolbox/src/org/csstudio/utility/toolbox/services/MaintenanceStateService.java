package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.WartungsStatus;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class MaintenanceStateService {

	@Inject
	private EntityManager em;

	@ClearPersistenceContextOnReturn
	public List<WartungsStatus> findAll() {
		TypedQuery<WartungsStatus> query = em.createNamedQuery(WartungsStatus.FIND_ALL, WartungsStatus.class);
		return query.getResultList();		
	}
	
}
