package org.csstudio.nams.application.department.decision.remote.xmpp;

import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;

/**
 * The remote login handler used used for XMPP-logins to decision department.
 */
public class XMPPLoginCallbackHandler implements ILoginCallbackHandler {

	private static final String CREDENTIALS_VIA_XMPP_REQUESTED_MESSAGE = "Credentials via XMPP requested";
	static final String LOGIN_FAILED = "Possible hacking attempt: XMPP-remote-login: Authorization failed! (no details avail)";
	static final String PASSWORD = "nams!login";
	static final String USER = "nams-decision-department-application-remote-login-user";
	private static Logger logger;

	public XMPPLoginCallbackHandler() {
		if (XMPPLoginCallbackHandler.logger == null) {
			throw new RuntimeException(
					"Class has not been intialized. Expected call of staticInject(Logger) before instantation.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Credentials getCredentials() {
		logger.logInfoMessage(this, CREDENTIALS_VIA_XMPP_REQUESTED_MESSAGE);
		return new Credentials(USER, PASSWORD);
	}

	/**
	 * {@inheritDoc}
	 */
	public void signalFailedLoginAttempt() {
		XMPPLoginCallbackHandler.logger
				.logWarningMessage(
						this,
						LOGIN_FAILED);
	}

	/**
	 * Injection of logger. Note: This method have to be called before any
	 * instance of this class is created!
	 */
	public static void staticInject(Logger logger) {
		XMPPLoginCallbackHandler.logger = logger;
	}

}
