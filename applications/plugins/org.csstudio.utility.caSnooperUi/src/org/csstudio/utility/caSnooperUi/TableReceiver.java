package org.csstudio.utility.caSnooperUi;

import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IResultReceiver;
import org.csstudio.utility.caSnooperUi.ui.ChangeView.SnooperView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class TableReceiver implements IResultReceiver {

    public void processResult(CommandResult result) {
        final String response = (String) result.getValue();
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
