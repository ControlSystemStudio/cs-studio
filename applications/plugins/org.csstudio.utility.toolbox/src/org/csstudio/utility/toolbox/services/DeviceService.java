package org.csstudio.utility.toolbox.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.Device;
import org.csstudio.utility.toolbox.func.None;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.func.Some;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class DeviceService {

	@Inject
	private EntityManager em;

	@ClearPersistenceContextOnReturn
	public List<Device> findAll() {
		TypedQuery<Device> query = em.createNamedQuery(Device.FIND_ALL, Device.class);
		return query.getResultList();
	}

	@ClearPersistenceContextOnReturn
	public Option<Device> findByName(String name) {
		TypedQuery<Device> query = em.createNamedQuery(Device.FIND_BY_NAME, Device.class);
		query.setParameter("keyword", name);
		List<Device> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			return new None<Device>();
		}
		return new Some<Device>(resultList.get(0));
	}

}
