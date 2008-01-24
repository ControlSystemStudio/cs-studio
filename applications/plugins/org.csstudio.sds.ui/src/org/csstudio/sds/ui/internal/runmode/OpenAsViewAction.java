package org.csstudio.sds.ui.internal.runmode;

import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action, that opens the currently selected SDS display as Eclipse view.
 * 
 * @author Sven Wende
 */
public final class OpenAsViewAction extends Action implements IObjectActionDelegate {

	/**
	 * The current selection.
	 */
	private IStructuredSelection _selection;

	/**
	 * {@inheritDoc}
	 */
	public void run(final IAction action) {
		if (_selection != null) {
			Object element = _selection.getFirstElement();

			if (element instanceof IFile) {
				RunModeService.getInstance().openDisplayViewInRunMode(((IFile) element).getLocation());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			_selection = (IStructuredSelection) selection;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setActivePart(final IAction action,
			final IWorkbenchPart targetPart) {

	}

}
