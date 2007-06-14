package org.csstudio.diag.interconnectionServer;

import org.csstudio.diag.interconnectionServer.server.Statistic;
import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.statistic.CollectorSupervisor;

public class GetStatisticInformation implements IAction {

	public Object run(Object param) {
		String result = null;
		// TODO Auto-generated method stub
		// test only CollectorSupervisor.getInstance().printCollection();
		result = "----------- RESULT -----------\n";
		result += CollectorSupervisor.getInstance().getCollectionAsString();
		result += Statistic.getInstance().getStatisticAsString();
		return result;
	}

}
