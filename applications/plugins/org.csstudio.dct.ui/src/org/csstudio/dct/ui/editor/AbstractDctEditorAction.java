package org.csstudio.dct.ui.editor;

import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public abstract class AbstractDctEditorAction implements IEditorActionDelegate, CommandStackEventListener {
	private DctEditor editor;
	private IAction proxyAction;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if(targetEditor instanceof DctEditor) {
			editor = (DctEditor) targetEditor;
			proxyAction = action;
			
			activeEditorChanged(editor);
		}
	}
	
	public IAction getProxyAction() {
		return proxyAction;
	}
	
	protected abstract void activeEditorChanged(DctEditor editor);
	
	public void selectionChanged(IAction action, ISelection selection) {

	}
}
