package org.csstudio.alarm.table.preferences;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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
		getFieldEditorParent().setSize(300, 400);
		System.out.println("AlarmPrefPage"); //$NON-NLS-1$
		addField(new ListEditor(AlarmViewerPreferenceConstants.P_STRINGAlarm, AlarmViewerPreferenceConstants.P_STRINGAlarm + ": ", getFieldEditorParent()){ //$NON-NLS-1$

			public String[] parseString(String stringList){
				System.out.println("Alarm: " + stringList); //$NON-NLS-1$
				return stringList.split(";"); //$NON-NLS-1$
			}

			public String getNewInputObject(){
				InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), Messages.AlarmViewerPreferencePage_enterColumnName, Messages.AlarmViewerPreferencePage_column, "", null); //$NON-NLS-1$
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
		//
		// Server Settings
		Group g2 = new Group(getFieldEditorParent(), SWT.NONE);
		g2.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,2,1));
		g2.setLayout(new GridLayout(2,false));
		// -- Primery Server
		Label l1 = new Label(g2,SWT.NONE);
		l1.setText(Messages.JMSPreferencePage_ALARM_PRIMERY_SERVER);
		l1.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER, false, false,2,1));
		addField(new StringFieldEditor(AlarmViewerPreferenceConstants.INITIAL_PRIMARY_CONTEXT_FACTORY, Messages.JMSPreferencePage_ALARM_CONTEXT_FACTORY, g2));
        final StringFieldEditor primary_url = new StringFieldEditor(AlarmViewerPreferenceConstants.PRIMARY_URL, Messages.JMSPreferencePage_ALARM_PROVIDER_URL, g2); 
		addField(primary_url);
		new Label(g2,SWT.HORIZONTAL|SWT.SEPARATOR|SWT.CENTER).setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false,2,1));
		// -- Secondary Server
		Label l2 = new Label(g2,SWT.NONE);
		l2.setText(Messages.JMSPreferencePage_ALARM_SECONDARY_SERVER);
		l2.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER, false, false,2,1));
		addField(new StringFieldEditor(AlarmViewerPreferenceConstants.INITIAL_SECONDARY_CONTEXT_FACTORY, Messages.JMSPreferencePage_ALARM_CONTEXT_FACTORY, g2));
        final StringFieldEditor secondary_url = new StringFieldEditor(AlarmViewerPreferenceConstants.SECONDARY_URL, Messages.JMSPreferencePage_ALARM_PROVIDER_URL, g2);
		addField(secondary_url);
		// --INITIAL_CONTEXT_FACTORY
		new Label(g2,SWT.HORIZONTAL|SWT.SEPARATOR|SWT.CENTER).setLayoutData(new GridData(SWT.FILL,SWT.FILL, false, false,2,1));
		addField(new StringFieldEditor(AlarmViewerPreferenceConstants.QUEUE, Messages.JMSPreferencePage_ALARM_QUEUE_NAME, g2));
        final StringFieldEditor sender_URL = new StringFieldEditor(AlarmViewerPreferenceConstants.SENDER_URL, Messages.JMSPreferencePage_ALARM_SENDER_URL, g2);
        sender_URL.getTextControl(g2).setVisible(true);
        sender_URL.getTextControl(g2).setEditable(true);
        addField(sender_URL);
//        primary_url.getTextControl(g2).addKeyListener(new KeyListener(){
//
//            public void keyPressed(KeyEvent e) {}
//
//            public void keyReleased(KeyEvent e) {
//                sender_URL.setStringValue("failover:("+primary_url.getStringValue()+","+secondary_url.getStringValue()+")?maxReconnectDelay=2000");
//            }
//            
//        });
//
//        secondary_url.getTextControl(g2).addKeyListener(new KeyListener(){
//
//            public void keyReleased(KeyEvent e) {
//                sender_URL.setStringValue("failover:("+primary_url.getStringValue()+","+secondary_url.getStringValue()+"");
//            }
//
//            public void keyPressed(KeyEvent e) {}
//            
//        });


       }
    

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */

	public void performApply(){
	}

	public void init(IWorkbench workbench) {
	}

}