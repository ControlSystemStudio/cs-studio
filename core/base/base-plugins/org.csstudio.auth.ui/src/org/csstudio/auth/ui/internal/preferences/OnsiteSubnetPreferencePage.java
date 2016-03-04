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
 package org.csstudio.auth.ui.internal.preferences;

import org.csstudio.auth.internal.AuthActivator;
import org.csstudio.auth.internal.subnet.OnsiteSubnetPreferences;
import org.csstudio.auth.internal.subnet.Subnet;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference page for the onsite subnets.
 *
 * @author Jan Hatje
 */
public class OnsiteSubnetPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    /**
     * Default constructor.
     */
    public OnsiteSubnetPreferencePage() {
        super(SWT.NULL);
        setMessage("Onsite Subnets");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void createFieldEditors() {
        addField(new ListEditor(OnsiteSubnetPreferences.PREFERENCE_KEY,
                "Subnets: ", getFieldEditorParent()){

            public String[] parseString(String stringList){
                return stringList.split(",");
            }

            public String getNewInputObject(){
                AddSubnetDialog dialog = new AddSubnetDialog(getShell());
                if (dialog.open() == Window.OK) {
                    Subnet s = dialog.getSubnet();
                    return s != null ? s.toString() : null;
                }
                return null;
            }

            public String createList(String[] items){
                StringBuilder temp = new StringBuilder();
                for(int i = 0; i < items.length; i++) {
                    temp.append(items[i]);
                    temp.append(",");
                }
                return temp.toString();
            }


        });
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
        // nothing to do
    }

}
