//$Id: FakeDataSourceException.java 15483 2008-11-03 14:25:59Z hardy.ferentschik $
package org.hibernate.ejb.test.connection;

/**
 * @author Emmanuel Bernard
 */
public class FakeDataSourceException extends RuntimeException {
	public FakeDataSourceException(String message) {
		super( message );
	}
}
