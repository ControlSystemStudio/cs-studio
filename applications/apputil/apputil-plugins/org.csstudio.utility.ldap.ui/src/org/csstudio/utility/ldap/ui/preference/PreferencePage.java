/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.ui.preference;

import org.csstudio.utility.ldap.preference.LdapPreference;
import org.csstudio.utility.ldap.ui.Activator;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    public PreferencePage() {
        super(GRID);
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,
                Activator.getDefault().getBundle().getSymbolicName()));

        String string = Messages.getString("PreferencePage.LDAP");
        setDescription(string); //$NON-NLS-1$
    }

    /**
     * Creates the field editors. Field editors are abstractions of
     * the common GUI blocks needed to manipulate various types
     * of preferences. Each field editor knows how to save and
     * restore itself.
     */
    @Override
    public void createFieldEditors() {
        addField(new StringFieldEditor(LdapPreference.URL.getKeyAsString(),
                                       Messages.getString("PreferencePage.URL"),
                                       getFieldEditorParent())); //$NON-NLS-1$
        addField(new StringFieldEditor(LdapPreference.USER_DN.getKeyAsString(),
                                       Messages.getString("PreferencePage.DN"),
                                       getFieldEditorParent())); //$NON-NLS-1$

        final StringFieldEditor sfeP =
            new StringFieldEditor(LdapPreference.USER_PASSWORD.getKeyAsString(),
                                  Messages.getString("PreferencePage.PASS"),
                                  getFieldEditorParent()); //$NON-NLS-1$

        sfeP.getTextControl(getFieldEditorParent()).setEchoChar('*');
        addField(sfeP);

        addField(new StringFieldEditor(LdapPreference.SECURITY_PROTOCOL.getKeyAsString(),
                                       Messages.getString("PreferencePage.SECURITY_PROTOCOL"),
                                       getFieldEditorParent())); //$NON-NLS-1$

        addField(new StringFieldEditor(LdapPreference.SECURITY_AUTH.getKeyAsString(),
                                       Messages.getString("PreferencePage.SECURITY_AUTHENTICATION"),
                                       getFieldEditorParent())); //$NON-NLS-1$

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(final IWorkbench workbench) {
        // Empty
    }


}
