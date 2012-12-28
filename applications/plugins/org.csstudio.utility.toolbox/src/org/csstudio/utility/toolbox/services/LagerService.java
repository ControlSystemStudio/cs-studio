package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Lager;
import org.csstudio.utility.toolbox.framework.jpa.FilterClause;
import org.csstudio.utility.toolbox.framework.jpa.JoinClause;
import org.csstudio.utility.toolbox.framework.jpa.JpaQuery;
import org.csstudio.utility.toolbox.framework.jpa.JpaQueryBuilder;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class LagerService {

	@Inject
	private EntityManager em;

	@Inject
	private JpaQueryBuilder querybuilder;

	@ClearPersistenceContextOnReturn
	public List<Lager> findAll() {
		TypedQuery<Lager> query = em.createNamedQuery(Lager.FIND_ALL, Lager.class);
		query.setMaxResults(100);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public Option<Lager> findByName(String name) {
		TypedQuery<Lager> query = em.createNamedQuery(Lager.FIND_BY_NAME, Lager.class);
		query.setParameter("name", name);
		List<Lager> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<Lager>();
		}
		return new Some<Lager>(resultList.get(0));
	}

	@ClearPersistenceContextOnReturn
	public List<Lager> find(List<SearchTerm> searchTerms, OrderBy orderBy) {
		JpaQuery jpaQuery = querybuilder.build(Lager.class, searchTerms, new None<JoinClause>(),
					new None<FilterClause>(), new Some<OrderBy>(orderBy));
		TypedQuery<Lager> query = em.createQuery(jpaQuery.getQueryString(), Lager.class);
		return query.getResultList();
	}

}