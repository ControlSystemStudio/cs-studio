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

public class LogViewerPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public LogViewerPreferencePage() {
		super(GRID);
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.columnNamesMessageKeys);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		System.out.println("LogPrefPage"); //$NON-NLS-1$

		addField(new ListEditor(LogViewerPreferenceConstants.P_STRING, LogViewerPreferenceConstants.P_STRING + ": ", getFieldEditorParent()){ //$NON-NLS-1$
			
			public String[] parseString(String stringList){
				System.out.println("Log: " + stringList); //$NON-NLS-1$

				return stringList.split(";"); //$NON-NLS-1$
			}
			
			public String getNewInputObject(){
				InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), Messages.newColumnName, Messages.column, "", null); //$NON-NLS-3$
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
		
		
		addField(new StringFieldEditor(LogViewerPreferenceConstants.MAX, LogViewerPreferenceConstants.MAX + ": ", getFieldEditorParent())); //$NON-NLS-1$
		addField(new StringFieldEditor(LogViewerPreferenceConstants.REMOVE, LogViewerPreferenceConstants.REMOVE + ": ", getFieldEditorParent())); //$NON-NLS-1$
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	
	public void performApply(){
	}
	
	public void init(IWorkbench workbench) {
	}
	
}