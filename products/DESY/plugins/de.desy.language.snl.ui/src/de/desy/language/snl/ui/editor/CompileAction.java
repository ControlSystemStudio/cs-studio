package de.desy.language.snl.ui.editor;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Action to trigger the compilation of the *.st file displayed in the SNL
 * Editor without saving.
 * 
 * @author Kai Meyer (C1 WPS)
 * 
 */
public class CompileAction implements IEditorActionDelegate, IWorkbenchWindowActionDelegate {

	private SNLEditor _editor;

	/**
	 * {@inheritDoc}
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null) {
			if (targetEditor instanceof SNLEditor) {
				_editor = (SNLEditor) targetEditor;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(IAction action) {
		if (_editor != null) {
			if (_editor.isDirty()) {
				MessageBox box = new MessageBox(_editor.getSite().getShell(),
						SWT.ICON_INFORMATION);
				box.setText("Unsaved changes");
				box
						.setMessage("There are unsaved changes.\nPlease save before compilation.");
				box.open();
			} else {
				_editor
						.compileFile(new NullProgressMonitor());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do
	}

	public void dispose() {
		
	}

	public void init(IWorkbenchWindow window) {
		
	}

}
