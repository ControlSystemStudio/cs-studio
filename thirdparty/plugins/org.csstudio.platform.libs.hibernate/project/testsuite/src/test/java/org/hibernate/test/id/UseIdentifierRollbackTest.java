//$Id: $
package org.hibernate.test.id;

import junit.framework.Test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;

/**
 * @author Emmanuel Bernard
 */
public class UseIdentifierRollbackTest extends FunctionalTestCase {
	public UseIdentifierRollbackTest(String str) {
		super(str);
	}

	public String[] getMappings() {
		return new String[] { "id/Product.hbm.xml" };
	}

	public void configure(Configuration cfg) {
		cfg.setProperty( Environment.USE_IDENTIFIER_ROLLBACK, "true");
		super.configure( cfg );
	}

	public static Test suite() {
		return new FunctionalTestClassTestSuite( UseIdentifierRollbackTest.class );
	}

	public void testSimpleRollback() {
		Session session = openSession();
		Transaction t = session.beginTransaction();
		Product prod = new Product();
		assertNull( prod.getName() );
		session.persist(prod);
		session.flush();
		assertNotNull( prod.getName() );
		t.rollback();
		session.close();
	}
}
