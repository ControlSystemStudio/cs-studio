
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

package org.csstudio.utility.screenshot.desy.preference;

import org.csstudio.utility.screenshot.desy.DestinationPlugin;
import org.csstudio.utility.screenshot.desy.internal.localization.LogbookSenderMessages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DestinationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    public DestinationPreferencePage() {
        super();
        setPreferenceStore(DestinationPlugin.getDefault().getPreferenceStore());
    }

    /*public void createControl(Composite parent)
    {
        super.createControl(parent);
    }*/

    @Override
    public void createFieldEditors() {
        
    	Composite parent = getFieldEditorParent();
        parent.setLayout(new GridLayout(1, true));
        parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        
        Composite mailAddress = createGroup(parent, "DestinationPreferencePage.GROUP_MAIL_ADDRESS");
        addField(new StringFieldEditor(DestinationPreferenceConstants.MAIL_ADDRESS_SENDER, LogbookSenderMessages.getString("DestinationPreferencePage.MAIL_ADDRESS_SENDER"), mailAddress));
        
        Composite mailServer = createGroup(parent, "DestinationPreferencePage.GROUP_MAIL_SERVER");
        addField(new StringFieldEditor(DestinationPreferenceConstants.MAIL_SERVER, LogbookSenderMessages.getString("DestinationPreferencePage.MAIL_SERVER_NAME"), mailServer));

        Composite logbookNames = createGroup(parent, "DestinationPreferencePage.GROUP_LOGBOOKS");
        addField(new AddRemoveListFieldEditor(DestinationPreferenceConstants.LOGBOOK_NAMES, LogbookSenderMessages.getString("DestinationPreferencePage.LOGBOOK_NAMES"), logbookNames));

        Composite groupNames = createGroup(parent, "DestinationPreferencePage.GROUP_GROUPNAMES");
        addField(new AddRemoveListFieldEditor(DestinationPreferenceConstants.GROUP_NAMES, LogbookSenderMessages.getString("DestinationPreferencePage.GROUP_NAMES"), groupNames));
    }

    private Composite createGroup(Composite composite, String label) {
        
    	Group group = new Group(composite, 0);
        group.setText(LogbookSenderMessages.getString(label));
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        return new Composite(group, SWT.NONE);
    }

    public void init(IWorkbench workbench) {
    	// Can be empty
    }
}
