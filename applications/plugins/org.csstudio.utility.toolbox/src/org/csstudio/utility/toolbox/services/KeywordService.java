package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.KeywordHardware;
import org.csstudio.utility.toolbox.entities.KeywordSoftware;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class KeywordService {

	@Inject
	private EntityManager em;
	
	@ClearPersistenceContextOnReturn
	public List<? extends TextValue> findAllSoftware() {
		TypedQuery<KeywordSoftware> query = em.createNamedQuery(KeywordSoftware.FIND_ALL, KeywordSoftware.class);
		return query.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	public List<? extends TextValue> findAllHardware() {
		TypedQuery<KeywordHardware> query = em.createNamedQuery(KeywordHardware.FIND_ALL, KeywordHardware.class);
		return query.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	public Option<KeywordHardware> findByKeywordHardware(String keyword) {
		TypedQuery<KeywordHardware> query = em.createNamedQuery(KeywordHardware.FIND_BY_KEYWORD, KeywordHardware.class);
		query.setParameter("keyword", keyword);
		List<KeywordHardware> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<KeywordHardware>();
		}
		return new Some<KeywordHardware>(resultList.get(0));
	}

	@ClearPersistenceContextOnReturn
	public Option<KeywordSoftware> findByKeywordSoftware(String keyword) {
		TypedQuery<KeywordSoftware> query = em.createNamedQuery(KeywordSoftware.FIND_BY_KEYWORD, KeywordSoftware.class);
		query.setParameter("keyword", keyword);
		List<KeywordSoftware> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<KeywordSoftware>();
		}
		return new Some<KeywordSoftware>(resultList.get(0));
	}

}
