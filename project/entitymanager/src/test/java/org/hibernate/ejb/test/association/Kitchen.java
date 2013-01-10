//$Id: Kitchen.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.association;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Kitchen {
	@Id @GeneratedValue
	private Long id;

	@OneToOne(mappedBy = "kitchen")
	private Oven oven;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Oven getOven() {
		return oven;
	}

	public void setOven(Oven oven) {
		this.oven = oven;
	}
}
