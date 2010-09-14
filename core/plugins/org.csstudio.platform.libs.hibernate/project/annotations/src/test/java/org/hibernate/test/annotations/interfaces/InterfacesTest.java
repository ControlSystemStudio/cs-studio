//$Id: InterfacesTest.java 18602 2010-01-21 20:48:59Z hardy.ferentschik $
package org.hibernate.test.annotations.interfaces;

import org.hibernate.test.annotations.TestCase;

/**
 * @author Emmanuel Bernard
 */
public class InterfacesTest extends TestCase {
	public void testInterface() {

	}

	protected Class[] getAnnotatedClasses() {
		return new Class[]{
				ContactImpl.class,
				UserImpl.class
		};
	}
}
