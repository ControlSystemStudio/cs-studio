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

import java.util.Hashtable;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * Preference page for the alarm tree.
 */
public class AlarmTreePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

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
	public void createFieldEditors() {
		addField(
			new StringFieldEditor(PreferenceConstants.LDAP_URL, "&URL:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.LDAP_USER, "&User:", getFieldEditorParent()));
		StringFieldEditor sfe = new StringFieldEditor(PreferenceConstants.LDAP_PASSWORD, "&Password:", getFieldEditorParent());
		sfe.getTextControl(getFieldEditorParent()).setEchoChar('*');
		addField(new ListEditor(PreferenceConstants.FACILITIES, "&Facility names: ", getFieldEditorParent()){
			
			public String[] parseString(String stringList){
				return stringList.split(";");
			}
			
			public String getNewInputObject(){
				AlarmTreePlugin myPluginInstance = AlarmTreePlugin.getDefault();
				Hashtable<String,String> env = new Hashtable<String,String>();
	            env.put("java.naming.provider.url", myPluginInstance.getPluginPreferences().getString(PreferenceConstants.LDAP_URL));
	            env.put("java.naming.security.principal", myPluginInstance.getPluginPreferences().getString(PreferenceConstants.LDAP_USER));
	            env.put("java.naming.security.credentials", myPluginInstance.getPluginPreferences().getString(PreferenceConstants.LDAP_PASSWORD));		
				AddMountPointDlg inputDialog = new AddMountPointDlg(getFieldEditorParent().getShell(), env);
				if (inputDialog.open() == Window.OK) {
					return ((AddMountPointDlg) inputDialog).getResult();
				}
				return null;
			}
			
			public String createList(String[] items){
				String temp = "";
				for(int i = 0; i < items.length;i++){
					temp = temp + items[i] + ";";
				}
				return temp;
			}
			
			
		});
		
		// JMS Server Settings
		Group g2 = new Group(getFieldEditorParent(), SWT.NONE);
		g2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,2,1));
		g2.setLayout(new GridLayout(2,false));
		// -- Primery Server
		Label l1 = new Label(g2,SWT.NONE);
		l1.setText("Primary server");
		l1.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER, false, false,2,1));
		addField(new StringFieldEditor(
				PreferenceConstants.JMS_CONTEXT_FACTORY_PRIMARY,
				"Context factory:", g2));
		addField(new StringFieldEditor(
				PreferenceConstants.JMS_URL_PRIMARY,
				"Provider URL:", g2));
		new Label(g2,SWT.HORIZONTAL|SWT.SEPARATOR|SWT.CENTER).setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false,2,1));
		// -- Secondary Server
		Label l2 = new Label(g2,SWT.NONE);
		l2.setText("Secondary server");
		l2.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER, false, false,2,1));
		addField(new StringFieldEditor(
				PreferenceConstants.JMS_CONTEXT_FACTORY_SECONDARY,
				"Context factory:", g2));
		addField(new StringFieldEditor(
				PreferenceConstants.JMS_URL_SECONDARY,
				"Provider URL:", g2));
		// --INITIAL_CONTEXT_FACTORY
		new Label(g2,SWT.HORIZONTAL|SWT.SEPARATOR|SWT.CENTER).setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false,2,1));
		addField(new StringFieldEditor(PreferenceConstants.JMS_QUEUE,
				"Queue:", g2));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}