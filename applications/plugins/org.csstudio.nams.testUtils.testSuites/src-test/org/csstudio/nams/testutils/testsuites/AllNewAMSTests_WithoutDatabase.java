package org.csstudio.nams.testutils.testsuites;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.Test;

/**
 * MIT ECLEMMA STARTEN!!!
 */
public class AllNewAMSTests_WithoutDatabase extends TestCase {

	public static junit.framework.Test suite() throws Throwable {
		TestSuite suite = new TestSuite(
				"AllNewAMSTests_WithoutDatabase");
		// $JUnit-BEGIN$
		suite.addTestSuite(AllNewAMSTests_WithoutDatabase.class);
		suite.addTest(org.csstudio.nams.application.department.decision.DecisionDepartmentAllTestsSuite.suite());
		
		return suite;
	}
	
	@Test
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
