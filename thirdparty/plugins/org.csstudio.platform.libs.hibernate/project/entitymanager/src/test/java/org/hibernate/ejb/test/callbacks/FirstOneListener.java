//$Id: FirstOneListener.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.callbacks;

import javax.persistence.PreUpdate;

/**
 * @author Emmanuel Bernard
 */
public class FirstOneListener {
	@PreUpdate
	public void firstOne(CommunicationSystem object) {
		if ( !object.isFirst ) throw new IllegalStateException();
		object.isFirst = true;
		object.isLast = false;
		object.communication++;
	}
}
