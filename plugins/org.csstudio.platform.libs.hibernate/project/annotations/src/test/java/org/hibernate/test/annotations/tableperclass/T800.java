//$Id: T800.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.tableperclass;

import javax.persistence.Entity;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class T800 extends Robot {
	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	private String targetName;
}
