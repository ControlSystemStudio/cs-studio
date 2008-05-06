package de.c1wps.desy.ams.allgemeines.contract;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AlgemeinesContract_SubSystemTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				AlgemeinesContract_SubSystemTestSuite.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(Contract_Test.class);
		//$JUnit-END$
		return suite;
	}

}
