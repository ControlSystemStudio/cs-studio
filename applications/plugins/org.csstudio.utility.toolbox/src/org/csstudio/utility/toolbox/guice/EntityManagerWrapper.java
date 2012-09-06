package org.csstudio.utility.toolbox.guice;

import javax.persistence.EntityManager;

import com.google.inject.Inject;

public class EntityManagerWrapper {

	@Inject
	private EntityManager em;

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
}
