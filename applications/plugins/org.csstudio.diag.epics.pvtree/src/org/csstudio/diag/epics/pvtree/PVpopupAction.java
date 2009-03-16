package org.csstudio.diag.epics.pvtree;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** Another application sent us a PV name via its popup menu.
 *  @author Kay Kasemir
 */
public class PVpopupAction extends ProcessVariablePopupAction
{
    @Override
    public void handlePVs(IProcessVariable pv_names[])
    {
        if (pv_names.length < 1)
            return;
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        try
        {
            PVTreeView view = (PVTreeView) page.showView(PVTreeView.ID);
            view.setPVName(pv_names[0].getName());
        }
        catch (Exception e)
        {
            CentralLogger.getInstance().getLogger(this)
                .error("Cannot open PVTreeView" , e); //$NON-NLS-1$
        }
    }
}
