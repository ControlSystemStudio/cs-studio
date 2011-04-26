package org.csstudio.utility.casnooper;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.csstudio.utility.casnooper.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class CaSnooperTask implements IApplication, IGenericServiceListener<ISessionService> {
	
	private static SnooperServer snooperServerInstance = null;

	public Object start(IApplicationContext context) throws Exception {
		
		
		System.out.println("Start caSnooper");
		snooperServerInstance = SnooperServer.getInstance();
		
		for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
			s.run();
		}
		snooperServerInstance.execute();
		
		return null;
	}


	public void stop() {
		snooperServerInstance.destroy();

	}

    public void bindService(ISessionService sessionService) {
        IPreferencesService prefs = Platform.getPreferencesService();
        String username = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_USER_NAME, "anonymous", null);
        String password = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_PASSWORD, "anonymous", null);
        String server = prefs.getString(Activator.PLUGIN_ID,
                PreferenceConstants.XMPP_SERVER, "krykxmpp.desy.de", null);
     	
    	try {
			sessionService.connect(username, password, server);
		} catch (Exception e) {
			CentralLogger.getInstance().warn(this,
					"XMPP connection is not available, " + e.toString());
		}
    }
    
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
}
