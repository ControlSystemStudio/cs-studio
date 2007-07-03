package org.csstudio.diag.interconnectionServer;

import org.csstudio.diag.interconnectionServer.server.InterconnectionServer;
import org.csstudio.platform.libs.dcf.actions.IAction;

public class StopIcServer implements IAction {

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.platform.libs.dcf.actions.IAction#run(java.lang.Object)
	 */
	@SuppressWarnings("deprecation")
	public Object run(Object param) {
		/*
		 * stop IC-Server
		 */
		boolean result = InterconnectionServer.getInstance().stopIcServer();
		/*
		 * stop here - how??
		 */
		
		return "" + result;
	}

}