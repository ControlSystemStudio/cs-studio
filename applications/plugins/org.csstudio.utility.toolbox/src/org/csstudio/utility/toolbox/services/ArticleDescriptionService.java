package org.csstudio.utility.toolbox.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.ArticleDescription;
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

public class ArticleDescriptionService {

	@Inject
	private EntityManager em;

	@Inject
	private JpaQueryBuilder querybuilder;
	
	@ClearPersistenceContextOnReturn
	public List<ArticleDescription> find(List<SearchTerm> searchTerms, OrderBy orderBy) {
		JpaQuery jpaQuery = querybuilder.build(ArticleDescription.class, searchTerms, new None<JoinClause>(),
					new None<FilterClause>(), new Some<OrderBy>(orderBy));
		TypedQuery<ArticleDescription> x = em.createQuery(jpaQuery.getQueryString(), ArticleDescription.class);
		return x.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<ArticleDescription> findAll() {
		TypedQuery<ArticleDescription> x = em.createNamedQuery(ArticleDescription.FIND_ALL, ArticleDescription.class);
		return x.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	public List<String> findAllDescription() {
		TypedQuery<ArticleDescription> x = em.createNamedQuery(ArticleDescription.FIND_ALL, ArticleDescription.class);
		List<String> descriptions = new ArrayList<String>();
		for (ArticleDescription ad : x.getResultList()) {
			descriptions.add(ad.getBeschreibung());
		}
		return descriptions;
	}
	
}
