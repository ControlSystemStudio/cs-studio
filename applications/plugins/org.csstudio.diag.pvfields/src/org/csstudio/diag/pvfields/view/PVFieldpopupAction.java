package org.csstudio.diag.pvfields.view;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;

/** Handle activation of Fields Table from the object contrib. context menu.
 *  @author Kay Kasemir
 *  @author Helge Rickens
 */
public class PVFieldpopupAction extends ProcessVariablePopupAction
{
    /** @see org.csstudio.data.exchange.ProcessVariablePopupAction#handlePVs(]) */
    @Override
    public void handlePVs(IProcessVariable[] pv_names)
    {
        if (pv_names.length < 1)
            return;
        PVFieldsView.activateWithPV(pv_names[0]);
    }
}
