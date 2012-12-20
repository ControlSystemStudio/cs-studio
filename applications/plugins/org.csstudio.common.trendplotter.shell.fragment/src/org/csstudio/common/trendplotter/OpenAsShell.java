package org.csstudio.common.trendplotter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class OpenAsShell implements IObjectActionDelegate {

	/**
	 * Current selection.
	 */
	private IStructuredSelection _selection;

	public void run(IAction action) {
		if (_selection != null) {
			Object element = _selection.getFirstElement();
			if (element instanceof IFile) {
				IFile file = (IFile) element;
				DB2Shell dbshell = new DB2Shell(file);
				dbshell.openShell();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			_selection = (IStructuredSelection) selection;
		}

	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
