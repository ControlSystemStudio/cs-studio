package org.csstudio.platform.ui.internal.developmentsupport.util;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.ui.dnd.rfc.ProcessVariablePopupAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class PrintPvsAction extends ProcessVariablePopupAction {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handlePvs(List<IProcessVariableAddress> pvs) {
		StringBuffer sb = new StringBuffer();
		for (IProcessVariableAddress pv : pvs) {
			sb.append(pv.getFullName());
			sb.append("\r\n");
		}

		MessageDialog.openInformation(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "Received PVs", sb
				.toString());
	}

}
