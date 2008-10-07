package de.desy.language.libraries;

import de.desy.language.libraries.utils.contract.Contract_Test;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.desy.language.libraries");
		//$JUnit-BEGIN$
		suite.addTestSuite(Contract_Test.class);
		//$JUnit-END$
		return suite;
	}

}
