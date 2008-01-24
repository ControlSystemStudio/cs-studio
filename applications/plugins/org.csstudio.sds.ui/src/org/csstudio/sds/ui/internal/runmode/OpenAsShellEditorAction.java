package org.csstudio.sds.ui.internal.runmode;

import org.csstudio.sds.ui.internal.editor.DisplayEditor;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * Editor Action, that opens the current SDS display model in a separate shell.
 * 
 * @author Sven Wende
 */
public final class OpenAsShellEditorAction implements IEditorActionDelegate {
	/**
	 * The current display editor.
	 */
	private DisplayEditor _editor;

	/**
	 * {@inheritDoc}
	 */
	public void setActiveEditor(final IAction action,
			final IEditorPart targetEditor) {
		_editor = (DisplayEditor) targetEditor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(final IAction action) {
		RunModeService.getInstance().openDisplayShellInRunMode(_editor.getFilePath());
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(final IAction action,
			final ISelection selection) {
	}
}
