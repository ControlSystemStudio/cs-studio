package org.csstudio.diag.interconnectionServer;

import org.csstudio.diag.interconnectionServer.server.Statistic;
import org.csstudio.platform.libs.dcf.actions.IAction;

public class GetListOfClients implements IAction {

	public Object run(Object param) {
		// TODO Auto-generated method stub
		return Statistic.getInstance().getNodeNameArray();
	}

}
