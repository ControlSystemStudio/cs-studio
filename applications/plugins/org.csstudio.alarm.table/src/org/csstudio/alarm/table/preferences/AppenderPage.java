package org.csstudio.alarm.table.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This PreferencePage is the root node of the PreferencePages of the appenders.
 * 
 * @author Andre Grunow
 * @version 1.0
 */
public class AppenderPage
		extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage
{
	// --------------------------------------------------------------------------------------------

	/**
	 * Creates the PreferencePage (no settings may be set here).
	 */
	public AppenderPage()
	{
		super("Log-Appenders", SWT.NULL);
		setMessage("Please select a category to setup the appender(s).");
		noDefaultAndApplyButton();
	}

	// --------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors()
	{
		Label label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setText("Please select the appender which you want to setup.");
		label.setSize(300, 50);
	}

	public void init(IWorkbench workbench) 
	{
	}
}