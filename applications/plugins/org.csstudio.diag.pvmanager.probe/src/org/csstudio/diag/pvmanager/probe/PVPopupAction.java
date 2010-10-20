/**
 * 
 */
package org.csstudio.diag.pvmanager.probe;

import org.csstudio.diag.pvmanager.probe.views.PVManagerProbe;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;

/**
 * @author shroffk
 *
 */
public class PVPopupAction extends ProcessVariablePopupAction {

	/**
	 * 
	 */
	public PVPopupAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handlePVs(IProcessVariable[] pvNames) {
		// TODO Auto-generated method stub
		if (pvNames.length < 1)
            return;
        PVManagerProbe.activateWithPV(pvNames[0]);
	}

}
