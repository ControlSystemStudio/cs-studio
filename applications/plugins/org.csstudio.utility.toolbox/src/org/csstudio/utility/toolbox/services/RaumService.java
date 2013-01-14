package org.csstudio.utility.toolbox.services;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Raum;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;


public class RaumService {

	@Inject
	private EntityManager em;
		
	@ClearPersistenceContextOnReturn
	public List<Raum> findAll(BigDecimal gebauedeId) {
		TypedQuery<Raum> query = em.createNamedQuery(Raum.FIND_ALL, Raum.class);
		query.setParameter("gebaeudeId", gebauedeId);
		return query.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	public Option<Raum> findByNameAndGebauedeId(String name, BigDecimal gebaeudeId) {
		TypedQuery<Raum> query = em.createNamedQuery(Raum.FIND_BY_NAME_AND_GEBAUEDE_ID, Raum.class);
		query.setParameter("name", name);
		query.setParameter("gebaeudeId", gebaeudeId);
		List<Raum> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<Raum>();
		}
		return new Some<Raum>(resultList.get(0));
	}

}