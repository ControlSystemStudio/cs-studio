package org.csstudio.nams.service.configurationaccess.localstore;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.TopicConfigurationId_Test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConfigurationaccessLocalStoreWithoutDBAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		
		TestSuite suite = new TestSuite("ConfigurationaccessLocalStoreWithoutDBAllTestsSuite");
		//$JUnit-BEGIN$

		suite.addTestSuite(ConfigurationaccessLocalStoreWithoutDBAllTestsSuite.class);
		
		suite.addTestSuite(LocalConfigurationStoreServiceActivator_Test.class);
		suite.addTestSuite(TopicConfigurationId_Test.class);
		
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
