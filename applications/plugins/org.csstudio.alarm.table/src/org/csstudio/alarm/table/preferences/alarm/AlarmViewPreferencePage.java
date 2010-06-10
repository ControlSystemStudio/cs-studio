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
package org.csstudio.alarm.table.preferences.alarm;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.preferences.ExchangeablePreferenceColumnTableEditor;
import org.csstudio.alarm.table.preferences.PreferenceTopicTableEditor;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class AlarmViewPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public AlarmViewPreferencePage() {
		super(GRID);
		setPreferenceStore(JmsLogsPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.AlarmViewerPreferencePage_columnNamesMessageKeys);
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
				AlarmViewPreferenceConstants.TOPIC_SET, "&Topic Sets: ",
				getFieldEditorParent());
		addField(preferenceTopicTableEditor);
		ExchangeablePreferenceColumnTableEditor preferenceColumnTableEditor = new ExchangeablePreferenceColumnTableEditor(
				AlarmViewPreferenceConstants.P_STRING_ALARM, "Column Settings",
				getFieldEditorParent());
		preferenceTopicTableEditor.setColumnTableReference(preferenceColumnTableEditor);
		addField(preferenceColumnTableEditor);
	}

	public void init(IWorkbench workbench) {
	}
}