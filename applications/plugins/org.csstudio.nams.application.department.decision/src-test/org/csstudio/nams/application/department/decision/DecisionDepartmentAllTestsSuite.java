package org.csstudio.nams.application.department.decision;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.csstudio.nams.application.department.decision.office.decision.Alarmentscheidungsbuero_SubSystemTestSuite;

public class DecisionDepartmentAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		final TestSuite suite = new TestSuite("NAMSCommonAllTestsSuite");
		// $JUnit-BEGIN$
		suite.addTest(Alarmentscheidungsbuero_SubSystemTestSuite.suite());
		suite.addTestSuite(DecisionDepartmentActivator_Test.class);
		suite.addTestSuite(DecisionDepartmentAllTestsSuite.class);
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
