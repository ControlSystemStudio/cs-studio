package de.desy.language.snl;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.desy.language.snl.codeElements.AllCodeElementsTests;
import de.desy.language.snl.parser.AllParserTests;
import de.desy.language.snl.parser.nodes.AllNodesTests;
import de.desy.language.snl.parser.parser.AllParserPartsTests;

public class AllTests {

	public static Test suite() {
		final TestSuite suite = new TestSuite("Test for de.desy.language.snl");
		// $JUnit-BEGIN$
		suite.addTest(AllCodeElementsTests.suite());
		suite.addTest(AllParserTests.suite());
		suite.addTest(AllNodesTests.suite());
		suite.addTest(AllParserPartsTests.suite());
		// $JUnit-END$
		return suite;
	}
}
