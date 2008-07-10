package org.csstudio.nams.application.department.decision.remote.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.application.department.decision.remote.RemotelyStoppable;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.junit.Test;

public class XMPPRemoteShutdownAction_Test extends
		AbstractObject_TestCase<XMPPRemoteShutdownAction> {

	boolean stopCalled = false;
	RemotelyStoppable stoppable = new TestRemotelyStoppaböe();
	private LoggerMock loggerMock = new LoggerMock();

	class TestRemotelyStoppaböe implements RemotelyStoppable {
		public void stopRemotely(Logger logger) {
			stopCalled = true;
		}
	}

	@Test
	public void testRun() {
		XMPPRemoteShutdownAction action = getNewInstanceOfClassUnderTest();

		assertNotNull(action);
		assertFalse(stopCalled);

		// Illegal arguments...
		Object invalidParam = "Hallo Welt!";
		try {
			action.run(invalidParam);
		} catch (AssertionError ae) {
			// OK!
		}
		assertFalse(stopCalled);

		Object anotherInvalidParam = 42;
		try {
			action.run(anotherInvalidParam);
		} catch (AssertionError ae) {
			// OK!
		}
		assertFalse(stopCalled);

		Map<String, String> invalidMap = new HashMap<String, String>();
		invalidMap.put("XXO", "XXO");
		try {
			action.run(invalidMap);
		} catch (AssertionError ae) {
			// OK!
		}
		assertFalse(stopCalled);

		// Syntacticly correct param!
		Map<String, String> anotherInvalidMap = new HashMap<String, String>();
		anotherInvalidMap.put("authorisation", "XXO=Xxo");
		action.run(anotherInvalidMap);
		assertFalse(stopCalled);

		// Valid!
		Map<String, String> validMap = new HashMap<String, String>();
		validMap
				.put("authorisation",
						"nams-decision-department-application-remote-login-user=nams!login");
		action.run(validMap);
		assertTrue(stopCalled);
	}

	@Override
	protected XMPPRemoteShutdownAction getNewInstanceOfClassUnderTest() {
		XMPPRemoteShutdownAction.staticInject(loggerMock);
		XMPPRemoteShutdownAction.staticInject(stoppable);
		return new XMPPRemoteShutdownAction();
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected XMPPRemoteShutdownAction[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		XMPPRemoteShutdownAction[] result = new XMPPRemoteShutdownAction[3];
		result[0] = getNewInstanceOfClassUnderTest();
		result[1] = getNewInstanceOfClassUnderTest();
		result[2] = getNewInstanceOfClassUnderTest();
		return result;
	}

}
