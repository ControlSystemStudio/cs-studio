package org.remotercp.contacts.actions;

import org.eclipse.ecf.presence.roster.RosterGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * This action does open a dialog where a new user can be added to the contacts
 * list.
 * 
 * @author Eugen Reiswich
 * @date 03.08.2008
 * 
 */
public class AddContactAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	// TreeViewer Selection
	private IStructuredSelection selection;

	public static final String ACTION_ID = "org.eclipsercp.hyperbola.contacts.ui.addcontactaction";

	public void dispose() {
		// do nothing yet
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {

		// UnsupportedOperationException unsupportedOperationException = new
		// UnsupportedOperationException(
		// "Method is not supported yet");
		// RemoteExceptionHandler.handleException(unsupportedOperationException,
		// "Method has to be written fist");

		MessageBox error = new MessageBox(window.getShell(), SWT.ICON_ERROR);
		error.setMessage("This method is not supported yet");
		error.open();
		// ErrorView.addError("Severe error", Level.SEVERE);
		// ErrorView.addError("Warning message", Level.WARNING);
		// ErrorView.addError("Info message", Level.INFO);

		// AddContactDialog addContactDialog = new AddContactDialog();
		//
		// Object item = selection.getFirstElement();
		// if (item instanceof RosterGroup) {
		// // RosterGroup group = (RosterGroup) item;
		//
		// WizardDialog dialog = new WizardDialog(window.getShell(),
		// addContactDialog);
		//
		// int state = dialog.open();
		// if (state == Window.OK) {
		// // try {
		// // Roster list =
		// // Session.getInstance().getConnection().getRoster();
		// // ContactEntryJFaceModel contactEntry = addContactDialog
		// // .getContactsEntry();
		// // String[] groups = new String[] { group.getName() };
		// // list.createEntry(contactEntry.getName(), contactEntry
		// // .getNickname(), groups);
		// // } catch (XMPPException e) {
		// // e.printStackTrace();
		// // }
		// }
		// }
	}

	public void selectionChanged(IAction action, ISelection selection) {
		/*
		 * Check, whether a group is selected in Contacts TreeViewer
		 */
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;

			/*
			 * Enable Action only if one Group and only one group is selected
			 */
			action.setEnabled(this.selection.size() == 1
					&& this.selection.getFirstElement() instanceof RosterGroup);
		} else {
			action.setEnabled(false);
		}
	}
}
