package org.csstudio.sds.ui.internal.pvlistview;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenPvInSearchViewAction extends ProcessVariablePopupAction {

	@Override
	public void handlePVs(IProcessVariable[] pv_names) {
		String pvName = "";

		for (IProcessVariable iProcessVariable : pv_names) {
			if (!(iProcessVariable.getName().isEmpty())) {
				pvName = iProcessVariable.getName();
				break;
			}
		}

		if (!pvName.isEmpty()) {
			try {
				if (!pvName.isEmpty()) {
					PvSearchView pvSearchView = (PvSearchView) PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(PvSearchView.VIEW_ID);
					pvSearchView.searchFor(pvName);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
}
