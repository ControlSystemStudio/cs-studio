package org.remotercp.preferences.ui.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.remotercp.preferences.ui.editor.PreferenceEditor;

public class ImportPreferencesAction implements IEditorActionDelegate {

	private IEditorPart targetEditor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = targetEditor;

	}

	public void run(IAction action) {
		FileDialog importDialog = new FileDialog(this.targetEditor
				.getEditorSite().getShell(), SWT.OPEN);
		String path = importDialog.open();
		File preferences = new File(path);

		// set imported preferences input to editor
		((PreferenceEditor) this.targetEditor)
				.setImportedPreferences(preferences);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing yet

	}

}
