package org.csstudio.diag.icsiocmonitor.ui.internal.model;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( { IocMonitorTest.class,
	MonitorItemTest.class,
	MonitorReportTest.class})
public class AllTests {
}

//public class AllTests {
//
//	public static Test suite() {
//		final TestSuite suite = new TestSuite(
//				"Test for de.desy.language.snl");
//		// $JUnit-BEGIN$
//
//		suite.addTestSuite(SNLMethodRule_Test.class);
//		suite.addTestSuite(EmbeddedCCodeRule_Test.class);
//
//		// $JUnit-END$
//		return suite;
//	}
//
//}
