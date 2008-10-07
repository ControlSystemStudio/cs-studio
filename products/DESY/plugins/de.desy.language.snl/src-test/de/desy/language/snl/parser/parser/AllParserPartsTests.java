package de.desy.language.snl.parser.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllParserPartsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for de.desy.language.snl.parser.parser");
		//$JUnit-BEGIN$
		suite.addTestSuite(EntryParser_Test.class);
		suite.addTestSuite(CharParser_Test.class);
		suite.addTestSuite(WhenParser_Test.class);
		suite.addTestSuite(SingleLineCommentParser_Test.class);
		suite.addTestSuite(BlockStatementParser_Test.class);
		suite.addTestSuite(OptionStatementParser_Test.class);
		suite.addTestSuite(MonitorStatementParser_Test.class);
		suite.addTestSuite(SingleLineEmbeddedCParser_Test.class);
		suite.addTestSuite(EventFlagParser_Test.class);
		suite.addTestSuite(StateSetParser_Test.class);
		suite.addTestSuite(ExitParser_Test.class);
		suite.addTestSuite(ProgramParser_Test.class);
		suite.addTestSuite(ConditionStatementParser_Test.class);
		suite.addTestSuite(StateParser_Test.class);
		suite.addTestSuite(AssignStatementParser_Test.class);
		suite.addTestSuite(SyncStatementParser_Test.class);
		suite.addTestSuite(VariableParser_Test.class);
		suite.addTestSuite(StringParser_Test.class);
		//$JUnit-END$
		return suite;
	}

}
