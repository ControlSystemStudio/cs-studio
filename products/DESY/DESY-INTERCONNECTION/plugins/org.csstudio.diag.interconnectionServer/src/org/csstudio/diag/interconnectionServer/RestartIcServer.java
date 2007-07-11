package org.csstudio.diag.interconnectionServer;

import org.csstudio.diag.interconnectionServer.server.InterconnectionServer;
import org.csstudio.platform.libs.dcf.actions.IAction;

public class RestartIcServer implements IAction {

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.platform.libs.dcf.actions.IAction#run(java.lang.Object)
	 */
	public Object run(Object param) {
		/*
		 * stop IC-Server
		 */
		boolean result = InterconnectionServer.getInstance().stopIcServer();
		try {
			/*
			 * wait for IC-Server to greacefully stop
			 */
			this.wait(5000); //wait 5 sec
		} catch (Exception e) {
			// TODO: handle exception
			// nothing to do we want to stop anyhow
		}		
		/*
		 * stop here - nicely
		 */
		// System.exit(0);
		HeadlessRunnable.SHUTDOWN = false;
		return "" + result;
	}

}