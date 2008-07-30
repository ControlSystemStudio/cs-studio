package org.csstudio.nams.application.department.decision.remote.xmpp;

import junit.framework.Assert;

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

	@Test
	public void testGetCredentials() {
		final XMPPLoginCallbackHandler callbackHandler = this
				.getNewInstanceOfClassUnderTest();

		final Credentials credentials = callbackHandler.getCredentials();
		Assert.assertEquals(
				"nams-decision-department-application-remote-login-user",
				credentials.getUsername());
		Assert.assertEquals("nams!login", credentials.getPassword());

		Assert.assertEquals(1,
				this.loggerMock.mockGetCurrentLogEntries().length);
		final LogEntry[] logEntries = this.loggerMock
				.mockGetCurrentLogEntries();
		Assert
				.assertEquals(LoggerMock.LogType.INFO, logEntries[0]
						.getLogType());
		Assert.assertEquals("Credentials via XMPP requested", logEntries[0]
				.getMessage());
	}

	@Test
	public void testSignalFailedLoginAttempt() {
		final XMPPLoginCallbackHandler callbackHandler = this
				.getNewInstanceOfClassUnderTest();
		callbackHandler.signalFailedLoginAttempt();

		Assert.assertEquals(1,
				this.loggerMock.mockGetCurrentLogEntries().length);
		final LogEntry[] logEntries = this.loggerMock
				.mockGetCurrentLogEntries();
		Assert.assertEquals(LoggerMock.LogType.WARNING, logEntries[0]
				.getLogType());
		Assert
				.assertEquals(
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
				this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest() };
	}

	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		XMPPLoginCallbackHandler.staticInject(null);
		try {
			new XMPPLoginCallbackHandler();
			Assert.fail("RuntimeException expected!");
		} catch (final RuntimeException r) {
			// Ok, das war erwartet...
		}

		this.loggerMock = new LoggerMock();
		XMPPLoginCallbackHandler.staticInject(this.loggerMock);
	}

	@After
	@Override
	protected void tearDown() throws Exception {
		XMPPLoginCallbackHandler.staticInject(null);
		this.loggerMock = null;
		super.tearDown();
	}

}
