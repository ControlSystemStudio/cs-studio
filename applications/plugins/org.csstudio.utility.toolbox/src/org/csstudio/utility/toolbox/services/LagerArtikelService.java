package org.csstudio.utility.toolbox.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.LagerArtikel;
import org.csstudio.utility.toolbox.framework.jpa.FilterClause;
import org.csstudio.utility.toolbox.framework.jpa.JoinClause;
import org.csstudio.utility.toolbox.framework.jpa.JpaQuery;
import org.csstudio.utility.toolbox.framework.jpa.JpaQueryBuilder;
import org.csstudio.utility.toolbox.framework.jpa.OrderBy;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class LagerArtikelService {

	@Inject
	private EntityManager em;

	@Inject
	private JpaQueryBuilder querybuilder;
	
	@ClearPersistenceContextOnReturn
	public List<LagerArtikel> find(List<SearchTerm> searchTerms, OrderBy orderBy) {

		List<SearchTerm> mainQuerySearchTerms = new ArrayList<SearchTerm>();

		for (SearchTerm searchTerm : searchTerms) {
			if (searchTerm.getProperty().isSubQuery()) {
				if (isPropertyFromArticleDescription(searchTerm.getProperty())) {
					String propertyName = searchTerm.getProperty().getName();
					String value = searchTerm.getValue();
					SearchTermType searchTermType = searchTerm.getSearchTermType();
					mainQuerySearchTerms.add(new SearchTerm(new Property("articleDescription." + propertyName), value,
								searchTermType));
				}
			} else {
				mainQuerySearchTerms.add(searchTerm);
			}
		}

		JpaQuery jpaQuery = querybuilder.build(LagerArtikel.class, mainQuerySearchTerms, new Some<JoinClause>(
					new JoinClause("left join fetch lagerartikel.articleDescription")), new None<FilterClause>(),
					new Some<OrderBy>(orderBy));
		TypedQuery<LagerArtikel> query = em.createQuery(jpaQuery.getQueryString(), LagerArtikel.class);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<LagerArtikel> findAll(String lagerName, BigDecimal articleDescriptionId) {
		TypedQuery<LagerArtikel> query = em.createNamedQuery(LagerArtikel.FIND_ALL, LagerArtikel.class);
		query.setParameter("name", lagerName);
		query.setParameter("id", articleDescriptionId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public Option<LagerArtikel> findById(String id) {
		TypedQuery<LagerArtikel> query = em.createNamedQuery(LagerArtikel.FIND_BY_ID, LagerArtikel.class);
		query.setParameter("id", id);
		List<LagerArtikel> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<LagerArtikel>();
		}
		return new Some<LagerArtikel>(resultList.get(0));
	}

	private boolean isPropertyFromArticleDescription(Property property) {
		return property.getName().equalsIgnoreCase("beschreibung");
	}

}
