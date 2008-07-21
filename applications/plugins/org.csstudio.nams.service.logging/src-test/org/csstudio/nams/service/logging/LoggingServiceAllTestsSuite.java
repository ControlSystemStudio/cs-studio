package org.csstudio.nams.service.logging;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.service.logging.declaration.LoggerMock_Test;

public class LoggingServiceAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		
		TestSuite suite = new TestSuite("LoggingServiceAllTestsSuite");
		//$JUnit-BEGIN$

		suite.addTestSuite(LoggingServiceAllTestsSuite.class);
		
		suite.addTestSuite(LoggingServiceActivator_Test.class);
		
		suite.addTestSuite(LoggerMock_Test.class);
		

		//$JUnit-END$
		return suite;
	}

	@org.junit.Test
	public void testAssertionsAktiviert()
	{
		try {
			assert false : "Ok, Assertions sind aktiviert!";
			fail("Nein, Assertions sind nicht aktiviert");
		} catch(AssertionError ae) {
			assertEquals("Ok, Assertions sind aktiviert!", ae.getMessage());
		}
	}
}
