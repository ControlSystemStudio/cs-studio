package org.csstudio.ui.sds.ui.internal.actions;

import java.util.List;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariablePopupAction;

public class PrintPvsAction extends ProcessVariablePopupAction {

	public PrintPvsAction() {
		super();
	}

	@Override
	protected void handlePvs(List<IProcessVariableAddress> pvs) {
		for(IProcessVariableAddress pv : pvs) {
			CentralLogger.getInstance().info(null, pv.getFullName());
		}
	}

}
