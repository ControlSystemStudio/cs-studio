package org.csstudio.language.script;

import org.csstudio.language.script.parser.statementParser.AllStatementParserTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	
	public static Test suite() {
		final TestSuite suite = new TestSuite("Test for org.csstudio.language.script");
		// $JUnit-BEGIN$
		suite.addTest(AllStatementParserTests.suite());
		// $JUnit-END$
		return suite;
	}

}
