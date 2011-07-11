package org.csstudio.cagateway;

import org.csstudio.cagateway.preferences.CAGatewayPreference;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaGateway implements IApplication, IGenericServiceListener<ISessionService> {

    private static final Logger LOG = LoggerFactory.getLogger(CaGateway.class);
    
	private static CaServer caGatewayInstance = null;

	@Override
    public Object start(IApplicationContext context) throws Exception {
		System.out.println("Start caGateway");
		caGatewayInstance = CaServer.getGatewayInstance();	
		
		caGatewayInstance.execute();
		
		return null;
	}

 

    @Override
    public void bindService(ISessionService sessionService) {
        String username = CAGatewayPreference.XMPP_USER_NAME.getValue();
        String password = CAGatewayPreference.XMPP_PASSWORD.getValue();
        String server = CAGatewayPreference.XMPP_SERVER_NAME.getValue();
    	
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
	@Override
    public void stop() {
		// TODO Auto-generated method stub

	}

}
