package org.csstudio.cagateway;

import org.csstudio.cagateway.preferences.CAGatewayPreference;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class CaGateway implements IApplication, IGenericServiceListener<ISessionService> {
	
	private static CaServer caGatewayInstance = null;

	public Object start(IApplicationContext context) throws Exception {
		System.out.println("Start caGateway");
		caGatewayInstance = CaServer.getGatewayInstance();	
		
		caGatewayInstance.execute();
		
		return null;
	}

 

    public void bindService(ISessionService sessionService) {
        String username = CAGatewayPreference.XMPP_USER_NAME.getValue();
        String password = CAGatewayPreference.XMPP_PASSWORD.getValue();
        String server = CAGatewayPreference.XMPP_SERVER_NAME.getValue();
    	
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
	public void stop() {
		// TODO Auto-generated method stub

	}

}
