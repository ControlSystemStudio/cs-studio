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
package org.csstudio.alarm.table.preferences.log;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.ExchangeablePreferenceColumnTableEditor;
import org.csstudio.alarm.table.preferences.PreferenceColumnTableEditor;
import org.csstudio.alarm.table.preferences.PreferenceTopicTableEditor;
import org.csstudio.alarm.table.preferences.alarm.AlarmViewPreferenceConstants;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for log view. 1. Table for column names and width 2. Number
 * of maximum rows of log table 3. Table for sets of monitored topics
 * 
 */
public class LogViewPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public LogViewPreferencePage() {
		super(GRID);
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.columnNamesMessageKeys);
	}

	public void createFieldEditors() {
		Label l = new Label(getFieldEditorParent(), SWT.NONE);
		Font font = l.getFont();
		FontData fontData = font.getFontData()[0];
		fontData.setStyle(1);
		Font font2 = CustomMediaFactory.getInstance().getFont(fontData);
		l.setFont(font2);
		l.setText("Die Einstellungen in den Spalten Popup Mode und Auto Start werden noch nicht ausgewertet.");
		
		PreferenceTopicTableEditor preferenceTopicTableEditor = new PreferenceTopicTableEditor(
				LogViewPreferenceConstants.TOPIC_SET, "&Topic Sets: ",
				getFieldEditorParent());
		addField(preferenceTopicTableEditor);
		ExchangeablePreferenceColumnTableEditor preferenceColumnTableEditor = new ExchangeablePreferenceColumnTableEditor(
				LogViewPreferenceConstants.P_STRING, "Column Settings",
				getFieldEditorParent());
		preferenceTopicTableEditor.setColumnTableReference(preferenceColumnTableEditor);
		addField(preferenceColumnTableEditor);

		Group g1 = new Group(getFieldEditorParent(), SWT.NONE);
		g1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		
		addField(new StringFieldEditor(LogViewPreferenceConstants.MAX,
				LogViewPreferenceConstants.MAX + ": ", g1)); //$NON-NLS-1$
	}

	public void init(IWorkbench workbench) {
	}
}