package org.csstudio.platform.ui.internal.actions;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Action that exports the preferences to a file suitable for use as a plugin
 * customization file.
 * 
 * @see CSSPlatformPlugin#exportPluginCustomization(String, boolean)
 * 
 * @author Joerg Rathlev
 */
public final class ExportPreferencesAction implements
		IWorkbenchWindowActionDelegate {
	
	/**
	 * The file filter for the save dialog.
	 */
	private static final String[] FILE_FILTER = new String[] {
		"*.ini;*.properties" };  //$NON-NLS-1$

	/**
	 * The names for the file filters in the save dialog.
	 */
	private static final String[] FILE_FILTER_NAMES = new String[] {
		Messages.ExportPreferencesAction_FileTypeDescription };

	/**
	 * The default file name for the export.
	 */
	private static final String DEFAULT_FILENAME = "plugin_customization.ini";  //$NON-NLS-1$
	
	/**
	 * The window that provides the context for this delegate.
	 */
	private IWorkbenchWindow _window;
	
	/**
	 * {@inheritDoc}
	 */
	public void run(IAction action) {
		FileDialog dialog = new FileDialog(_window.getShell(), SWT.SAVE);
		dialog.setText(Messages.ExportPreferencesAction_DialogTitle);
		dialog.setFileName(DEFAULT_FILENAME);
		dialog.setFilterExtensions(FILE_FILTER);
		dialog.setFilterNames(FILE_FILTER_NAMES);
		
		String fileName = dialog.open();
		if (fileName != null) {
			boolean includeDefaults = MessageDialog.openQuestion(
					_window.getShell(),
					Messages.ExportPreferencesAction_DialogTitle,
					Messages.ExportPreferencesAction_IncludeDefaultsQuestion);
			try {
				CSSPlatformPlugin.getDefault().exportPluginCustomization(fileName, includeDefaults);
			} catch (CoreException e) {
				MessageDialog.openError(_window.getShell(),
						Messages.ExportPreferencesAction_DialogTitle,
						NLS.bind(Messages.ExportPreferencesAction_ErrorMessage, e.getMessage()));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(IWorkbenchWindow window) {
		_window = window;
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
