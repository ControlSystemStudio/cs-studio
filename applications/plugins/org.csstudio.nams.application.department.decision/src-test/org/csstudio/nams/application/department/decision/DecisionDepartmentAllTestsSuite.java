package org.csstudio.nams.application.department.decision;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.csstudio.nams.application.department.decision.office.decision.Alarmentscheidungsbuero_SubSystemTestSuite;
import org.csstudio.nams.application.department.decision.remote.xmpp.XMPPLoginCallbackHandler_Test;
import org.csstudio.nams.application.department.decision.remote.xmpp.XMPPRemoteShutdownAction_Test;

public class DecisionDepartmentAllTestsSuite extends TestCase {

	public static Test suite() throws Throwable {
		// TODO System.setErr(new PrintStream(new OutputStreamWriter(new StringWriter())));
		
		TestSuite suite = new TestSuite("DecisionDepartmentAllTestsSuite");
		//$JUnit-BEGIN$
		suite.addTest(Alarmentscheidungsbuero_SubSystemTestSuite.suite());
		suite.addTestSuite(DecisionDepartmentActivator_Test.class);
		suite.addTestSuite(DecisionDepartmentAllTestsSuite.class);
		suite.addTestSuite(SyncronisationsAutomat_Test.class);
		suite.addTestSuite(ThreadTypesOfDecisionDepartment_Test.class);
		suite.addTestSuite(XMPPLoginCallbackHandler_Test.class);
		suite.addTestSuite(XMPPRemoteShutdownAction_Test.class);
		//$JUnit-END$
		return suite;
	}

	@org.junit.Test
	public void testAssertionsAktiviert()
	{
		try {
			assert false : "Ok, Assertions sind aktiviert!";
			fail("Nein, Assertions sind nicht aktiviert");
		} catch(AssertionError ae) {
			assertEquals("Ok, Assertions sind aktiviert!", ae.getMessage());
		}
	}
}
