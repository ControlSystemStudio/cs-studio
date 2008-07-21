package org.csstudio.nams.configurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.configurator.editor.FilterTreeContentProvider_Test;
import org.csstudio.nams.configurator.service.ConfigurationBeanServiceImpl_Test;
import org.csstudio.nams.configurator.service.synchronize.SynchronizeServiceImpl_Test;

public class NAMSNewConfiguratorAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		TestSuite suite = new TestSuite("ConfigurationaccessLocalStoreWithoutDBAllTestsSuite");
		//$JUnit-BEGIN$

		suite.addTestSuite(NAMSNewConfiguratorAllTestsSuite.class);
		
		suite.addTestSuite(FilterTreeContentProvider_Test.class);
		suite.addTestSuite(SynchronizeServiceImpl_Test.class);
		suite.addTestSuite(ConfigurationBeanServiceImpl_Test.class);
		
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
