package org.csstudio.utility.toolbox.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Article;
import org.csstudio.utility.toolbox.entities.ArticleDelivered;
import org.csstudio.utility.toolbox.entities.ArticleHistoryInfo;
import org.csstudio.utility.toolbox.entities.ArticleInStore;
import org.csstudio.utility.toolbox.entities.ArticleInstalled;
import org.csstudio.utility.toolbox.entities.ArticleMaintenance;
import org.csstudio.utility.toolbox.entities.ArticleRented;
import org.csstudio.utility.toolbox.entities.ArticleRetired;
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

public class ArticleService {

	@Inject
	private EntityManager em;

	@Inject
	private JpaQueryBuilder querybuilder;

	@ClearPersistenceContextOnReturn
	public BigDecimal getNextInternId() {
		Query query = em.createNativeQuery("select SEQ_INTERN_ID.NextVal from dual");
		return new BigDecimal(query.getResultList().get(0).toString());
	}

	@ClearPersistenceContextOnReturn
	public BigDecimal createId() {
		Query query = em.createNativeQuery("select SEQ_ARTIKEL_DATEN.NextVal from dual");
		return new BigDecimal(query.getResultList().get(0).toString());
	}

	@ClearPersistenceContextOnReturn
	public List<Article> find(List<SearchTerm> searchTerms, OrderBy orderBy) {

		List<SearchTerm> mainQuerySearchTerms = new ArrayList<SearchTerm>();

		Option<FilterClause> filter = new None<FilterClause>();

		for (SearchTerm searchTerm : searchTerms) {
			if (searchTerm.getProperty().isSubQuery()) {
				if (isPropertyFromArticleDescription(searchTerm.getProperty())) {
					String propertyName = searchTerm.getProperty().getName();
					String value = searchTerm.getValue();
					SearchTermType searchTermType = searchTerm.getSearchTermType();
					mainQuerySearchTerms.add(new SearchTerm(new Property("articleDescription." + propertyName), value,
								searchTermType));
				} else if (searchTerm.getProperty().getName().equalsIgnoreCase("baNr")) {
					filter = new Some<FilterClause>(new FilterClause(
								"article.gruppeArtikel in (select ap.article.id from OrderPos ap where ap.order.nummer like '_"
											+ searchTerm.getValue() + "')"));
				}
			} else {
				mainQuerySearchTerms.add(searchTerm);
			}
		}

		JpaQuery jpaQuery = querybuilder.build(Article.class, mainQuerySearchTerms, new Some<JoinClause>(
					new JoinClause("left join fetch article.articleDescription")), filter, new Some<OrderBy>(orderBy));

		TypedQuery<Article> query = em.createQuery(jpaQuery.getQueryString(), Article.class);

		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<Article> findAllArticleInGroup(BigDecimal gruppe) {
		TypedQuery<Article> query = em.createNamedQuery(Article.FIND_IN_GROUP, Article.class);
		query.setParameter("gruppeArtikel", gruppe);
		List<Article> articles = query.getResultList();
		int index = 1;
		for (Article article : articles) {
			article.setIndex(index);
			index++;
		}
		return articles;
	}

	@ClearPersistenceContextOnReturn
	public Option<Article> findById(BigDecimal articleDatenId) {
		if (articleDatenId == null) {
			return new None<Article>();
		}
		TypedQuery<Article> query = em.createNamedQuery(Article.FIND_BY_ID, Article.class);
		query.setParameter("id", articleDatenId);
		List<Article> articles = query.getResultList();
		if (articles.isEmpty()) {
			return new None<Article>();
		} else {
			if (articles.size() > 1) {
				throw new IllegalStateException("Unexpected recourd count");
			}
			return new Some<Article>(articles.get(0));
		}
	}

	private static class DateComparator implements Comparator<ArticleHistoryInfo>, Serializable {
		private static final long serialVersionUID = 1L;
		public int compare(ArticleHistoryInfo t1, ArticleHistoryInfo t2) { 
	    	  return t2.getDate().compareTo(t1.getDate());
	      }
	}
	
	@ClearPersistenceContextOnReturn
	public List<ArticleHistoryInfo> buildArticleHistory(BigDecimal articleDatenId) {
		List<ArticleHistoryInfo> articleHistory = new ArrayList<ArticleHistoryInfo>();
		articleHistory.addAll(findArticleInstalled(articleDatenId));
		articleHistory.addAll(findArticleRetired(articleDatenId));
		articleHistory.addAll(findArticleMaintenance(articleDatenId));
		articleHistory.addAll(findArticleInStore(articleDatenId));
		articleHistory.addAll(findArticleRented(articleDatenId));
		articleHistory.addAll(findArticleDelivered(articleDatenId));
		Collections.sort(articleHistory, new DateComparator());
		return articleHistory;
	}
	
	@ClearPersistenceContextOnReturn
	public List<ArticleInstalled> findAllInstalledData(BigDecimal articleDatenId) {
		TypedQuery<ArticleInstalled> query = em.createNamedQuery(Article.FIND_ALL_INSTALLED, ArticleInstalled.class);
		query.setParameter("artikelDatenId", articleDatenId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<Article> findContains(BigDecimal articleDatenId) {
		TypedQuery<ArticleInstalled> query = em.createNamedQuery(ArticleInstalled.FIND_INSTALLED_IN, ArticleInstalled.class);
		query.setParameter("eingebautInArtikel", articleDatenId);
		List<Article> installedArticle = new ArrayList<Article>();
		for (ArticleInstalled articleInstalled : query.getResultList()) {
			Option<Article> article = findById(articleInstalled.getArtikelDatenId());
			if (article.hasValue()) {
				installedArticle.add(article.get());
			}
		}
		return installedArticle;
	}

	@ClearPersistenceContextOnReturn
	public List<ArticleInstalled> findArticleInstalled(BigDecimal articleDatenId) {
		if (articleDatenId == null) {
			return new ArrayList<ArticleInstalled>();
		}
		TypedQuery<ArticleInstalled> query = em.createNamedQuery(ArticleInstalled.FIND_RECORD, ArticleInstalled.class);
		query.setParameter("artikelDatenId", articleDatenId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<ArticleRetired> findArticleRetired(BigDecimal articleDatenId) {
		TypedQuery<ArticleRetired> query = em.createNamedQuery(ArticleRetired.FIND_RECORD, ArticleRetired.class);
		query.setParameter("artikelDatenId", articleDatenId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<ArticleMaintenance> findArticleMaintenance(BigDecimal articleDatenId) {
		TypedQuery<ArticleMaintenance> query = em.createNamedQuery(ArticleMaintenance.FIND_RECORD,
					ArticleMaintenance.class);
		query.setParameter("artikelDatenId", articleDatenId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<ArticleInStore> findArticleInStore(BigDecimal articleDatenId) {
		TypedQuery<ArticleInStore> query = em.createNamedQuery(ArticleInStore.FIND_RECORD, ArticleInStore.class);
		query.setParameter("artikelDatenId", articleDatenId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public Option<ArticleInStore> findNewestEntryInStore(BigDecimal articleDatenId) {
		TypedQuery<ArticleInStore> query = em.createNamedQuery(ArticleInStore.FIND_RECORD, ArticleInStore.class);
		query.setParameter("artikelDatenId", articleDatenId);
		List<ArticleInStore> articlesInStrore = query.getResultList();
		if (articlesInStrore.isEmpty()) {
			return new None<ArticleInStore>();
		}
		return new Some<ArticleInStore>(articlesInStrore.get(0));
	}

	@ClearPersistenceContextOnReturn
	public List<ArticleRented> findArticleRented(BigDecimal articleDatenId) {
		TypedQuery<ArticleRented> query = em.createNamedQuery(ArticleRented.FIND_RECORD, ArticleRented.class);
		query.setParameter("artikelDatenId", articleDatenId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public List<ArticleDelivered> findArticleDelivered(BigDecimal articleDatenId) {
		TypedQuery<ArticleDelivered> query = em.createNamedQuery(ArticleDelivered.FIND_RECORD, ArticleDelivered.class);
		query.setParameter("artikelDatenId", articleDatenId);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	private boolean isPropertyFromArticleDescription(Property property) {
		return ((property.getName().equalsIgnoreCase("beschreibung")) || (property.getName()
					.equalsIgnoreCase("lieferantName")));
	}

}
