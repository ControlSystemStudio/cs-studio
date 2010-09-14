//$Id: ExceptionListener.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.callbacks;

import javax.persistence.PrePersist;

/**
 * @author Emmanuel Bernard
 */
public class ExceptionListener {
	@PrePersist
	public void raiseException(Object e) {
		throw new ArithmeticException( "1/0 impossible" );
	}
}
