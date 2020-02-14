//$Id: Thingy.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.access;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * @author Emmanuel Bernard
 */
@MappedSuperclass
public class Thingy {
	private String god;

	@Transient
	public String getGod() {
		return god;
	}

	public void setGod(String god) {
		this.god = god;
	}
}
