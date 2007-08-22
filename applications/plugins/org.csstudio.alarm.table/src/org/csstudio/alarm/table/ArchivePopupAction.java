package org.csstudio.alarm.table;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariablePopupAction;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/** Handle activation of Probe from the object contrib. context menu.
 *  @author Jan Hatje
 */
public class ArchivePopupAction extends ProcessVariablePopupAction {

	/** @see org.csstudio.data.exchange.ProcessVariablePopupAction#handlePVs(]) */
	@Override
	public void handlePVs(IProcessVariable[] pv_names)
	    {
	        if (pv_names.length < 1)
	            return;
	        IWorkbench workbench = PlatformUI.getWorkbench();
	        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	        IWorkbenchPage page = window.getActivePage();
	        try
	        {
	            LogViewArchive view = (LogViewArchive) page.showView(LogViewArchive.ID);
	            view.readDBFromExternalCall(pv_names[0]);
	        }
	        catch (Exception e)
	        {
//	            Plugin.logException("Cannot open PVTreeView" , e);
	        }
	 }
}
