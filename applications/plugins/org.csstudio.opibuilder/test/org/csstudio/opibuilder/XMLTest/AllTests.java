package org.csstudio.opibuilder.XMLTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.csstudio.opibuilder.XMLTest");
		//$JUnit-BEGIN$
		suite.addTestSuite(XMLUtilTest.class);
		//$JUnit-END$
		return suite;
	}

}
