package org.csstudio.utility.caSnooperUi;

import org.csstudio.platform.libs.dcf.ui.ViewCreator;
import org.csstudio.utility.caSnooperUi.ui.ChangeView.SnooperView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class SnooperViewCreator extends ViewCreator {

	public void createView(String origin, String actionId, String actionName, final Object response) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try{
					IWorkbench workbench = PlatformUI.getWorkbench();
				    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				    IWorkbenchPage snoopPage = window.getActivePage();
				    
				    SnooperView view = (SnooperView)snoopPage.showView(SnooperView.ID);
				   
				    view.setMessage(response);
				    
				    return;
				}catch (Exception e)
		    	{
		    		System.out.println("Exception: " + e.getMessage());
		    	}
			}
		});
		System.out.println("return");
		return;
	}

}
