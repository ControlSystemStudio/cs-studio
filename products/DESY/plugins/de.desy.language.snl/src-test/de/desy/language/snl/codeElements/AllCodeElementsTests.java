package de.desy.language.snl.codeElements;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCodeElementsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for de.desy.language.snl.codeElements");
		//$JUnit-BEGIN$
		suite.addTestSuite(RegExToMatchSyncDeclaration_Test.class);
		suite.addTestSuite(RegExToMatchSingleComment_Test.class);
		suite.addTestSuite(RegExToMatchEventFlagDeclaration_Test.class);
		suite.addTestSuite(RegExToMatchWhenStatements_Test.class);
		suite.addTestSuite(RegExToMatchMultiLineEmbeddedC_Test.class);
		suite.addTestSuite(FindWithRegExInStringExperiment_Text.class);
		suite.addTestSuite(RegExToMatchStateSetStatements_Test.class);
		suite.addTestSuite(RegExToMatchMultiLineComment_Test.class);
		suite.addTestSuite(RegExToMatchMonitorDeclaration_Test.class);
		suite.addTestSuite(RegExToMatchStateStatements_Test.class);
		suite.addTestSuite(RegExToMatchSingleEmbeddedC_Test.class);
		suite.addTestSuite(RegExToMatchProgramStatement_Test.class);
		suite.addTestSuite(RegExToMatchVariableDeclaration_Test.class);
		suite.addTestSuite(RegExToMatchAssignDeclaration_Test.class);
		suite.addTestSuite(RegExToMatchTextWithoutSubsequent_Test.class);
		//$JUnit-END$
		return suite;
	}

}
