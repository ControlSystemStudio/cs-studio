package de.desy.language.snl.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllParserTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.desy.language.snl.parser");
		//$JUnit-BEGIN$
		suite.addTestSuite(SNLParser_Test.class);
		//$JUnit-END$
		return suite;
	}
}
