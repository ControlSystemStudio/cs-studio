package org.csstudio.language.script;

import org.csstudio.language.script.parser.statementParser.AllStatementParserTests;
import org.csstudio.language.script.parser.statementParser.VariableParser_Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { VariableParser_Test.class,
	AllStatementParserTests.class} )
public class AllTests {
}

//public class AllTests {
//	
//	public static Test suite() {
//		final TestSuite suite = new TestSuite("Test for org.csstudio.language.script");
//		// $JUnit-BEGIN$
//		suite.addTest(AllStatementParserTests.suite());
//		// $JUnit-END$
//		return suite;
//	}
//
//}
