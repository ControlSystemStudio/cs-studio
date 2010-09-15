//$Id: Location.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.override;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Location {
	private String name;

	@Id
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
