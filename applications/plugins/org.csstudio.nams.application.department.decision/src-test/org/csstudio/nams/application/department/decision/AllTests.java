package org.csstudio.nams.application.department.decision;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.application.department.decision.office.decision.Abteilungsleiter_Test;
import org.csstudio.nams.application.department.decision.office.decision.AlarmEntscheidungsBuero_Test;
import org.csstudio.nams.application.department.decision.office.decision.Sachbearbeiter_Test;
import org.csstudio.nams.application.department.decision.office.decision.TerminAssistenz_Test;
import org.csstudio.nams.application.department.decision.office.decision.Terminnotiz_Test;

public class AllTests extends TestCase {

	public static Test suite() throws Throwable {
		final TestSuite suite = new TestSuite("NAMSCommonAllTestsSuite");
		// $JUnit-BEGIN$
	    suite.addTestSuite(AlarmEntscheidungsBuero_Test.class);
	    suite.addTestSuite(Abteilungsleiter_Test.class);
	    suite.addTestSuite(Sachbearbeiter_Test.class);
	    suite.addTestSuite(TerminAssistenz_Test.class);
	    suite.addTestSuite(Terminnotiz_Test.class);

	    suite.addTestSuite(DecisionDepartmentActivator_Test.class);
		suite.addTestSuite(SyncronisationsAutomat_Test.class);
		suite.addTestSuite(ThreadTypesOfDecisionDepartment_Test.class);
		// $JUnit-END$
		return suite;
	}

	@org.junit.Test
	public void testAssertionsAktiviert() {
		try {
			assert false : "Ok, Assertions sind aktiviert!";
			Assert.fail("Nein, Assertions sind nicht aktiviert");
		} catch (final AssertionError ae) {
			Assert.assertEquals("Ok, Assertions sind aktiviert!", ae
					.getMessage());
		}
	}
}
