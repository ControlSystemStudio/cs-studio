package org.csstudio.platform.ui.internal.security;

import org.csstudio.platform.security.AuthenticationService;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.internal.localization.Messages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * The preference page for the css authentication service.
 * 
 * @author awill
 */
public class AuthenticationPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Default constructor.
	 */
	public AuthenticationPreferencePage() {
		super(SWT.NULL);
		setMessage(Messages
				.getString("AuthenticationPreferencePage.PAGE_TITLE")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void createFieldEditors() {
		addField(new BooleanFieldEditor(
				AuthenticationService.PROP_AUTH_LOGIN,
				Messages
						.getString("AuthenticationPreferencePage.LOGIN_ON_STARTUP"), getFieldEditorParent())); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IPreferenceStore doGetPreferenceStore() {
		return CSSPlatformUiPlugin.getCorePreferenceStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final IWorkbench workbench) {
	}
}
