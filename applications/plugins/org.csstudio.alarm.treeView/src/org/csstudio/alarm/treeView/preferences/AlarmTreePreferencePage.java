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
 package org.csstudio.alarm.treeView.preferences;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.views.AddMountPointDlg;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * Preference page for the alarm tree.
 */
public class AlarmTreePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	/**
	 * Creates a new alarm tree preference page.
	 */
	public AlarmTreePreferencePage() {
		super(GRID);
		setPreferenceStore(AlarmTreePlugin.getDefault().getPreferenceStore());
		setDescription("Alarm tree preferences");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public final void createFieldEditors() {
		addField(new ListEditor(PreferenceConstants.FACILITIES, "&Facility names: ", getFieldEditorParent()){
			
			public String[] parseString(final String stringList){
				return stringList.split(";");
			}
			
			public String getNewInputObject(){
				AddMountPointDlg inputDialog = new AddMountPointDlg(getFieldEditorParent().getShell());
				if (inputDialog.open() == Window.OK) {
					return ((AddMountPointDlg) inputDialog).getResult();
				}
				return null;
			}
			
			public String createList(final String[] items){
				String temp = "";
				for(int i = 0; i < items.length;i++){
					temp = temp + items[i] + ";";
				}
				return temp;
			}
			
			
		});
		
		Group jmsGroup = new Group(getFieldEditorParent(), SWT.SHADOW_ETCHED_IN);
		jmsGroup.setText("JMS settings");
		jmsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		jmsGroup.setLayout(new GridLayout(2, false));
		
		addField(new StringFieldEditor(
				PreferenceConstants.JMS_URL_PRIMARY,
				"First transport URI:", jmsGroup));
		addField(new StringFieldEditor(
				PreferenceConstants.JMS_URL_SECONDARY,
				"Second transport URI:", jmsGroup));
		addField(new StringFieldEditor(PreferenceConstants.JMS_TOPICS,
				"Topics:", jmsGroup));
	}


	/**
	 * {@inheritDoc}
	 */
	public void init(final IWorkbench workbench) {
	}
	
}
