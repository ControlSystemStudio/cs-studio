package org.csstudio.utility.toolbox.services;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.entities.OrderPosFinder;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class OrderPosService implements OrderPosFinder {
	
	@Inject
	private EntityManager em;

	@ClearPersistenceContextOnReturn
	public List<OrderPos> findByGruppeArtikel(BigDecimal gruppeArtikel) {
		TypedQuery<OrderPos> query = em.createNamedQuery(OrderPos.FIND_IN_ARTIKEL_DATEN_ID, OrderPos.class);
		query.setParameter("artikelDatenId", gruppeArtikel);
		return query.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	@Override
	public List<OrderPos> findByBaNr(BigDecimal baNr) {
		TypedQuery<OrderPos> query = em.createNamedQuery(OrderPos.FIND_BY_BA_NR, OrderPos.class);
		query.setParameter("baNr", baNr);
		return query.getResultList();		
	}
}
