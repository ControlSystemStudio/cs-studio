package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Project;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class ProjectService {

	@Inject
	private EntityManager em;
	
	@ClearPersistenceContextOnReturn
	public List<Project> findAll() {
		TypedQuery<Project> query = em.createNamedQuery(Project.FIND_ALL, Project.class);
		return query.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	public Option<Project> findByName(String name) {
		TypedQuery<Project> query = em.createNamedQuery(Project.FIND_BY_NAME, Project.class);
		query.setParameter("keyword", name);
		List<Project> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<Project>();
		}
		return new Some<Project>(resultList.get(0));
	}

}
