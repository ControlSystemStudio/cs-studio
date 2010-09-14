//$Id: Mouse.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.pack.war;

import javax.persistence.ExcludeDefaultListeners;

/**
 * @author Emmanuel Bernard
 */
@ExcludeDefaultListeners
public class Mouse {
	private Integer id;
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
