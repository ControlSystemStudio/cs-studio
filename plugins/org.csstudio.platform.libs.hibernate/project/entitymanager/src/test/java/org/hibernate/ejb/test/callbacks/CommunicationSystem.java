//$Id: CommunicationSystem.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.callbacks;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * @author Emmanuel Bernard
 */
@MappedSuperclass
@EntityListeners(IncreaseListener.class)
public class CommunicationSystem {
	public int communication = 0;
	public boolean isFirst = true;
	public boolean isLast;

	public void init() {
		communication = 0;
		isFirst = true;
		isLast = false;
	}
}
