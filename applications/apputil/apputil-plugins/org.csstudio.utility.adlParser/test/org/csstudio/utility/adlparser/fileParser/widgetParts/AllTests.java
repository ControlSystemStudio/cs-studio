package org.csstudio.utility.adlparser.fileParser.widgetParts;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(ADLBasicAttributeTest.class);
		suite.addTestSuite(ADLChildrenTest.class);
		suite.addTestSuite(ADLControlTest.class);
		suite.addTestSuite(ADLDynamicAttributeTest.class);
		suite.addTestSuite(ADLLimitsTest.class);
//		suite.addTestSuite(ADLMenuItemTest.class);
		suite.addTestSuite(ADLMonitorTest.class);
		suite.addTestSuite(ADLObjectTest.class);
//		suite.addTestSuite(ADLPenTest.class);
//		suite.addTestSuite(ADLPlotcomTest.class);
//		suite.addTestSuite(ADLPlotDataTest.class);
//		suite.addTestSuite(ADLPlotTraceTest.class);
//		suite.addTestSuite(ADLPointsTest.class);
//		suite.addTestSuite(ADLSensitiveTest.class);
		suite.addTestSuite(RelatedDisplayItemTest.class);
		//$JUnit-END$
		return suite;
	}

}
