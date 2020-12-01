//$Id: Simple.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.instrument;

/**
 * @author Emmanuel Bernard
 */
public class Simple {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
