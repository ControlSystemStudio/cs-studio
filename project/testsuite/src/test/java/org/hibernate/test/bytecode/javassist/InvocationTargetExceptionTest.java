package org.hibernate.test.bytecode.javassist;

import java.text.ParseException;

import junit.framework.TestSuite;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.cfg.Environment;
import org.hibernate.junit.functional.FunctionalTestCase;
import org.hibernate.junit.functional.FunctionalTestClassTestSuite;
import org.hibernate.test.bytecode.Bean;

/**
 * Test that the Javassist-based lazy initializer properly handles
 * InvocationTargetExceptions
 *
 * @author Steve Ebersole
 */
public class InvocationTargetExceptionTest extends FunctionalTestCase {
	public InvocationTargetExceptionTest(String name) {
		super( name );
	}

	public String[] getMappings() {
		return new String[] { "bytecode/Bean.hbm.xml" };
	}

	public static TestSuite suite() {
		return new FunctionalTestClassTestSuite( InvocationTargetExceptionTest.class );
	}

	public void testProxiedInvocationException() {
		if ( !( Environment.getBytecodeProvider() instanceof org.hibernate.bytecode.javassist.BytecodeProviderImpl ) ) {
			// because of the scoping :(
			reportSkip(
					"env not configured for javassist provider", "bytecode-provider InvocationTargetException handling"
			);
			return;
		}
		Session s = openSession();
		s.beginTransaction();
		Bean bean = new Bean();
		bean.setSomeString( "my-bean" );
		s.save( bean );
		s.getTransaction().commit();
		s.close();

		s = openSession();
		s.beginTransaction();
		bean = ( Bean ) s.load( Bean.class, bean.getSomeString() );
		assertFalse( Hibernate.isInitialized( bean ) );
		try {
			bean.throwException();
			fail( "exception not thrown" );
		}
		catch ( ParseException e ) {
			// expected behavior
		}
		catch ( Throwable t ) {
			fail( "unexpected exception type : " + t );
		}

		s.delete( bean );
		s.getTransaction().commit();
		s.close();
	}
}
