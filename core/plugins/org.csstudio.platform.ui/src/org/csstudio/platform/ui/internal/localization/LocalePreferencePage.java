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
 * @author Alexander Will
 */
public class LocalePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Default constructor.
	 */
	public LocalePreferencePage() {
		super(SWT.NULL);
		setMessage(Messages.LocalePreferencePage_PAGE_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	protected final void createFieldEditors() {
		addField(new RadioGroupFieldEditor(LocaleService.PROP_LOCALE,
                  Messages.LocalePreferencePage_LOCALE,
				1, new String[][] {
						{ Messages.LocalePreferencePage_DEFAULT,
								"" }, //$NON-NLS-1$
						{ Messages.LocalePreferencePage_DE,
								"de" }, //$NON-NLS-1$
						{ Messages.LocalePreferencePage_EN_US,
								"en_US" }, //$NON-NLS-1$
						{ Messages.LocalePreferencePage_EN_GB,
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
		setMessage(Messages.LocalePreferencePage_CHANGE_MESSAGE,
                   IMessageProvider.INFORMATION);
		super.propertyChange(event);
	}

}
