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
package org.csstudio.platform.utility.jms.ui.preferences;

import org.csstudio.platform.utility.jms.Activator;
import org.csstudio.platform.utility.jms.preferences.PreferenceConstants;
import org.csstudio.platform.utility.jms.ui.internal.localization.Messages;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference page for configuring the shared JMS connection services.
 */
public class SharedJmsConnectionPreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

    private ScopedPreferenceStore _prefStore;

    public SharedJmsConnectionPreferencePage() {
        super(GRID);

        _prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
                Activator.getDefault().getBundle().getSymbolicName());
        setPreferenceStore(_prefStore);

        setDescription(Messages.SharedJmsConnectionPreferencePage_Description);
    }

    /**
     * Creates the field editors. Field editors are abstractions of
     * the common GUI blocks needed to manipulate various types
     * of preferences. Each field editor knows how to save and
     * restore itself.
     */
    @Override
    public void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.SENDER_BROKER_URL,
                Messages.SharedJmsConnectionPreferencePage_SenderUrlLabel,
                getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.RECEIVER_BROKER_URL_1,
                Messages.SharedJmsConnectionPreferencePage_ReceiverUrl1Label,
                getFieldEditorParent()));
        addField(new StringFieldEditor(PreferenceConstants.RECEIVER_BROKER_URL_2,
                Messages.SharedJmsConnectionPreferencePage_ReceiverUrl2Label,
                getFieldEditorParent()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench) {
    }
}
