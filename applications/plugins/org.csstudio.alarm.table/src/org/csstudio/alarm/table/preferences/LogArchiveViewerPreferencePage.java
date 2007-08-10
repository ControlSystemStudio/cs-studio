package org.csstudio.alarm.table.preferences;


import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;

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

public class LogArchiveViewerPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public LogArchiveViewerPreferencePage() {
		super(GRID);
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.LogArchiveViewerPreferencePage_columnNamesMessageKeys);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new ListEditor(LogArchiveViewerPreferenceConstants.P_STRINGArch, LogArchiveViewerPreferenceConstants.P_STRINGArch + ": ", getFieldEditorParent()){ //$NON-NLS-1$

			public String[] parseString(String stringList){
				return stringList.split(";"); //$NON-NLS-1$
			}

			public String getNewInputObject(){
				InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), Messages.LogArchiveViewerPreferencePage_newColumnName, Messages.LogArchiveViewerPreferencePage_column, "", null); //$NON-NLS-1$
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
		StringFieldEditor date = new StringFieldEditor(LogArchiveViewerPreferenceConstants.DATE_FORMAT,Messages.LogArchiveViewerPreferencePage_dateFormat,getFieldEditorParent());
		date.getLabelControl(getFieldEditorParent()).setToolTipText(Messages.LogArchiveViewerPreferencePage_javaDateFormat);
		addField(date);
		StringFieldEditor answerSize = new StringFieldEditor(LogArchiveViewerPreferenceConstants.MAX_ANSWER_SIZE,Messages.LogArchiveViewerPreferencePage_maxAnswerSize,getFieldEditorParent());
		answerSize.getLabelControl(getFieldEditorParent()).setToolTipText(Messages.LogArchiveViewerPreferencePage_javaDateFormat);
		addField(answerSize);
		}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */

	public void performApply(){
	}

	public void init(IWorkbench workbench) {
	}


}