package org.csstudio.platform.ui.views;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.csstudio.platform.ui.workbench.FileEditorInput;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * Action that deletes a resource from the workspace.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class DeleteResourceAction implements IViewActionDelegate {
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
		if (_selectedResource != null) {
			boolean reallyDelete = MessageDialog.openQuestion(_view.getSite()
					.getShell(), Messages.DeleteResourceAction_QUESTION_TITLE,
					NLS.bind(Messages.DeleteResourceAction_QUESTION_MESSAGE,
							_selectedResource.getName()));

			if (reallyDelete) {
				try {
					for (IEditorReference openEditor : _view.getSite()
							.getPage().getEditorReferences()) {
						IEditorInput editorInput = openEditor.getEditorInput();

						if (editorInput instanceof FileEditorInput) {
							FileEditorInput fileEditorInput = (FileEditorInput) editorInput;

							if (fileEditorInput.getFile().equals(
									_selectedResource)) {
								_view.getSite().getPage().closeEditor(
										openEditor.getEditor(false), true);
							}
						}
					}
					_selectedResource.delete(true, null);

				} catch (CoreException e) {
					CentralLogger.getInstance().error(this, e);
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
