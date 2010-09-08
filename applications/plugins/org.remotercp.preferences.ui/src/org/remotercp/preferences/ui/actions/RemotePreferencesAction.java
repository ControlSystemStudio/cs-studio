package org.remotercp.preferences.ui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.SortedMap;

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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.preferences.IRemotePreferenceService;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.preferences.ui.PreferencesUIActivator;
import org.remotercp.preferences.ui.editor.PreferenceEditor;
import org.remotercp.preferences.ui.editor.PreferencesEditorInput;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;
import org.remotercp.util.roster.RosterUtil;

public class RemotePreferencesAction implements IViewActionDelegate {

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
				RemotePreferencesAction.this.roster = (IRoster) event
						.getNewValue();

				if (RemotePreferencesAction.this.roster == null) {
					RemotePreferencesAction.this.action.setEnabled(false);
				} else {
					RemotePreferencesAction.this.action.setEnabled(true);
				}
			}

		};
	}

	public void run(IAction action) {
		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				PreferencesUIActivator.getBundleContext(),
				ISessionService.class);

		try {

			// get online user
			ID[] userIDs = RosterUtil.filterOnlineUserAsArray(this.roster);

			List<IRemotePreferenceService> remoteService = sessionService
					.getRemoteService(IRemotePreferenceService.class, userIDs,
							null);

			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();

			/* open for each selected user a new editor */
			for (int user = 0; user < userIDs.length; user++) {
				IRemotePreferenceService remotePreferenceService = remoteService
						.get(user);
				ID userId = userIDs[user];
				SortedMap<String, String> preferences = remotePreferenceService
						.getPreferences(new String[] {});

				/* open editor */
				PreferencesEditorInput prefInput = new PreferencesEditorInput(
						preferences, userId);
				activePage.openEditor(prefInput, PreferenceEditor.EDITOR_ID);
			}

		} catch (ECFException e) {
			IStatus ecfError = new Status(Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Error occured while retrieving remote preference service",
					e);
			ErrorView.addError(ecfError);
		} catch (InvalidSyntaxException e) {
			IStatus ecfError = new Status(
					Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Invalid user filter used to retrieve remote preference service",
					e);
			ErrorView.addError(ecfError);
		} catch (PartInitException e) {
			IStatus editorError = new Status(Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Unable to open preference ediro", e);
			ErrorView.addError(editorError);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;
		// if (this.roster == null
		// || RosterUtil.getRosterEntries(this.roster).isEmpty()) {
		// this.action.setEnabled(false);
		// }
	}

}
