package org.remotercp.preferences.ui.actions;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.remotercp.errorhandling.ui.ErrorView;
import org.remotercp.preferences.ui.PreferencesUIActivator;
import org.remotercp.preferences.ui.editor.PreferencesEditorInput;
import org.remotercp.util.preferences.PreferencesUtil;

public class ExportPreferencesAction implements IEditorActionDelegate {

	private IEditorPart targetEditor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.targetEditor = targetEditor;

	}

	public void run(IAction action) {
		FileDialog saveDialog = new FileDialog(this.targetEditor
				.getEditorSite().getShell(), SWT.SAVE);
		String path = saveDialog.open();

		// if dialog has not been canceled export the file
		if (path != null) {

		PreferencesEditorInput input = (PreferencesEditorInput) this.targetEditor
					.getEditorInput();
			 Map<String, String> preferences = input.getPreferences();
			 
			 try {
				PreferencesUtil.exportPreferencesToFile(preferences, path);
			} catch (IOException e) {
				IStatus error = new Status(Status.ERROR,
						PreferencesUIActivator.PLUGIN_ID,
						"Error while trying to export preferences occured", e);
				ErrorView.addError(error);
			}

//			// copy preferences to local file
//			try {
//				InputStream in = new FileInputStream(preferences);
//				OutputStream out = new FileOutputStream(exportPreferences);
//
//				// transfer bytes from in to out
//				byte[] buf = new byte[1024];
//				int len;
//				while ((len = in.read(buf)) > 0) {
//					out.write(buf, 0, len);
//				}
//			} catch (FileNotFoundException e) {
//				IStatus error = new Status(Status.ERROR,
//						PreferencesUIActivator.PLUGIN_ID,
//						"Error while trying to export preferences occured", e);
//				ErrorView.addError(error);
//			} catch (IOException e) {
//				IStatus error = new Status(Status.ERROR,
//						PreferencesUIActivator.PLUGIN_ID,
//						"Error while trying to export preferences occured", e);
//				ErrorView.addError(error);
//			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing yet

	}

}
