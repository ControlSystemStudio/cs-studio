package org.csstudio.utility.recordproperty;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;

/** Handle activation of Record Property from the object contrib. context menu.
 *  @author Kay Kasemir
 *  @author Helge Rickens
 *  @author Rok Povsic
 */
public class PVpopupAction extends ProcessVariablePopupAction {

	@Override
	public void handlePVs(IProcessVariable[] pv_names) {
		
        if (pv_names.length < 1)
            return;
        RecordPropertyView.activateWithPV(pv_names[0]);
		
	}

}
