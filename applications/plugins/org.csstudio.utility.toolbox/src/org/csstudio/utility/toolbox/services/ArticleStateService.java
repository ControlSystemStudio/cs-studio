package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.ArticleState;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class ArticleStateService {

	@Inject
	private EntityManager em;

	@ClearPersistenceContextOnReturn
	public List<ArticleState> findAll() {
		TypedQuery<ArticleState> x = em.createNamedQuery(ArticleState.FIND_ALL, ArticleState.class);
		return x.getResultList();		
	}
	
}
