package org.csstudio.utility.quickstart;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class AddToQuickstartAction implements IObjectActionDelegate {
	
	/**
	 * The current selection.
	 */
	private IStructuredSelection _selection;

	public static IFile sdsFile;
	
	public AddToQuickstartAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		if (_selection != null) {
			Object element = _selection.getFirstElement();

			if (element instanceof IFile) {
				sdsFile = (IFile) element;
//				RunModeService.getInstance().openDisplayShellInRunMode(file.getLocation());
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			_selection = (IStructuredSelection) selection;
		}
	}

}
