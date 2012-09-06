package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Firma;
import org.csstudio.utility.toolbox.framework.jpa.FilterClause;
import org.csstudio.utility.toolbox.framework.jpa.JoinClause;
import org.csstudio.utility.toolbox.framework.jpa.JpaQuery;
import org.csstudio.utility.toolbox.framework.jpa.JpaQueryBuilder;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class FirmaService {

	@Inject
	private EntityManager em;

	@Inject
	private JpaQueryBuilder querybuilder;

	@ClearPersistenceContextOnReturn
	public List<Firma> findAll() {
		TypedQuery<Firma> x = em.createNamedQuery(Firma.FIND_ALL, Firma.class);
		return x.getResultList();		
	}

	@ClearPersistenceContextOnReturn
	public List<Firma> find(List<SearchTerm> searchTerms, OrderBy orderBy) {
		JpaQuery jpaQuery = querybuilder.build(Firma.class, searchTerms,
					new None<JoinClause>(),
					new None<FilterClause>(), 
					new Some<OrderBy>(orderBy));
	TypedQuery<Firma> x = em.createQuery(jpaQuery.getQueryString(), Firma.class);
		return x.getResultList();
	}
			
}
