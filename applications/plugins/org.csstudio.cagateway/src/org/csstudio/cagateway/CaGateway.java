package org.csstudio.cagateway;

import org.csstudio.cagateway.preferences.Preference;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.ecf.ECFConstants;
import org.remotercp.login.connection.HeadlessConnection;

public class CaGateway implements IApplication {
	
	private static CaServer caGatewayInstance = null;

	public Object start(IApplicationContext context) throws Exception {
		System.out.println("Start caGateway");
		caGatewayInstance = CaServer.getGatewayInstance();	
		
		connectToXmppServer();
		caGatewayInstance.execute();
		
		return null;
	}

    /**
     * Connects to the XMPP server for remote management (ECF-based).
     */
    private void connectToXmppServer() throws Exception {
        String username = Preference.XMPP_USER_NAME.getValue();
        String password = Preference.XMPP_PASSWORD.getValue();
        String server = Preference.XMPP_SERVER_NAME.getValue();
        HeadlessConnection.connect(username, password, server, ECFConstants.XMPP);
        ServiceLauncher.startRemoteServices();
    }

	
	public void stop() {
		// TODO Auto-generated method stub

	}

}
