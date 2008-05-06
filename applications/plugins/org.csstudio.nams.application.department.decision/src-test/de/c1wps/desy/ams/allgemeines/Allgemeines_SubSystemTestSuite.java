package de.c1wps.desy.ams.allgemeines;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Allgemeines_SubSystemTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for de.c1wps.desy.ams.allgemeines");
		//$JUnit-BEGIN$
		suite.addTestSuite(StandardAblagekorb_Test.class);
		suite.addTestSuite(Vorgangsmappe_Test.class);
		suite.addTestSuite(AlarmNachricht_Test.class);
		suite.addTestSuite(Millisekunden_Test.class);
		suite.addTestSuite(Vorgangsmappenkennung_Test.class);
		//$JUnit-END$
		return suite;
	}

}
