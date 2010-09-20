//$Id: Editor.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.lob;

import java.io.Serializable;

/**
 * @author Emmanuel Bernard
 */
public class Editor implements Serializable {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
