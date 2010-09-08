package org.remotercp.preferences.ui.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.osgi.framework.InvalidSyntaxException;
import org.remotercp.common.preferences.IRemotePreferenceService;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.preferences.ui.EditableTableItem;
import org.remotercp.preferences.ui.PreferencesUIActivator;
import org.remotercp.preferences.ui.editor.PreferenceEditor;
import org.remotercp.preferences.ui.editor.PreferencesEditorInput;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

public class SaveRemotePreferencesAction implements IEditorActionDelegate {

	private PreferenceEditor targetEditor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = (PreferenceEditor) targetEditor;
	}

	public void run(IAction action) {
		List<EditableTableItem> items = this.targetEditor.getViewerInput();
		PreferencesEditorInput input = (PreferencesEditorInput) this.targetEditor
				.getEditorInput();
		ID userId = input.getUserId();

		Map<String, String> changedPreferences = new HashMap<String, String>();
		/*
		 * check which preferences have been changed and store only these
		 * preferences
		 */
		for (EditableTableItem item : items) {
			if (item.isChanged()) {
				changedPreferences.put(item.getKey(), item.getRemoteValue());
			}
		}

		ISessionService sessionService = OsgiServiceLocatorUtil.getOSGiService(
				PreferencesUIActivator.getBundleContext(),
				ISessionService.class);

		try {
			List<IRemotePreferenceService> remoteServices = sessionService
					.getRemoteService(IRemotePreferenceService.class,
							new ID[] { userId }, null);

			Assert.isTrue(!remoteServices.isEmpty());

			IRemotePreferenceService remotePreferenceService = remoteServices
					.get(0);

			List<IStatus> results = remotePreferenceService.setPreferences(
					changedPreferences, sessionService.getContainer().getID());
			ErrorView.addError(results);

		} catch (ECFException e) {
			IStatus error = e.getStatus();
			ErrorView.addError(error);
		} catch (InvalidSyntaxException e) {
			IStatus error = new Status(Status.ERROR,
					PreferencesUIActivator.PLUGIN_ID,
					"Unable to retrieve remote preference service for user "
							+ userId.getName(), e);
			ErrorView.addError(error);
		}

		// refresh editor
		this.targetEditor.refresh();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do

	}

}
