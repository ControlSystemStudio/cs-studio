package de.c1wps.desy.ams.allgemeines.regelwerk;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.common.material.Regelwerkskennung_Test;


public class AllgemeinesRegelwerk_SubSystemTestSuite extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for de.c1wps.desy.ams.AllgmeinesRegelwerk");
		//$JUnit-BEGIN$
		suite.addTestSuite(Pruefliste_Test.class);
		suite.addTestSuite(StandardRegelwerk_Test.class);
		suite.addTestSuite(WeiteresVersandVorgehen_Test.class);
		suite.addTestSuite(RegelErgebnis_Test.class);
		suite.addTestSuite(Regelwerkskennung_Test.class);
		suite.addTestSuite(UndVersandRegel_Test.class);
		suite.addTestSuite(OderVersandRegel_Test.class);
		suite.addTestSuite(NichtVersandRegel_Test.class);
		suite.addTestSuite(AbstractNodeVersandRegel_Test.class);
		suite.addTestSuite(TimeBasedRegel_Test.class);
		suite.addTestSuite(TimeBasedRegelAlarmBeiBestaetigung_Test.class);
		suite.addTestSuite(AlarmNachricht_Test.class);
		suite.addTestSuite(ProcessVariableRegel_Test.class);
		suite.addTestSuite(StringRegel_Test.class);
		//$JUnit-END$
		return suite;
	}

}
