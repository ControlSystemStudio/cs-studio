package org.csstudio.diag.interconnectionServer;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.preferences.XMLStore;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;

public class LoginCallbackHandler1 implements ILoginCallbackHandler {

	public Credentials getCredentials() {
		
		//get properties from xml store.
		XMLStore store = XMLStore.getInstance();
		String xmppUserName = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
				"xmppUserName", false);
		String xmppPassword = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences", 
				"xmppPassword", false);
		
		return new Credentials(xmppUserName, xmppPassword);
	}

	public void signalFailedLoginAttempt() {
		CentralLogger.getInstance().error(this, "XMPP login failed");
	}

}
