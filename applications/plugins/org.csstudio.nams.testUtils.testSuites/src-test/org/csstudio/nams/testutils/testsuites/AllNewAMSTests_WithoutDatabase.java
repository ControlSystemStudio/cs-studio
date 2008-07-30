package org.csstudio.nams.testutils.testsuites;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.application.department.decision.DecisionDepartmentAllTestsSuite;
import org.csstudio.nams.common.NAMSCommonAllTestsSuite;
import org.csstudio.nams.configurator.NAMSNewConfiguratorAllTestsSuite;
import org.csstudio.nams.service.configurationaccess.localstore.ConfigurationaccessLocalStoreWithoutDBAllTestsSuite;
import org.csstudio.nams.service.history.HistoryServiceAllTestsSuite;
import org.csstudio.nams.service.history.impl.confstore.HistoryServiceConfStroeImplAllTestsSuite;
import org.csstudio.nams.service.logging.LoggingServiceAllTestsSuite;
import org.csstudio.nams.service.messaging.MessagingServiceAllTestsSuite;
import org.csstudio.nams.service.messaging.impl.jms.MessagingServiceJMSImplAllTestsSuite;
import org.csstudio.nams.service.preferenceservice.PreferenceServiceAllTestsSuite;
import org.csstudio.nams.service.preferenceservice.ui.PreferenceServiceUIAllTestsSuite;
import org.csstudio.nams.service.regelwerkbuilder.RegelwerksbuilderAllTestsSuite;
import org.csstudio.nams.service.regelwerkbuilder.impl.confstore.RegelwerksbuilderConfStoreImplAllTestsSuite;
import org.junit.Test;

/**
 * MIT ECLEMMA STARTEN!!!
 */
public class AllNewAMSTests_WithoutDatabase extends TestCase {

	public static junit.framework.Test suite() throws Throwable {
		final TestSuite suite = new TestSuite("AllNewAMSTests_WithoutDatabase");
		// $JUnit-BEGIN$
		suite.addTestSuite(AllNewAMSTests_WithoutDatabase.class);
		suite.addTest(DecisionDepartmentAllTestsSuite.suite());
		suite.addTest(NAMSCommonAllTestsSuite.suite());
		suite.addTest(NAMSNewConfiguratorAllTestsSuite.suite());
		suite.addTest(ConfigurationaccessLocalStoreWithoutDBAllTestsSuite
				.suite());
		suite.addTest(HistoryServiceAllTestsSuite.suite());
		suite.addTest(HistoryServiceConfStroeImplAllTestsSuite.suite());
		suite.addTest(LoggingServiceAllTestsSuite.suite());
		suite.addTest(MessagingServiceAllTestsSuite.suite());
		suite.addTest(MessagingServiceJMSImplAllTestsSuite.suite());
		suite.addTest(PreferenceServiceAllTestsSuite.suite());
		suite.addTest(PreferenceServiceUIAllTestsSuite.suite());
		suite.addTest(RegelwerksbuilderAllTestsSuite.suite());
		suite.addTest(RegelwerksbuilderConfStoreImplAllTestsSuite.suite());
		// $JUnit-END$
		return suite;
	}

	@Test
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
