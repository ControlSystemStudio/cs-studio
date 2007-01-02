package org.csstudio.utility.nameSpaceSearch;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.csstudio.utility.nameSpaceSearch.ui.MainView;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PVpopupAction extends ProcessVariablePopupAction{

	public void handlePVs(IProcessVariable pv_names[])
    {
        if (pv_names.length < 1)
            return;
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        try
        {
//        	System.out.println("Rufe Fenster");
            MainView view = (MainView) page.showView(MainView.ID);
            view.startSearch(pv_names[0].getName());
        }
        catch (Exception e)
        {
//            Plugin.logException("Cannot open PVTreeView" , e);
        }
    }
}
