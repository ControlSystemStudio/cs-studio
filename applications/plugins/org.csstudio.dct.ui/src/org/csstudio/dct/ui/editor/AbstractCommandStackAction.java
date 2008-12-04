package org.csstudio.dct.ui.editor;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackEvent;
import org.eclipse.gef.commands.CommandStackEventListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public abstract class AbstractCommandStackAction extends AbstractDctEditorAction implements CommandStackEventListener {
	private CommandStack commandStack;

	@Override
	protected void activeEditorChanged(DctEditor editor) {
		if (commandStack != null) {
			commandStack.removeCommandStackEventListener(this);
		}

		commandStack = editor.getCommandStack();
		commandStack.addCommandStackEventListener(this);
		
		getProxyAction().setEnabled(isActionEnabled(commandStack));
	}

	public void run(IAction action) {
		doRun(commandStack);
	}

	protected abstract void doRun(CommandStack commandStack);
	
	protected abstract boolean isActionEnabled(CommandStack commandStack);

	/**
	 *{@inheritDoc}
	 */
	public void stackChanged(CommandStackEvent event) {
		getProxyAction().setEnabled(isActionEnabled(commandStack));
	}
}
