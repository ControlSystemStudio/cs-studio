package org.csstudio.diag.probe;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;

/** Handle activation of Probe from the objectr contrib. context menu.
 *  @author Kay Kasemir
 *  @author Last modifications by Helge Rickens
 */
public class PVpopupAction extends ProcessVariablePopupAction
{
    /** @see org.csstudio.data.exchange.ProcessVariablePopupAction#handlePVs(]) */
    @Override
    public void handlePVs(IProcessVariable[] pv_names)
    {
        if (pv_names.length < 1)
            return;
//        Probe.activateWithPV(pv_names[0].getName());
        Probe.activateWithPV(pv_names[0]);
    }
}
