package de.desy.language.snl.ui.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import de.desy.language.editor.ui.eventing.UIEvent;

public class RefreshAction implements IEditorActionDelegate {


	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		//nothing to do
	}

	public void run(IAction action) {
		UIEvent.HIGHLIGHTING_REFRESH_REQUEST.triggerEvent();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		//nothing to do
	}

}
