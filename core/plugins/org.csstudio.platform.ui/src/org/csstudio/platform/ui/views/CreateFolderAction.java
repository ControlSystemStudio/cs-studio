package org.csstudio.platform.ui.views;

import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.util.ResourceUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Action that creates a folder in the workspace.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class CreateFolderAction implements IViewActionDelegate {
	/**
	 * The calling view part.
	 */
	private IViewPart _view;

	/**
	 * The selected display model file.
	 */
	private IResource _selectedResource;

	/**
	 * {@inheritDoc}
	 */
	public void init(final IViewPart view) {
		_view = view;
		_selectedResource = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(final IAction action) {
		if (_selectedResource instanceof IContainer) {
			IContainer parentContainer = (IContainer) _selectedResource;

			InputDialog inputDialog = new InputDialog(_view.getSite()
					.getShell(), Messages.CreateFolderAction_DIALOG_TITLE,
					Messages.CreateFolderAction_DIALOG_MESSAGE, "", null); //$NON-NLS-1$

			int ret = inputDialog.open();

			if (ret == Window.OK) {
				String folderName = inputDialog.getValue();

				if (folderName != null) {
					if (ResourceUtil.getInstance().createFolder(parentContainer, folderName)==ResourceUtil.FOLDEREXISTS) {
						MessageDialog.openInformation(_view.getSite().getShell(),
                                        Messages.CreateFolderAction_ERROR_TITLE,
								Messages.CreateFolderAction_ERROR_MESSAGE);
					}
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
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		_selectedResource = (IResource) structuredSelection.getFirstElement();
	}

}
