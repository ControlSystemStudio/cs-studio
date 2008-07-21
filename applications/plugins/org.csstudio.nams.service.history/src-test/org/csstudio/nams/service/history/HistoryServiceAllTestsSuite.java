package org.csstudio.nams.service.history;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HistoryServiceAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		
		TestSuite suite = new TestSuite("HistoryServiceAllTestsSuite");
		//$JUnit-BEGIN$

		suite.addTestSuite(HistoryServiceAllTestsSuite.class);
		
		suite.addTestSuite(HistoryActivator_Test.class);

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
