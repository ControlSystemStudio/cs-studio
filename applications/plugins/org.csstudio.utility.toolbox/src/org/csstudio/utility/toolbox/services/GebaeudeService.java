package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Gebaeude;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class GebaeudeService {

	@Inject
	private EntityManager em;
	
	@ClearPersistenceContextOnReturn
	public List<Gebaeude> findAll() {
		TypedQuery<Gebaeude> query = em.createNamedQuery(Gebaeude.FIND_ALL, Gebaeude.class);
		return query.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	public Option<Gebaeude> findByName(String name) {
		TypedQuery<Gebaeude> query = em.createNamedQuery(Gebaeude.FIND_BY_NAME, Gebaeude.class);
		query.setParameter("name", name);
		List<Gebaeude> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<Gebaeude>();
		}
		return new Some<Gebaeude>(resultList.get(0));
	}
	
}
