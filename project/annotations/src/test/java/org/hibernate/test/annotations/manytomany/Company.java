//$Id: Company.java 17527 2009-09-18 09:06:47Z hardy.ferentschik $
package org.hibernate.test.annotations.manytomany;

import java.io.Serializable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Column;

/**
 * @author Emmanuel Bernard
 */
@MappedSuperclass
public class Company implements Serializable {
	@Column
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
