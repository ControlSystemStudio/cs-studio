/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.ui.internal.workspace;

import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.dialogs.StartupPreferencePage;

/**
 * Extends the Startup and Shutdown preference page with IDE-specific settings.
 * 
 * Note: want IDE settings to appear in main Workbench preference page (via
 * subclassing), however the superclass, StartupPreferencePage, is internal
 * 
 * <p>
 * <b>Code is based upon
 * <code>org.eclipse.ui.internal.ide.dialogs.IDEStartupPreferencePage</code>
 * in plugin <code>org.eclipse.ui.ide</code>.</b>
 * </p>
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class WorkspacePreferencePage extends StartupPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Refresh workspace on startup button.
	 */
	private Button _refreshButton;

	/**
	 * Prompt for workspace on startup button.
	 */
	private Button _launchPromptButton;

	/**
	 * Confirm exit button.
	 */
	private Button _exitPromptButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(final Composite parent) {
		Composite composite = createComposite(parent);

		createLaunchPromptPref(composite);
		createRefreshWorkspaceOnStartupPref(composite);
		createExitPromptPref(composite);

		Label space = new Label(composite, SWT.NONE);
		space.setLayoutData(new GridData());
		createEarlyStartupSelection(composite);

		return composite;
	}

	/**
	 * The default button has been pressed.
	 */
	@Override
	protected void performDefaults() {
		IPreferenceStore store = getCorePreferenceStore();

		_launchPromptButton.setSelection(true);

		_refreshButton
				.setSelection(store
						.getDefaultBoolean(ChooseWorkspaceData.REFRESH_WORKSPACE_ON_STARTUP));
		_exitPromptButton
				.setSelection(store
						.getDefaultBoolean(ChooseWorkspaceData.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW));

		super.performDefaults();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		IPreferenceStore store = getCorePreferenceStore();

		// store the refresh workspace on startup setting
		store.setValue(ChooseWorkspaceData.REFRESH_WORKSPACE_ON_STARTUP,
				_refreshButton.getSelection());

		// TODO: This should get the value from the configuration preference
		// area, but dj said we shouldn't use it yet; some final details are
		// being worked out. Hopefully it will be available soon, at which time
		// the entire recentWorkspaces.xml file can be removed. But until then,
		// this preference reads/writes the file each time.
		ChooseWorkspaceData.setShowDialogValue(_launchPromptButton
				.getSelection());

		// store the exit prompt on last window close setting
		store.setValue(ChooseWorkspaceData.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW,
				_exitPromptButton.getSelection());

		CSSPlatformUiPlugin.getDefault().savePluginPreferences();

		return super.performOk();
	}

	/**
	 * Create the "Refresh workspace on starup" button.
	 * 
	 * @param composite
	 *            The parent composite.
	 */
	protected void createRefreshWorkspaceOnStartupPref(final Composite composite) {
		_refreshButton = new Button(composite, SWT.CHECK);
		_refreshButton.setText(Messages
				.getString("WorkspacePreferencePage.REFRESH_ON_STARTUP")); //$NON-NLS-1$
		_refreshButton.setFont(composite.getFont());
		_refreshButton.setSelection(getCorePreferenceStore().getBoolean(
				ChooseWorkspaceData.REFRESH_WORKSPACE_ON_STARTUP));
	}

	/**
	 * Create the "Prompt for &workspace on startup" button.
	 * 
	 * @param composite
	 *            The parent composite.
	 */
	protected void createLaunchPromptPref(final Composite composite) {
		_launchPromptButton = new Button(composite, SWT.CHECK);
		_launchPromptButton.setText(Messages
				.getString("WorkspacePreferencePage.PROMPT_FOR_WORKSPACE")); //$NON-NLS-1$
		_launchPromptButton.setFont(composite.getFont());

		// TODO: This should get the value from the configuration preference
		// area, but dj said we shouldn't use it yet; some final details are
		// being worked out. Hopefully it will be available soon, at which time
		// the entire recentWorkspaces.xml file can be removed. But until then,
		// this preference reads/writes the file each time.
		_launchPromptButton.setSelection(ChooseWorkspaceData
				.getShowDialogValue());
	}

	/**
	 * Create the "Confirm exit when closing last window" button.
	 * 
	 * @param composite
	 *            The parent composite.
	 */
	protected void createExitPromptPref(final Composite composite) {
		_exitPromptButton = new Button(composite, SWT.CHECK);
		_exitPromptButton.setText(Messages
				.getString("WorkspacePreferencePage.CONFIRM_EXIT")); //$NON-NLS-1$
		_exitPromptButton.setFont(composite.getFont());
		_exitPromptButton.setSelection(getCorePreferenceStore().getBoolean(
				ChooseWorkspaceData.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW));
	}

	/**
	 * Returns the core preference store.
	 * 
	 * @return The core preference store.
	 */
	protected IPreferenceStore getCorePreferenceStore() {
		return CSSPlatformUiPlugin.getCorePreferenceStore();
	}
}
