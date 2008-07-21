package org.csstudio.nams.service.preferenceservice;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class PreferenceServiceAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		
		TestSuite suite = new TestSuite("PreferenceServiceAllTestsSuite");
		//$JUnit-BEGIN$

		suite.addTestSuite(PreferenceServiceAllTestsSuite.class);


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
