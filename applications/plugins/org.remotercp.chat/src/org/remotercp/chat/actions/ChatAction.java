package org.remotercp.chat.actions;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.ecf.presence.IPresence;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.remotercp.chat.ChatEditorInput;
import org.remotercp.chat.ui.ChatEditor;

public class ChatAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	private IStructuredSelection selection;

	public static final String ID = "org.eclipsercp.hyperbola.contacts.ui.chat";

	public void dispose() {
		// do nothing yet
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		/*
		 * Open the chat Editor
		 */
		IRosterEntry contactEntry = (IRosterEntry) this.selection
				.getFirstElement();
		IWorkbenchPage page = this.window.getActivePage();
		ChatEditorInput input = new ChatEditorInput(contactEntry.getUser()
				.getID());

		try {
			page.openEditor(input, ChatEditor.ID);
		} catch (PartInitException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"Unable to open ChatEditor");
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			action
					.setEnabled(this.selection.size() == 1
							&& this.selection.getFirstElement() instanceof IRosterEntry
							&& ((IRosterEntry) this.selection.getFirstElement())
									.getPresence().getType() != IPresence.Type.UNAVAILABLE);
		} else {
			action.setEnabled(false);
		}
	}
}
