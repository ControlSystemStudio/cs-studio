package org.csstudio.nams.service.history;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HistoryServiceAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {

		final TestSuite suite = new TestSuite("HistoryServiceAllTestsSuite");
		// $JUnit-BEGIN$

		suite.addTestSuite(HistoryServiceAllTestsSuite.class);

		suite.addTestSuite(HistoryActivator_Test.class);

		// $JUnit-END$
		return suite;
	}

	@org.junit.Test
	public void testAssertionsAktiviert() {
		try {
			assert false : "Ok, Assertions sind aktiviert!";
			Assert.fail("Nein, Assertions sind nicht aktiviert");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Ok, Assertions sind aktiviert!", ae
					.getMessage());
		}
	}
}
