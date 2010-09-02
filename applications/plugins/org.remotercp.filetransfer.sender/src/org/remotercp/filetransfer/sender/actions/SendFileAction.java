package org.remotercp.filetransfer.sender.actions;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.remotercp.filetransfer.sender.RemoteFileSender;
import org.remotercp.util.dialogs.RemoteExceptionHandler;
import org.remotercp.util.roster.RosterUtil;

/*
 * This action has been formally used for popup menus.
 */
@Deprecated
public class SendFileAction implements IObjectActionDelegate {

	private IStructuredSelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	public void run(IAction action) {

		// filter online user
		ID[] userIDs = RosterUtil
				.filterOnlineUserAsArray((IRosterItem) this.selection
						.getFirstElement());

		try {
			new RemoteFileSender().sendFile(userIDs);
			// RemoteFileSender.sendFile(userIDs);
		} catch (ECFException e) {
			RemoteExceptionHandler.handleException(e,
					"Unable to send file to user");
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;

			boolean isItemOnline = RosterUtil
					.isRosterItemOnline((IRosterItem) this.selection
							.getFirstElement());

			action.setEnabled(isItemOnline);

		}
	}

}
