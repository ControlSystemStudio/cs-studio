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

	private ISessionService xmppService;

	@Override
    public Object start(final IApplicationContext context) throws Exception {

	    LOG.info("Start caGateway");

	    Activator.getDefault().addSessionServiceListener(this);
		caGatewayInstance = CaServer.getGatewayInstance();
		context.applicationRunning();
		caGatewayInstance.execute();

		if (xmppService != null) {

		    // To avoid errors when calling the remote stop command
		    // we have to wait a little bit
		    final Object lock = new Object();
		    synchronized (lock) {
		        try {
		            lock.wait(250);
		        } catch (final InterruptedException ie) {
		            LOG.warn("[*** InterruptedException ***]: {}", ie.getMessage());
		        }
		    }

		    xmppService.disconnect();
		}

		LOG.info("Leaving caGateway application.");
		return IApplication.EXIT_OK;
	}



    @Override
    public void bindService(final ISessionService sessionService) {

        final String username = CAGatewayPreference.XMPP_USER_NAME.getValue();
        final String password = CAGatewayPreference.XMPP_PASSWORD.getValue();
        final String server = CAGatewayPreference.XMPP_SERVER_NAME.getValue();

    	try {
			sessionService.connect(username, password, server);
			xmppService = sessionService;
		} catch (final Exception e) {
			LOG.warn("XMPP connection is not available: {}", e.getMessage());
			xmppService = null;
		}
    }

    @Override
    public void unbindService(final ISessionService service) {
    	// Nothing to do here
    }

	@Override
    public void stop() {
		// Auto-generated method stub
	}
}
