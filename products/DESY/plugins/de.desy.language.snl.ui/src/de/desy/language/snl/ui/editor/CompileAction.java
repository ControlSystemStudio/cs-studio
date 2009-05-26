package de.desy.language.snl.ui.editor;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class CompileAction implements IEditorActionDelegate {

	private SNLEditor _editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null) {
			if (targetEditor instanceof SNLEditor) {
				_editor = (SNLEditor) targetEditor;
			}
		}
	}

	public void run(IAction action) {
		if (_editor != null) {
			if (_editor.isDirty()) {
				MessageBox box = new MessageBox(_editor.getSite().getShell(), SWT.ICON_INFORMATION);
				box.setText("Unsaved changes");
				box.setMessage("There are unsaved changes.\nPlease save before compilation.");
				box.open();
			} else {
				_editor.doHandleSourceModifiedAndSaved(new NullProgressMonitor());
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do
	}

}
