package org.csstudio.alarm.treeView.preferences;

import java.util.Hashtable;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.views.AddMountPointDlg;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class LdapTreePreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public LdapTreePreferences() {
		super(GRID);
		setPreferenceStore(AlarmTreePlugin.getDefault().getPreferenceStore());
		setDescription("Ldap preferences");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.PROTOCOL,
			"Choose a directory protocol",
			1,
			new String[][] { { "&LDAP", "ldap" }, {
				"&EPICS directory service", "epics" }
		}, getFieldEditorParent()));
		addField(
			new StringFieldEditor(PreferenceConstants.URL, "&URL:", getFieldEditorParent()));
		addField(
				new StringFieldEditor(PreferenceConstants.USER, "&User:", getFieldEditorParent()));
		StringFieldEditor sfe = new StringFieldEditor(PreferenceConstants.PASSWORD, "&Password:", getFieldEditorParent());
		sfe.getTextControl(getFieldEditorParent()).setEchoChar('*');
//		addField(
//				new StringFieldEditor(PreferenceConstants.NODE, "&Mount points:", getFieldEditorParent()));
		addField(new ListEditor(PreferenceConstants.NODE, "&Mount points: ", getFieldEditorParent()){
			
			public String[] parseString(String stringList){
				return stringList.split(";");
			}
			
			public String getNewInputObject(){
				AlarmTreePlugin myPluginInstance = AlarmTreePlugin.getDefault();
				Hashtable<String,String> env = new Hashtable<String,String>();
	            env.put("java.naming.provider.url", myPluginInstance.getPluginPreferences().getString(PreferenceConstants.URL));
	            env.put("java.naming.security.principal", myPluginInstance.getPluginPreferences().getString(PreferenceConstants.USER));
	            env.put("java.naming.security.credentials", myPluginInstance.getPluginPreferences().getString(PreferenceConstants.PASSWORD));		
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
		
		addField(new StringFieldEditor(PreferenceConstants.JMSURL, "&JMS URL:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.JMSTOPIC, "JMS &TOPIC:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}