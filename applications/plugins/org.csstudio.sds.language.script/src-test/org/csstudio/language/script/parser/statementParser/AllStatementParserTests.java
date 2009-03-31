package org.csstudio.language.script.parser.statementParser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllStatementParserTests {
	
	public static Test suite() {
		final TestSuite suite = new TestSuite("Test for org.csstudio.language.script");
		// $JUnit-BEGIN$
		suite.addTestSuite(VariableParser_Test.class);
		// $JUnit-END$
		return suite;
	}

}
