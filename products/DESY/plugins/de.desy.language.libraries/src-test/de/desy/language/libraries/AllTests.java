package de.desy.language.libraries;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.desy.language.libraries.utils.contract.Contract_Test;


@RunWith(Suite.class)
@SuiteClasses( { Contract_Test.class})
public class AllTests {
}
//public class AllTests {
//
//	public static Test suite() {
//		TestSuite suite = new TestSuite("Test for de.desy.language.libraries");
//		//$JUnit-BEGIN$
//		suite.addTestSuite(Contract_Test.class);
//		//$JUnit-END$
//		return suite;
//	}
//
//}
