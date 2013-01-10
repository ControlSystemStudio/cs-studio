package org.csstudio.utility.adlparser.fileParser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(ADLWidgetTest.class);
		suite.addTestSuite(ColorMapTest.class);
		suite.addTestSuite(FileLineTest.class);
		suite.addTestSuite(LinePartsTest.class);
		suite.addTestSuite(ADLResourceTest.class);
		//$JUnit-END$
		return suite;
	}

}
