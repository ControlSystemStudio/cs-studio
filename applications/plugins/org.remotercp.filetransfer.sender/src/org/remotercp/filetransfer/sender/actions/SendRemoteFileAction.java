package org.remotercp.filetransfer.sender.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.filetransfer.sender.FiletransferSenderActivator;
import org.remotercp.filetransfer.sender.RemoteFileSender;
import org.remotercp.util.roster.RosterUtil;

public class SendRemoteFileAction implements IViewActionDelegate {

	private IViewPart view;

	private IRoster roster;

	private IAction action;

	public void init(IViewPart view) {
		this.view = view;

		// register listener for changes in view.
		PropertyChangeSupport pcs = (PropertyChangeSupport) this.view
				.getAdapter(IPropertyChangeListener.class);
		pcs.addPropertyChangeListener(getPropertyChangeListener());

		this.roster = (IRoster) this.view.getAdapter(IRoster.class);

	}

	private PropertyChangeListener getPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				SendRemoteFileAction.this.roster = (IRoster) event
						.getNewValue();

				if (SendRemoteFileAction.this.roster == null) {
					SendRemoteFileAction.this.action.setEnabled(false);
				} else {
					SendRemoteFileAction.this.action.setEnabled(true);
				}
			}

		};
	}

	public void run(IAction action) {
		// get online user
		ID[] userIDs = RosterUtil.filterOnlineUserAsArray(this.roster);

		try {
			new RemoteFileSender().sendFile(userIDs);
		} catch (ECFException e) {
			IStatus error = new Status(Status.ERROR,
					FiletransferSenderActivator.PLUGIN_ID,
					"Unable to send files to selected user", e);
			ErrorView.addError(error);
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing yet

	}

}
