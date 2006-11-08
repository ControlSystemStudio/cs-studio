package org.csstudio.platform.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.update.ui.UpdateManagerUI;

/**
 * Action that opens up the <b>Find and Install...</b>
 * dialog.
 * 
 * @author awill
 * 
 */
public class InstallWizardAction extends Action implements
		IWorkbenchWindowActionDelegate {

	/**
	 * Reference to the associated workbench window.
	 */
	private IWorkbenchWindow _window;

	/**
	 * Standard constructor.
	 */
	public InstallWizardAction() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public final void run(final IAction action) {
		BusyIndicator.showWhile(_window.getShell().getDisplay(),
				new Runnable() {
					public void run() {
						UpdateManagerUI.openInstaller(_window.getShell());
					}
				});
	}

	/**
	 * {@inheritDoc}
	 */
	public final void selectionChanged(final IAction action,
			final ISelection selection) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	public final void init(final IWorkbenchWindow window) {
		_window = window;
	}
}
