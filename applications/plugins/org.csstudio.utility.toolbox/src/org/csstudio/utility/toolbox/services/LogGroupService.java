package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.entities.LogGroup;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;

import com.google.inject.Inject;

public class LogGroupService {

	@Inject
	private EntityManager em;
	
	@Inject
	private Environment env;
	
	public List<LogGroup> findAll() {
		TypedQuery<LogGroup> query = em.createNamedQuery(LogGroup.FIND_ALL, LogGroup.class);
		return query.getResultList();		
	}
	
	public Option<LogGroup> findByEmail(String email) {
		TypedQuery<LogGroup> query = em.createNamedQuery(LogGroup.FIND_BY_EMAIL, LogGroup.class);
		query.setParameter("groupEmail", email);
		List<LogGroup> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<LogGroup>();
		}
		return new Some<LogGroup>(resultList.get(0));
	}
	
	public List<LogGroup> findAllAndIncludeEmptySelection() {
		TypedQuery<LogGroup> query = em.createNamedQuery(LogGroup.FIND_ALL, LogGroup.class);
		 List<LogGroup> result = query.getResultList();
		 LogGroup logGroup = new LogGroup();
		 logGroup.setGroupName(env.getEmptySelectionText());
		 result.add(0, logGroup);
		 return result;
	}
	
	public List<LogGroup> findAllAndIncludeEmptySelectionUseEmail() {
		TypedQuery<LogGroup> query = em.createNamedQuery(LogGroup.FIND_ALL, LogGroup.class);
		 List<LogGroup> result = query.getResultList();
		 for (LogGroup logGroup: result) {
			 logGroup.setValueProperty(LogGroup.GroupGetValueProperty.GROUP_EMAIL);
		 }
		 LogGroup logGroup = new LogGroup();
		 logGroup.setGroupName(env.getEmptySelectionText());
		 result.add(0, logGroup);
		 return result;
	}
}
