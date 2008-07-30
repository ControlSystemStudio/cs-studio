package org.csstudio.nams.service.configurationaccess.localstore;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicConfigurationId_Test;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO_Test;

public class ConfigurationaccessLocalStoreWithoutDBAllTestsSuite extends
		TestCase {

	public static Test suite() throws Throwable {

		final TestSuite suite = new TestSuite(
				"ConfigurationaccessLocalStoreWithoutDBAllTestsSuite");
		// $JUnit-BEGIN$

		suite
				.addTestSuite(ConfigurationaccessLocalStoreWithoutDBAllTestsSuite.class);

		suite.addTestSuite(LocalConfigurationStoreServiceActivator_Test.class);
		suite.addTestSuite(TopicConfigurationId_Test.class);

		suite.addTestSuite(StringFilterConditionDTO_Test.class);

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
