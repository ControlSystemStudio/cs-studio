package org.csstudio.platform.ui.views;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.util.ResourceUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Action that creates a project in the workspace.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class CreateProjectAction implements IViewActionDelegate {
	/**
	 * The calling view part.
	 */
	private IViewPart _view;

	/**
	 * {@inheritDoc}
	 */
	public void init(final IViewPart view) {
		_view = view;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(final IAction action) {
		InputDialog inputDialog = new InputDialog(_view.getSite().getShell(),
				Messages.getString("CreateProjectAction.DIALOG_TITLE"), //$NON-NLS-1$
				Messages.getString("CreateProjectAction.DIALOG_MESSAGE"), "", null); //$NON-NLS-1$ //$NON-NLS-2$

		int ret = inputDialog.open();

		if (ret == Window.OK) {
			String projectName = inputDialog.getValue();

			if (projectName != null) {
				if (ResourceUtil.getInstance().createProject(projectName)==ResourceUtil.PROJECTEXISTS) {
					MessageDialog.openInformation(_view.getSite().getShell(),
							Messages.getString("CreateProjectAction.ERROR_TITLE"), //$NON-NLS-1$
							Messages.getString("CreateProjectAction.ERROR_MESSAGE")); //$NON-NLS-1$
				}
			}
		}

		_view.setFocus();
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(final IAction action,
			final ISelection selection) {
	}

}
