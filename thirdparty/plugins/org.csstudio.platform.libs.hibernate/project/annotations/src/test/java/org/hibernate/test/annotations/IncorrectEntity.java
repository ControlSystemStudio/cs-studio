//$Id: IncorrectEntity.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class IncorrectEntity {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
