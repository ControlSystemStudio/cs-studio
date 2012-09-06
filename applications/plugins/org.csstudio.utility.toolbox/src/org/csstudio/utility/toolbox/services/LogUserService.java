package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.entities.LogUser;

import com.google.inject.Inject;

public class LogUserService {

	@Inject
	private EntityManager em;
	
	@Inject
	private Environment env;
	
	public List<LogUser> findAll() {
		TypedQuery<LogUser> query = em.createNamedQuery(LogUser.FIND_ALL, LogUser.class);
		return query.getResultList();		
	}

	public List<LogUser> findAllAndIncludeEmptySelection() {
		TypedQuery<LogUser> query = em.createNamedQuery(LogUser.FIND_ALL, LogUser.class);
		 List<LogUser> result = query.getResultList();
		 LogUser logUser = new LogUser();
		 logUser.setAccountname(env.getEmptySelectionText());
		 result.add(0, logUser);
		 return result;
	}
	
	public List<LogUser> findAllAndIncludeEmptySelectionUseEmail() {
		TypedQuery<LogUser> query = em.createNamedQuery(LogUser.FIND_ALL, LogUser.class);
		 List<LogUser> result = query.getResultList();
		 for (LogUser logUser: result) {
			 logUser.setValueProperty(LogUser.UserGetValueProperty.ACCOUNT_EMAIL);
		 }
		 LogUser logUser = new LogUser();
		 logUser.setAccountname(env.getEmptySelectionText());
		 result.add(0, logUser);
		 return result;
	}

}
