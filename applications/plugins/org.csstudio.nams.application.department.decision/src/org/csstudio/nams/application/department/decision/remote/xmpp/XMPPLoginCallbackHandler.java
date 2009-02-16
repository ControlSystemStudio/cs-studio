package org.csstudio.nams.application.department.decision.remote.xmpp;

import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;

/**
 * The remote login handler used used for XMPP-logins to decision department.
 */
public class XMPPLoginCallbackHandler implements ILoginCallbackHandler {

	static final String LOGIN_FAILED = "ERROR: [3] - Possible hacking attempt: XMPP-remote-login: Authorization failed! (no details avail)";
	static final String PASSWORD = "ams";
	static final String USER = "ams-department-decision";
	private static final String CREDENTIALS_VIA_XMPP_REQUESTED_MESSAGE = "Credentials via XMPP requested";
	private static Logger logger;

	/**
	 * Injection of logger. Note: This method have to be called before any
	 * instance of this class is created!
	 */
	public static void staticInject(final Logger logger) {
		XMPPLoginCallbackHandler.logger = logger;
	}

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
		XMPPLoginCallbackHandler.logger
				.logInfoMessage(
						this,
						XMPPLoginCallbackHandler.CREDENTIALS_VIA_XMPP_REQUESTED_MESSAGE);
		return new Credentials(XMPPLoginCallbackHandler.USER,
				XMPPLoginCallbackHandler.PASSWORD);
	}

	/**
	 * {@inheritDoc}
	 */
	public void signalFailedLoginAttempt() {
		XMPPLoginCallbackHandler.logger.logWarningMessage(this,
				XMPPLoginCallbackHandler.LOGIN_FAILED);
	}

}
