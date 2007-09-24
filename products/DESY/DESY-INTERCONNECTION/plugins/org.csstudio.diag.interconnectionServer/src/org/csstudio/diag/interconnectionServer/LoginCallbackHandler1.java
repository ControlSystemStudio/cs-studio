package org.csstudio.diag.interconnectionServer;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.security.Credentials;
import org.csstudio.platform.security.ILoginCallbackHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class LoginCallbackHandler1 implements ILoginCallbackHandler {

	public Credentials getCredentials() {
		
//		//get properties from xml store.
//		XMLStore store = XMLStore.getInstance();
//		String xmppUserName = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences",
//				"xmppUserName", false);
//		String xmppPassword = store.getPropertyValue("org.csstudio.diag.interconnectionServer.preferences", 
//				"xmppPassword", false);

	    IPreferencesService prefs = Platform.getPreferencesService();
	    String xmppUserName = prefs.getString(Activator.getDefault().getPluginId(),
	    		"xmppUserName", "", null);
	    String xmppPassword = prefs.getString(Activator.getDefault().getPluginId(),
	    		"xmppPassword", "", null);  
		
		return new Credentials(xmppUserName, xmppPassword);
	}

	public void signalFailedLoginAttempt() {
		CentralLogger.getInstance().error(this, "XMPP login failed");
	}

}
