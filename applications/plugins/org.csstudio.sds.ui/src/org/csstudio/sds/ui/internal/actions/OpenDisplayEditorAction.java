package org.csstudio.sds.ui.internal.actions;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.CentralItemFactory;
import org.csstudio.platform.ui.workbench.ControlSystemItemEditorInput;
import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Action that opens the Synoptic Display Editor.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class OpenDisplayEditorAction implements
		IWorkbenchWindowActionDelegate {

	/**
	 * A workbench window handle.
	 */
	private IWorkbenchWindow _window;

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {

	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final IWorkbenchWindow window) {
		_window = window;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(final IAction action) {
		String query = "x."+DisplayEditor.SDS_FILE_EXTENSION; //$NON-NLS-1$

		// we need a dummy editor input...
		IEditorInput editorInput = new ControlSystemItemEditorInput(
				CentralItemFactory.createProcessVariable("x")); //$NON-NLS-1$

		IEditorRegistry editorRegistry = PlatformUI.getWorkbench()
				.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(query);

		if (descriptor != null && editorInput != null) {
			IWorkbenchPage page = _window.getActivePage();
			try {
				page.openEditor(editorInput, descriptor.getId());
			} catch (PartInitException e) {
				CentralLogger.getInstance()
						.error(this, "Cannot open editor", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(final IAction action,
			final ISelection selection) {
	}
}
