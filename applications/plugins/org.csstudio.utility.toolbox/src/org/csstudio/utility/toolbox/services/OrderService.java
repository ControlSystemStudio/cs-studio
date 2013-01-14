package org.csstudio.utility.toolbox.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.Order;
import org.csstudio.utility.toolbox.entities.OrderPos;
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

public class OrderService {

	@Inject
	private EntityManager em;

	@Inject
	private JpaQueryBuilder querybuilder;

	@ClearPersistenceContextOnReturn
	public List<Order> findAll() {
		TypedQuery<Order> query = em.createNamedQuery(Order.FIND_ALL, Order.class);
		query.setMaxResults(50);
		return query.getResultList();		
	}

	@ClearPersistenceContextOnReturn
	public Option<Order> findByNummer(BigDecimal nummer) {
		TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMMER, Order.class);
		query.setParameter("nummer", nummer);
		List<Order> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<Order>();
		}
		return new Some<Order>(resultList.get(0));
	}
	
	@ClearPersistenceContextOnReturn
	public List<Order> find(List<SearchTerm> searchTerms, OrderBy orderBy) {

		List<SearchTerm> subQuerySearchTerms = new ArrayList<SearchTerm>();
		List<SearchTerm> mainQuerySearchTerms = new ArrayList<SearchTerm>();

		for (SearchTerm searchTerm : searchTerms) {
			if (searchTerm.getProperty().isSubQuery()) {
				subQuerySearchTerms.add(searchTerm);
			} else {
				mainQuerySearchTerms.add(searchTerm);
			}
		}

		
		if (subQuerySearchTerms.isEmpty()) {
			return findWithFilter(mainQuerySearchTerms, orderBy);
		} else {
			for (SearchTerm searchTerm : mainQuerySearchTerms) {
				searchTerm.setPrefix("order");
			}
			return findWithSubQueryFilter(mainQuerySearchTerms, subQuerySearchTerms, orderBy);
		}

	}

	@ClearPersistenceContextOnReturn
	private List<Order> findWithFilter(List<SearchTerm> mainQuerySearchTerms, OrderBy orderBy) {
		JpaQuery jpaQuery = querybuilder.build(Order.class, mainQuerySearchTerms, new None<JoinClause>(),
					new None<FilterClause>(), new Some<OrderBy>(orderBy));
		TypedQuery<Order> orders = em.createQuery(jpaQuery.getQueryString(), Order.class);
		return orders.getResultList();
	}

	@ClearPersistenceContextOnReturn
	private List<Order> findWithSubQueryFilter(List<SearchTerm> mainQuerySearchTerms,
				List<SearchTerm> subQuerySearchTerms, OrderBy orderBy) {

		Option<FilterClause> filterClause;

		JpaQuery jpaSubQuery = querybuilder.build(Article.class, subQuerySearchTerms, new None<JoinClause>(),
					new None<FilterClause>(), new None<OrderBy>());

		String subQueryFilter = "orderPos.artikelDatenId in (select article.gruppeArtikel "
					+ jpaSubQuery.getQueryString() + ")";
		filterClause = new Some<FilterClause>(new FilterClause(subQueryFilter));

		JpaQuery jpaQuery = querybuilder.build(OrderPos.class, mainQuerySearchTerms, new Some<JoinClause>(
					new JoinClause("left join fetch orderPos.order")), filterClause, new Some<OrderBy>(new OrderBy(
					"order." + orderBy.getValue())));

		TypedQuery<OrderPos> positions = em.createQuery(jpaQuery.getQueryString(), OrderPos.class);

		List<Order> orders = new ArrayList<Order>();
		Set<BigDecimal> numbers = new HashSet<BigDecimal>();

		for (OrderPos orderPos : positions.getResultList()) {
			if (!numbers.contains(orderPos.getOrder().getNummer())) {
				numbers.add(orderPos.getOrder().getNummer());
				orders.add(orderPos.getOrder());
			}
		}
		
		return orders;
	}

}
