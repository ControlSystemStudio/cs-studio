package org.csstudio.nams.application.department.decision.remote.xmpp;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.csstudio.nams.application.department.decision.remote.RemotelyStoppable;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.junit.Test;

public class XMPPRemoteShutdownAction_Test extends
		AbstractObject_TestCase<XMPPRemoteShutdownAction> {

	class TestRemotelyStoppaböe implements RemotelyStoppable {
		public void stopRemotely(final Logger logger) {
			XMPPRemoteShutdownAction_Test.this.stopCalled = true;
		}
	}

	boolean stopCalled = false;
	RemotelyStoppable stoppable = new TestRemotelyStoppaböe();

	private final LoggerMock loggerMock = new LoggerMock();

	@Test
	public void testRun() {
		final XMPPRemoteShutdownAction action = this
				.getNewInstanceOfClassUnderTest();

		Assert.assertNotNull(action);
		Assert.assertFalse(this.stopCalled);

		// Illegal arguments...
		final Object invalidParam = "Hallo Welt!";
		try {
			action.run(invalidParam);
		} catch (final AssertionError ae) {
			// OK!
		}
		Assert.assertFalse(this.stopCalled);

		final Object anotherInvalidParam = 42;
		try {
			action.run(anotherInvalidParam);
		} catch (final AssertionError ae) {
			// OK!
		}
		Assert.assertFalse(this.stopCalled);

		final Map<String, String> invalidMap = new HashMap<String, String>();
		invalidMap.put("XXO", "XXO");
		try {
			action.run(invalidMap);
		} catch (final AssertionError ae) {
			// OK!
		}
		Assert.assertFalse(this.stopCalled);

		// Syntacticly correct param!
		final Map<String, String> anotherInvalidMap = new HashMap<String, String>();
		anotherInvalidMap.put("authorisation", "XXO=Xxo");
		action.run(anotherInvalidMap);
		Assert.assertFalse(this.stopCalled);

		// Valid!
		final Map<String, String> validMap = new HashMap<String, String>();
		validMap
				.put("authorisation",
						"nams-decision-department-application-remote-login-user=nams!login");
		action.run(validMap);
		Assert.assertTrue(this.stopCalled);
	}

	@Override
	protected XMPPRemoteShutdownAction getNewInstanceOfClassUnderTest() {
		XMPPRemoteShutdownAction.staticInject(this.loggerMock);
		XMPPRemoteShutdownAction.staticInject(this.stoppable);
		return new XMPPRemoteShutdownAction();
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected XMPPRemoteShutdownAction[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final XMPPRemoteShutdownAction[] result = new XMPPRemoteShutdownAction[3];
		result[0] = this.getNewInstanceOfClassUnderTest();
		result[1] = this.getNewInstanceOfClassUnderTest();
		result[2] = this.getNewInstanceOfClassUnderTest();
		return result;
	}

}
