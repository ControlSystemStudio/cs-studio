package org.csstudio.nams.testutils.testsuites;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.service.configurationaccess.localstore.ConfigurationServiceFactoryImpl_DatabaseIntegrationTest_RequiresOracle;

/**
 * MIT ECLEMMA STARTEN!!!
 */
public class AllNewAMSTests_WithDatabase extends TestCase {

	public static Test suite() throws Throwable {
		TestSuite suite = new TestSuite(
				"AllNewAMSTests_WithDatabase");
		// $JUnit-BEGIN$
		suite.addTest(AllNewAMSTests_WithoutDatabase.suite());
		suite.addTestSuite(ConfigurationServiceFactoryImpl_DatabaseIntegrationTest_RequiresOracle.class);
		
		// $JUnit-END$
		return suite;
	}
}
