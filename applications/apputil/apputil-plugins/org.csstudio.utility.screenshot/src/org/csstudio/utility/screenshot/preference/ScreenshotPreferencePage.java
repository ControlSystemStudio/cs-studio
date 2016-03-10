
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

package org.csstudio.utility.screenshot.preference;

import org.csstudio.utility.screenshot.ScreenshotPlugin;
import org.csstudio.utility.screenshot.internal.localization.ScreenshotMessages;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ScreenshotPreferencePage extends FieldEditorPreferencePage
                                      implements IWorkbenchPreferencePage {

    public ScreenshotPreferencePage() {
        super(GRID);
        setPreferenceStore(ScreenshotPlugin.getDefault().getPreferenceStore());
    }

    /*public void createControl(Composite parent)
    {
        super.createControl(parent);
    }*/

    @Override
    public void createFieldEditors() {

        Composite parent = getFieldEditorParent();
        parent.setLayout(new GridLayout(2, true));
        parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        // Composite mailServer = createGroup(parent, "ScreenshotPreferencePage.GROUP_MAIL_SERVER");
        addField(new StringFieldEditor(ScreenshotPreferenceConstants.MAIL_SERVER, ScreenshotMessages.getString("ScreenshotPreferencePage.MAIL_SERVER_NAME"), parent));

        // Composite mailAddress = createGroup(parent, "ScreenshotPreferencePage.GROUP_MAIL_ADDRESS");
        addField(new StringFieldEditor(ScreenshotPreferenceConstants.MAIL_ADDRESS_SENDER, ScreenshotMessages.getString("ScreenshotPreferencePage.MAIL_ADDRESS_SENDER"), parent));

        // Composite copyMail = createGroup(parent, "ScreenshotPreferencePage.GROUP_COPY_MAIL");
        addField(new BooleanFieldEditor(ScreenshotPreferenceConstants.COPY_TO_SENDER, ScreenshotMessages.getString("ScreenshotPreferencePage.COPY_TO_SENDER"), parent));
    }

//    private Composite createGroup(Composite composite, String label) {
//
//        Group group = new Group(composite, 0);
//        group.setText(ScreenshotMessages.getString(label));
//
//        group.setLayout(new GridLayout(2, true));
//        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//
//        return new Composite(group, 0);
//    }

    @Override
    public void init(IWorkbench workbench) {
        // Can be empty
    }
}
