//$Id: MyOidGenerator.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
package org.hibernate.test.annotations.type;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * @author Emmanuel Bernard
 */
public class MyOidGenerator implements IdentifierGenerator {

	private int counter;

	public Serializable generate(SessionImplementor aSessionImplementor, Object aObject) throws HibernateException {
		counter++;
		return new MyOid( 0, 0, 0, counter );
	}
}

