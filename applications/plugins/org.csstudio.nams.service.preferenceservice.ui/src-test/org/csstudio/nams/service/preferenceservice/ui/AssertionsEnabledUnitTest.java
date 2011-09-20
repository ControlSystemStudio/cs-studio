package org.csstudio.nams.service.preferenceservice.ui;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AssertionsEnabledUnitTest extends TestCase {

	public static Test suite() throws Throwable {

		final TestSuite suite = new TestSuite(AssertionsEnabledUnitTest.class.getSimpleName());
		// $JUnit-BEGIN$

		suite.addTestSuite(AssertionsEnabledUnitTest.class);

		// $JUnit-END$
		return suite;
	}

	@org.junit.Test
	public void testAssertionsActivated() {
		try {
			assert false : "Ok, assertions are enabled.";
			Assert.fail("Not ok, assertions are not enabled!");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Ok, assertions are enabled!", ae.getMessage());
		}
	}
}
