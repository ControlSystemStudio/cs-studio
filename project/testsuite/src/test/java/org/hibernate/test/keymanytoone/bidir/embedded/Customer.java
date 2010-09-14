package org.hibernate.test.keymanytoone.bidir.embedded;

import java.util.Collection;
import java.util.ArrayList;

/**
 * {@inheritDoc}
 *
 * @author Steve Ebersole
 */
public class Customer {
	private Long id;
	private String name;
	private Collection orders = new ArrayList();

	public Customer() {
	}

	public Customer(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection getOrders() {
		return orders;
	}

	public void setOrders(Collection orders) {
		this.orders = orders;
	}
}
