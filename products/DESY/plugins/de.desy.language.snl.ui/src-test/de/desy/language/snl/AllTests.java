package de.desy.language.snl;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.desy.language.snl.ui.rules.EmbeddedCCodeRule_Test;
import de.desy.language.snl.ui.rules.SNLMethodRule_Test;

public class AllTests {

	public static Test suite() {
		final TestSuite suite = new TestSuite(
				"Test for de.desy.language.snl");
		// $JUnit-BEGIN$

		suite.addTestSuite(SNLMethodRule_Test.class);
		suite.addTestSuite(EmbeddedCCodeRule_Test.class);

		// $JUnit-END$
		return suite;
	}

}
