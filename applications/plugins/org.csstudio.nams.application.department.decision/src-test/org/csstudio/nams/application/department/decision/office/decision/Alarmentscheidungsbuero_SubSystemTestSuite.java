package org.csstudio.nams.application.department.decision.office.decision;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Alarmentscheidungsbuero_SubSystemTestSuite extends TestCase {

	public static Test suite() {
		final TestSuite suite = new TestSuite(
				"Alarmentscheidungsbuero_SubSystemTestSuite");
		// $JUnit-BEGIN$
		suite.addTestSuite(AlarmEntscheidungsBuero_Test.class);
		suite.addTestSuite(Abteilungsleiter_Test.class);
		suite.addTestSuite(Sachbearbeiter_Test.class);
		suite.addTestSuite(TerminAssistenz_Test.class);
		suite.addTestSuite(Terminnotiz_Test.class);
		// $JUnit-END$
		return suite;
	}

}
