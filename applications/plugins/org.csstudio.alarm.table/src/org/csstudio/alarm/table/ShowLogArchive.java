package org.csstudio.alarm.table;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


public class ShowLogArchive implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
		{
		    try
		    {
		        IWorkbench workbench = PlatformUI.getWorkbench();
		        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		        IWorkbenchPage page = window.getActivePage();
		        System.out.println(LogViewArchive.ID);
		        page.showView(LogViewArchive.ID);
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
