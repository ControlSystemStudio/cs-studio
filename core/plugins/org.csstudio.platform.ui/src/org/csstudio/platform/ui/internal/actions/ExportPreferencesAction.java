/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
