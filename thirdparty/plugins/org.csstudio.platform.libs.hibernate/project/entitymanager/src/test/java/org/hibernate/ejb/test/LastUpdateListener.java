//$Id: LastUpdateListener.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test;

import java.util.Date;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * @author Emmanuel Bernard
 */
public class LastUpdateListener {
	@PreUpdate
	@PrePersist
	public void setLastUpdate(Cat o) {
		o.setLastUpdate( new Date() );
		o.setManualVersion( o.getManualVersion() + 1 );
	}
}
