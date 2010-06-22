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
package org.csstudio.alarm.service.preferences;

import javax.annotation.Nullable;

import org.csstudio.alarm.service.AlarmServiceActivator;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class AlarmServicePreferencePage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public AlarmServicePreferencePage() {
        super(GRID);
        setPreferenceStore(AlarmServiceActivator.getDefault().getPreferenceStore());
    }

    @Override
    public void createFieldEditors() {
        addField(newImplSelectionEditor());
        addField(newListEditor());
    }

    @Override
    public void init(@Nullable final IWorkbench workbench) {
        // Nothing to do
    }

    private RadioGroupFieldEditor newImplSelectionEditor() {
        final Group group = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
        group.setText("Alarm Service");
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        group.setLayout(new GridLayout(2, false));

        final String[][] contextTypes = {
                {"DAL", Boolean.TRUE.toString()},
                {"JMS", Boolean.FALSE.toString()}
              };
        return new RadioGroupFieldEditor(AlarmPreference.ALARMSERVICE_IS_DAL_IMPL.getKeyAsString(),
                                           "Select the implementation for the Alarm Service.\nYou must restart CSS after a change to take effect.",
                                           contextTypes.length,
                                           contextTypes,
                                           group);
    }

    private ListEditor newListEditor() {
        final Group group = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
        group.setText("Facility Names");
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        group.setLayout(new GridLayout(2, false));

        return new ListEditor(AlarmPreference.ALARMSERVICE_FACILITIES.getKeyAsString(), "Select Facility Names for use in Alarm Tree and Alarm Table\n" +
        		"The selected facilities will show up in the Alarm Tree. They will also be used to retrieve\n" +
        		"the initial state of the contained PVs.", group){

            public String[] parseString(final String stringList){
                return stringList.split(AlarmPreference.STRING_LIST_SEPARATOR);
            }

            public String getNewInputObject(){
                AddMountPointDlg inputDialog = new AddMountPointDlg(getFieldEditorParent().getShell());
                if (inputDialog.open() == Window.OK) {
                    return (inputDialog).getResult();
                }
                return null;
            }

            public String createList(final String[] items){
                String temp = "";
                for (String item : items) {
                    temp = temp + item + AlarmPreference.STRING_LIST_SEPARATOR;
                }
                return temp;
            }
        };
    }
}
