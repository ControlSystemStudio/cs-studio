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
package org.csstudio.auth.ui.internal.preferences;

import org.csstudio.auth.internal.AuthActivator;
import org.csstudio.auth.security.SecurityFacade;
import org.csstudio.auth.ui.internal.localization.Messages;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The preference page for the css authentication service.
 *
 * @author Jan Hatje
 */
public class AuthenticationPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    /**
     * Default constructor.
     */
    public AuthenticationPreferencePage() {
        super(SWT.NULL);
        setMessage(Messages.AuthenticationPreferencePage_PAGE_TITLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void createFieldEditors() {
        addField(new BooleanFieldEditor(
                SecurityFacade.ONSITE_LOGIN_PREFERECE,
                Messages.AuthenticationPreferencePage_LOGIN_ON_STARTUP,
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(
                SecurityFacade.OFFSITE_LOGIN_PREFERENCE,
                Messages.AuthenticationPreferencePage_LOGIN_ON_STARTUP_OFFSITE,
                getFieldEditorParent()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final IPreferenceStore doGetPreferenceStore() {
        IPreferenceStore preferenceStore = new ScopedPreferenceStore(
                new InstanceScope(), AuthActivator.ID);
        return preferenceStore;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final IWorkbench workbench)
    {
        // NOP
    }
}
