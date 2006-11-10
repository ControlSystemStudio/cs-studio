package org.csstudio.platform.ui.internal.localization;

import org.csstudio.platform.LocaleService;
import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences for the locale setting.
 * 
 * <p/>
 *
 * For an overview over supportes locale ID's, see {@link java.util.Locale}.
 * 
 * @author awill
 */
public class LocalePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Default constructor.
	 */
	public LocalePreferencePage() {
		super(SWT.NULL);
		setMessage(Messages.getString("LocalePreferencePage.PAGE_TITLE")); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void createFieldEditors() {
		addField(new RadioGroupFieldEditor(LocaleService.PROP_LOCALE, Messages
				.getString("LocalePreferencePage.LOCALE"), //$NON-NLS-1$
				1, new String[][] {
						{ Messages.getString("LocalePreferencePage.DEFAULT"), //$NON-NLS-1$
								"" }, //$NON-NLS-1$
						{ Messages.getString("LocalePreferencePage.DE"), //$NON-NLS-1$
								"de" }, //$NON-NLS-1$
						{ Messages.getString("LocalePreferencePage.EN_US"), //$NON-NLS-1$
								"en_US" }, //$NON-NLS-1$
						{ Messages.getString("LocalePreferencePage.EN_GB"), //$NON-NLS-1$
								"en_GB" } }, getFieldEditorParent(), true)); //$NON-NLS-1$
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean performOk() {
		boolean result = super.performOk();
		LocaleService.setSystemLocale(getPreferenceStore().getString(
				LocaleService.PROP_LOCALE));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void propertyChange(final PropertyChangeEvent event) {
		setMessage(
				Messages.getString("LocalePreferencePage.CHANGE_MESSAGE"), IMessageProvider.INFORMATION); //$NON-NLS-1$			
		super.propertyChange(event);
	}

}
