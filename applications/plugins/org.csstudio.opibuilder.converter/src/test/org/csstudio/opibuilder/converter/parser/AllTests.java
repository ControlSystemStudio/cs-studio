package org.csstudio.opibuilder.converter.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.csstudio.opibuilder.converter.parser");
		//$JUnit-BEGIN$
		suite.addTestSuite(EdmParserTest.class);
		suite.addTestSuite(EdmDisplayParserTest.class);
		suite.addTestSuite(EdmFontsListParserTest.class);
		suite.addTestSuite(EdmColorsListParserTest.class);
		//$JUnit-END$
		return suite;
	}

}
