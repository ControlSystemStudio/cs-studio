package de.desy.language.snl;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.desy.language.snl.ui.rules.EmbeddedCCodeRule_Test;
import de.desy.language.snl.ui.rules.SNLMethodRule_Test;


@RunWith(Suite.class)
@SuiteClasses( { EmbeddedCCodeRule_Test.class,
	SNLMethodRule_Test.class})
public class AllTests {
}

//public class AllTests {
//
//	public static Test suite() {
//		final TestSuite suite = new TestSuite(
//				"Test for de.desy.language.snl");
//		// $JUnit-BEGIN$
//
//		suite.addTestSuite(SNLMethodRule_Test.class);
//		suite.addTestSuite(EmbeddedCCodeRule_Test.class);
//
//		// $JUnit-END$
//		return suite;
//	}
//
//}
