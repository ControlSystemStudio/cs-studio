package org.csstudio.nams.application.department.decision.remote.xmpp;

import java.util.Map;

import org.csstudio.nams.application.department.decision.remote.RemotelyStoppable;
import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.platform.libs.dcf.actions.IAction;

/**
 * The remote shutdown action for decision department.
 */
public class XMPPRemoteShutdownAction implements IAction {

	private static String ACTION_LOGIN_FAILED = XMPPLoginCallbackHandler.LOGIN_FAILED
			+ " [requested action: \"shutdown\"]";
	private static String ACTION_LOGIN_SUCCEDED = "OK: [0] - Login succeded for user "
			+ XMPPLoginCallbackHandler.USER
			+ ", shutdown has been initiated [requested action: \"shutdown\"]";
	private static Logger logger;
	private static RemotelyStoppable thingToBeStopped;

	/**
	 * Injection of logger. Note: This method have to be called before any
	 * instance of this class is created!
	 */
	public static void staticInject(final Logger logger) {
		XMPPRemoteShutdownAction.logger = logger;
	}

	/**
	 * Injection of stoppable thing. Note: This method have to be called before
	 * any instance of this class is created!
	 */
	public static void staticInject(final RemotelyStoppable thingToBeStopped) {
		XMPPRemoteShutdownAction.thingToBeStopped = thingToBeStopped;
	}

	public XMPPRemoteShutdownAction() {
		if (XMPPRemoteShutdownAction.logger == null) {
			throw new RuntimeException(
					"Class has not been intialized. Expected call of staticInject(Logger) before instantation.");
		}
		if (XMPPRemoteShutdownAction.thingToBeStopped == null) {
			throw new RuntimeException(
					"Class has not been intialized. Expected call of staticInject(RemotelyStoppable) before instantation.");
		}
	}

	/**
	 * Performs the action. Expects a {@link Map} as param. The map must only
	 * contain a value keyed by the String "authorisation". The contained value
	 * must be a string formatted as "user=password".
	 * 
	 * @param A
	 *            {@link Map} as described above, not null.
	 * @return A String containing a status message.
	 */
	public Object run(final Object param) {
		Contract.requireNotNull("param", param);
		Contract.require(param instanceof Map, "param instanceof Map");
		final Map<Object, Object> paramMap = this.castToMap(param);
		Contract.require(paramMap.containsKey("Password"),
				"((Map)param).containsKey(\"Password\")");

		final String value = paramMap.get("Password").toString();
		// Not necessary  
		//final String[] valueParts = value.split("=");

		/*
		 * This does not work.
		if (valueParts.length == 2) {
			if (XMPPLoginCallbackHandler.USER.equals(valueParts[0])) {
				if (XMPPLoginCallbackHandler.PASSWORD.equals(valueParts[1])) {
					XMPPRemoteShutdownAction.thingToBeStopped
							.stopRemotely(XMPPRemoteShutdownAction.logger);
					XMPPRemoteShutdownAction.logger.logInfoMessage(this,
							XMPPRemoteShutdownAction.ACTION_LOGIN_SUCCEDED);
					return XMPPRemoteShutdownAction.ACTION_LOGIN_SUCCEDED;
				}
			}
		}
        */
		
		if(XMPPLoginCallbackHandler.ADMIN_PASSWORD.equals(value))
		{
            XMPPRemoteShutdownAction.thingToBeStopped
                    .stopRemotely(XMPPRemoteShutdownAction.logger);
            
            XMPPRemoteShutdownAction.logger.logInfoMessage(this,
                    XMPPRemoteShutdownAction.ACTION_LOGIN_SUCCEDED);
            
            return XMPPRemoteShutdownAction.ACTION_LOGIN_SUCCEDED;
		}
		
		XMPPRemoteShutdownAction.logger.logWarningMessage(this,
				XMPPRemoteShutdownAction.ACTION_LOGIN_FAILED);
		
		return XMPPRemoteShutdownAction.ACTION_LOGIN_FAILED;
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> castToMap(final Object param) {
		return (Map) param;
	}

}
