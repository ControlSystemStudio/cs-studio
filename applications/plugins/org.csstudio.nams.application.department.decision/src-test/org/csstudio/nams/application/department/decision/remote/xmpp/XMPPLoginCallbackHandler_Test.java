package org.csstudio.nams.application.department.decision.remote.xmpp;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.csstudio.nams.service.logging.declaration.LoggerMock.LogEntry;
import org.csstudio.platform.security.Credentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XMPPLoginCallbackHandler_Test extends
		AbstractObject_TestCase<XMPPLoginCallbackHandler> {

	private LoggerMock loggerMock;

	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		XMPPLoginCallbackHandler.staticInject(null);
		try {
			new XMPPLoginCallbackHandler();
			fail("RuntimeException expected!");
		} catch (RuntimeException r) {
			// Ok, das war erwartet...
		}

		loggerMock = new LoggerMock();
		XMPPLoginCallbackHandler.staticInject(loggerMock);
	}

	@After
	@Override
	protected void tearDown() throws Exception {
		XMPPLoginCallbackHandler.staticInject(null);
		loggerMock = null;
		super.tearDown();
	}

	@Test
	public void testGetCredentials() {
		XMPPLoginCallbackHandler callbackHandler = getNewInstanceOfClassUnderTest();

		Credentials credentials = callbackHandler.getCredentials();
		assertEquals("nams-decision-department-application-remote-login-user",
				credentials.getUsername());
		assertEquals("nams!login", credentials.getPassword());

		assertEquals(1, loggerMock.mockGetCurrentLogEntries().length);
		LogEntry[] logEntries = loggerMock.mockGetCurrentLogEntries();
		assertEquals(LoggerMock.LogType.INFO, logEntries[0].getLogType());
		assertEquals("Credentials via XMPP requested", logEntries[0]
				.getMessage());
	}

	@Test
	public void testSignalFailedLoginAttempt() {
		XMPPLoginCallbackHandler callbackHandler = getNewInstanceOfClassUnderTest();
		callbackHandler.signalFailedLoginAttempt();

		assertEquals(1, loggerMock.mockGetCurrentLogEntries().length);
		LogEntry[] logEntries = loggerMock.mockGetCurrentLogEntries();
		assertEquals(LoggerMock.LogType.WARNING, logEntries[0].getLogType());
		assertEquals(
				"Possible hacking attempt: XMPP-remote-login: Authorization failed! (no details avail)",
				logEntries[0].getMessage());
	}

	@Override
	protected XMPPLoginCallbackHandler getNewInstanceOfClassUnderTest() {
		return new XMPPLoginCallbackHandler();
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected XMPPLoginCallbackHandler[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new XMPPLoginCallbackHandler[] {
				getNewInstanceOfClassUnderTest(),
				getNewInstanceOfClassUnderTest(),
				getNewInstanceOfClassUnderTest() };
	}

}
