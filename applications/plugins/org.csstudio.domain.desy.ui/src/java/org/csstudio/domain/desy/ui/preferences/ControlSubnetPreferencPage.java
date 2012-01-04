/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.ui.preferences;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.preferences.ControlSubnetPreference;
import org.csstudio.platform.util.StringUtil;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author hrickens
 * @since 23.12.2011
 */
public class ControlSubnetPreferencPage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * Constructor.
     */
    public ControlSubnetPreferencPage() {
        super(GRID);
        setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(),
                                                     "org.csstudio.domain.desy"));
        setDescription("Control Subnet Preferences");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(@Nullable final IWorkbench workbench) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFieldEditors() {
        final ListEditor controlSubnetList = new ListEditor(ControlSubnetPreference.CONTROL_SUBNETS.getKeyAsString(),"Control Subnet", getFieldEditorParent()) {

            @Override
            @Nonnull
            protected String[] parseString(@Nonnull final String stringList) {
                return stringList.split(ControlSubnetPreference.STRING_LIST_SEPARATOR);
            }

            @Override
            @CheckForNull
            protected String getNewInputObject() {
                final AddSubnetDialog dialog = new AddSubnetDialog(getShell());
                if (dialog.open() == Window.OK) {
                    final String s = dialog.getSubnet();
                    return s;
                }
                return null;
            }

            @Override
            @Nonnull
            protected String createList(@Nonnull final String[] items) {
                return StringUtil.join(items, ControlSubnetPreference.STRING_LIST_SEPARATOR);
            }
        };
        addField(controlSubnetList);

    }

}
