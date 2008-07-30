package org.csstudio.nams.service.regelwerkbuilder;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RegelwerksbuilderAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {

		final TestSuite suite = new TestSuite("PreferenceServiceAllTestsSuite");
		// $JUnit-BEGIN$

		suite.addTestSuite(RegelwerksbuilderAllTestsSuite.class);

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
