package org.csstudio.utility.casnooper;


import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.csstudio.utility.casnooper.preferences.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaSnooperTask implements IApplication, IGenericServiceListener<ISessionService> {
	
    private static final Logger LOG = LoggerFactory.getLogger(CaSnooperTask.class);
    
	private static SnooperServer snooperServerInstance = null;

	@Override
    public Object start(IApplicationContext context) throws Exception {
		
		Activator.getDefault().addSessionServiceListener(this);
		
		System.out.println("Start caSnooper");
		snooperServerInstance = SnooperServer.getInstance();
		
		for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
			s.run();
		}
		snooperServerInstance.execute();
		
		return null;
	}


	@Override
    public void stop() {
		snooperServerInstance.destroy();

	}

    @Override
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
			LOG.warn("XMPP connection is not available, ", e);
		}
    }
    
    @Override
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
}
