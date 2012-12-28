package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.LagerBox;
import org.csstudio.utility.toolbox.entities.LagerFach;
import org.csstudio.utility.toolbox.entities.LagerOrt;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class StoreLookupDataService {

	@Inject
	private EntityManager em;

	@ClearPersistenceContextOnReturn
	public List<LagerOrt> findAllLocations(String lagerName) {
		TypedQuery<LagerOrt> query = em.createNamedQuery(LagerOrt.FIND_ALL, LagerOrt.class);
		query.setParameter("lagerName", lagerName);
		return query.getResultList();		
	}

	@ClearPersistenceContextOnReturn
	public List<LagerFach> findAllShelves(String lagerName) {
		TypedQuery<LagerFach> query = em.createNamedQuery(LagerFach.FIND_ALL, LagerFach.class);
		query.setParameter("lagerName", lagerName);
		return query.getResultList();		
	}

	@ClearPersistenceContextOnReturn
	public List<LagerBox> findAllBox(String lagerName) {
		TypedQuery<LagerBox> query = em.createNamedQuery(LagerBox.FIND_ALL, LagerBox.class);
		query.setParameter("lagerName", lagerName);
		return query.getResultList();		
	}

	public Option<LagerOrt> findLocationByName(String lagerName, String boxName) {
		TypedQuery<LagerOrt> query = em.createNamedQuery(LagerOrt.FIND_BY_NAME, LagerOrt.class);
		query.setParameter("lagerName", lagerName);
		query.setParameter("name", boxName);
		List<LagerOrt> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<LagerOrt>();
		}
		return new Some<LagerOrt>(resultList.get(0));
	}

	public Option<LagerBox> findBoxByName(String lagerName, String boxName) {
		TypedQuery<LagerBox> query = em.createNamedQuery(LagerBox.FIND_BY_NAME, LagerBox.class);
		query.setParameter("lagerName", lagerName);
		query.setParameter("name", boxName);
		List<LagerBox> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<LagerBox>();
		}
		return new Some<LagerBox>(resultList.get(0));
	}

	public Option<LagerFach> findShelfByName(String lagerName, String boxName) {
		TypedQuery<LagerFach> query = em.createNamedQuery(LagerFach.FIND_BY_NAME, LagerFach.class);
		query.setParameter("lagerName", lagerName);
		query.setParameter("name", boxName);
		List<LagerFach> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<LagerFach>();
		}
		return new Some<LagerFach>(resultList.get(0));
	}
	
}
