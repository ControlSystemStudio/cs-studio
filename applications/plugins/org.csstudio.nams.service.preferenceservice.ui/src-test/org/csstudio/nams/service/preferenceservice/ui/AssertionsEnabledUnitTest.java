package org.csstudio.nams.service.preferenceservice.ui;

import org.junit.Assert;
import org.junit.Test;

public class AssertionsEnabledUnitTest {

	@Test
	public void testAssertionsActivated() {
		try {
			assert false : "Ok, assertions are enabled.";
			Assert.fail("Not ok, assertions are not enabled!");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Ok, assertions are enabled!", ae.getMessage());
		}
	}
}
