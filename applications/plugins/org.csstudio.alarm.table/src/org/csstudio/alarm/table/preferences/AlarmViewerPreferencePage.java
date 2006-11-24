package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.Messages;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
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

public class AlarmViewerPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public AlarmViewerPreferencePage() {
		super(GRID);
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.AlarmViewerPreferencePage_columnNamesMessageKeys); 
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		System.out.println("AlarmPrefPage"); //$NON-NLS-1$
		addField(new ListEditor(AlarmViewerPreferenceConstants.P_STRINGAlarm, AlarmViewerPreferenceConstants.P_STRINGAlarm + ": ", getFieldEditorParent()){ //$NON-NLS-1$
			
			public String[] parseString(String stringList){
				System.out.println("Alarm: " + stringList); //$NON-NLS-1$
				return stringList.split(";"); //$NON-NLS-1$
			}
			
			public String getNewInputObject(){
				InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), Messages.AlarmViewerPreferencePage_enterColumnName, Messages.AlarmViewerPreferencePage_column, "", null); //$NON-NLS-3$
				if (inputDialog.open() == Window.OK) {
					return inputDialog.getValue();
				}
				return null;
			}
			
			public String createList(String[] items){
				String temp = ""; //$NON-NLS-1$
				for(int i = 0; i < items.length;i++){
					temp = temp + items[i] + ";"; //$NON-NLS-1$
				}
				return temp;
			}
			
			
		});
		
		
		addField(new StringFieldEditor(AlarmViewerPreferenceConstants.MAX, AlarmViewerPreferenceConstants.MAX + ": ", getFieldEditorParent())); //$NON-NLS-1$
		addField(new StringFieldEditor(AlarmViewerPreferenceConstants.REMOVE, AlarmViewerPreferenceConstants.REMOVE + ": ", getFieldEditorParent())); //$NON-NLS-1$
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	
	public void performApply(){
	}
	
	public void init(IWorkbench workbench) {
	}
	
}