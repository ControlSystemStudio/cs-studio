//$Id: Chair.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.access;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class Chair extends Furniture {

	@Transient
	private String pillow;

	public String getPillow() {
		return pillow;
	}

	public void setPillow(String pillow) {
		this.pillow = pillow;
	}
}
