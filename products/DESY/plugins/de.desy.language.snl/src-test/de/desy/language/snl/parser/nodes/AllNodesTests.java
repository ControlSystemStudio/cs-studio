package de.desy.language.snl.parser.nodes;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllNodesTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for de.desy.language.snl.parser.nodes");
		//$JUnit-BEGIN$
		suite.addTestSuite(EntryNode_Test.class);
		suite.addTestSuite(SyncStatementNode_Test.class);
		suite.addTestSuite(StateNode_Test.class);
		suite.addTestSuite(SingleLineCommentNode_Test.class);
		suite.addTestSuite(AssignStatementNode_Test.class);
		suite.addTestSuite(WhenNode_Test.class);
		suite.addTestSuite(MonitorStatementNode_Test.class);
		suite.addTestSuite(BlockConditionNode_Test.class);
		suite.addTestSuite(OptionStatementNode_Test.class);
		suite.addTestSuite(SingleLineEmbeddedCNode_Test.class);
		suite.addTestSuite(BlockStatementNode_Test.class);
		suite.addTestSuite(EventFlagNode_Test.class);
		suite.addTestSuite(CharNode_Test.class);
		suite.addTestSuite(StateSetNode_Test.class);
		suite.addTestSuite(StringNode_Test.class);
		suite.addTestSuite(ExitNode_Test.class);
		suite.addTestSuite(VariableNode_Test.class);
		//$JUnit-END$
		return suite;
	}

}
