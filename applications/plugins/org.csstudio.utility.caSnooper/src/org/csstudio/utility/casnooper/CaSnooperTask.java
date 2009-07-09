package org.csstudio.utility.casnooper;

import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.csstudio.utility.casnooper.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.connection.HeadlessConnection;

public class CaSnooperTask implements IApplication {
	
	private static SnooperServer snooperServerInstance = null;

	public Object start(IApplicationContext context) throws Exception {
		
		
		System.out.println("Start caSnooper");
		snooperServerInstance = SnooperServer.getInstance();
		
		for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
			s.run();
		}
		connectToXmppServer();
		snooperServerInstance.execute();
		
		return null;
	}

	   /**
     * Connects to the XMPP server for remote management (ECF-based).
     */
    private void connectToXmppServer() throws Exception {
        IPreferencesService prefs = Platform.getPreferencesService();
        String username = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        String password = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        String server = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krykxmpp.desy.de", null);
        
        HeadlessConnection.connect(username, password, server, ECFConstants.XMPP);
        ServiceLauncher.startRemoteServices();
    }
	
	public void stop() {
		snooperServerInstance.destroy();

	}

}
