//$Id: NoOpListener.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test;

import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreInsertEvent;

/**
 * @author Emmanuel Bernard
 */
public class NoOpListener implements PreInsertEventListener {
	public boolean onPreInsert(PreInsertEvent event) {
		return false;
	}
}
