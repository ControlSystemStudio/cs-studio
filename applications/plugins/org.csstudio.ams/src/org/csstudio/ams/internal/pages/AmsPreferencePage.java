
/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.ams.internal.pages;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Messages;
import org.csstudio.ams.PasswordEditor;
import org.csstudio.ams.internal.AmsPreferenceKey;

/**
 * A preference page that contains the following types of preferences:<br>
 * </p>
 * <ul>
 * </ul>
 * 
 * @author Alexander Will
 * 
 */
public class AmsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Standard constructor.
	 */
	public AmsPreferencePage() {
		super(GRID);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
    @Override
	protected final void createFieldEditors() 
	{
		TabFolder tabs = new TabFolder(getFieldEditorParent(), SWT.NONE);
		tabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// database settings
		TabItem tabDb = new TabItem(tabs, SWT.NONE);
		tabDb.setText(Messages.Pref_Database);
		Composite c0 = new Composite(tabs, SWT.NONE);
		c0.setLayout(new GridLayout(1, true));
		c0.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabDb.setControl(c0);

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);
		
		addField(new StringFieldEditor(AmsPreferenceKey.P_CONFIG_DATABASE_CONNECTION,
                Messages.Pref_ConfigDBCon, 120, c0));		
		addField(new StringFieldEditor(AmsPreferenceKey.P_CONFIG_DATABASE_USER,
				Messages.Pref_ConfigDBUser, c0));
		addField(new PasswordEditor(AmsPreferenceKey.P_CONFIG_DATABASE_PASSWORD,
				Messages.Pref_ConfigDBPassword, c0));

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_APP_DATABASE_CONNECTION,
				Messages.Pref_AppDBCon, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_APP_DATABASE_USER,
				Messages.Pref_AppDBUser, c0));
		addField(new PasswordEditor(AmsPreferenceKey.P_APP_DATABASE_PASSWORD,
				Messages.Pref_AppDBPassword, c0));

		// filter key field of message
		TabItem tabFilterKeyFields = new TabItem(tabs, SWT.NONE);
		tabFilterKeyFields.setText(Messages.Pref_FilterKeyFields);
		c0 = new Composite(tabs,SWT.NONE);
		c0.setLayout(new GridLayout(1,false));
		c0.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		tabFilterKeyFields.setControl(c0);		

		addField(new ListEditor(
				AmsPreferenceKey.P_FILTER_KEYFIELDS, ""/*Messages.Pref_FilterKeyFields*/, c0)
		{
			@Override
            public String[] parseString(String stringList)
			{
				return stringList.split(";");
			}
			
			@Override
            public String getNewInputObject()
			{
				InputDialog inputDialog = new InputDialog(getFieldEditorParent().getShell(), 
						Messages.Pref_FilterKeyFieldEnterOne, 
						Messages.Pref_FilterKeyField, 
						"", 
						null);
				if (inputDialog.open() == Window.OK) 
				{
					return inputDialog.getValue();
				}
				return null;
			}
			
			@Override
            public String createList(String[] items)
			{
				String temp = "";
				for(int i = 0; i < items.length;i++)
					temp = temp + items[i] + ";";
				return temp;
			}
		});

		// jms communication
		TabItem tabJmsSources = new TabItem(tabs, SWT.NONE);
		tabJmsSources.setText(Messages.P_JMS_SOURCES);
		c0 = new Composite(tabs,SWT.NONE);
		c0.setLayout(new GridLayout(1,false));
		c0.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		tabJmsSources.setControl(c0);		

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS,
				Messages.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXTERN_PROVIDER_URL_1,
				Messages.P_JMS_EXTERN_PROVIDER_URL_1, c0));
        // ADDED BY: Markus Moeller, 02.08.2007
        addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXTERN_PROVIDER_URL_2,
                Messages.P_JMS_EXTERN_PROVIDER_URL_2, c0));
        // ADDED BY: Markus Moeller, 13.08.2007
        addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXTERN_SENDER_PROVIDER_URL,
                Messages.P_JMS_EXTERN_SENDER_PROVIDER_URL, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY,
				Messages.P_JMS_EXTERN_CONNECTION_FACTORY, c0));
		new Label(c0, SWT.NONE);
		addField(new BooleanFieldEditor(AmsPreferenceKey.P_JMS_EXTERN_CREATE_DURABLE,
                Messages.P_JMS_EXTERN_CREATE_DURABLE, c0));

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS,
				Messages.P_JMS_AMS_CONNECTION_FACTORY_CLASS, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
				Messages.P_JMS_AMS_PROVIDER_URL_1, c0));
		
		// ADDED BY: Markus Moeller, 02.08.2007        
        StringFieldEditor editor = new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                Messages.P_JMS_AMS_PROVIDER_URL_2, c0);
        // editor.setEnabled(false, c0);
        addField(editor);
        // ADDED BY: Markus Moeller, 13.08.2007
        editor = new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL,
                Messages.P_JMS_AMS_SENDER_PROVIDER_URL, c0);
        // editor.setEnabled(false, c0);
        addField(editor);        
        
        addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY,
				Messages.P_JMS_AMS_CONNECTION_FACTORY, c0));
        new Label(c0, SWT.NONE);
        addField(new BooleanFieldEditor(AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE,
                Messages.P_JMS_AMS_CREATE_DURABLE, c0));

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS,
				Messages.P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_FREE_TOPIC_CONNECTION_FACTORY,
				Messages.P_JMS_FREE_TOPIC_CONNECTION_FACTORY, c0));

		// external topics
		TabItem tabJmsExt = new TabItem(tabs, SWT.NONE);
		tabJmsExt.setText(Messages.P_JMS_EXT);
		c0 = new Composite(tabs,SWT.NONE);
		c0.setLayout(new GridLayout(1,false));
		c0.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		tabJmsExt.setControl(c0);

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);
		
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXT_TOPIC_ALARM,
				Messages.P_JMS_EXT_TOPIC_ALARM, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXT_TSUB_ALARM_FMR,
				Messages.P_JMS_EXT_TSUB_ALARM_FMR, c0));

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXT_TOPIC_COMMAND,
				Messages.P_JMS_EXT_TOPIC_COMMAND, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD,
				Messages.P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD, c0));

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_EXT_TOPIC_STATUSCHANGE,
				Messages.P_JMS_EXT_TOPIC_STATUSCHANGE, c0));

		// ams internal topics
		BooleanFieldEditor bfe = null;
		TabItem tabJmsAms = new TabItem(tabs, SWT.NONE);
		tabJmsAms.setText(Messages.P_JMS_AMS);
		c0 = new Composite(tabs,SWT.NONE);
		c0.setLayout(new GridLayout(1,false));
		c0.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		tabJmsAms.setControl(c0);

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR,
				Messages.P_JMS_AMS_TOPIC_DISTRIBUTOR, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_DISTRIBUTOR,
				Messages.P_JMS_AMS_TSUB_DISTRIBUTOR, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY,
				Messages.P_JMS_AMS_TOPIC_REPLY, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_REPLY,
				Messages.P_JMS_AMS_TSUB_REPLY, c0));
        addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_MESSAGEMINDER,
                Messages.P_JMS_AMS_TOPIC_MESSAGEMINDER, c0));
        addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_MESSAGEMINDER,
                Messages.P_JMS_AMS_TSUB_MESSAGEMINDER, c0));

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR,
				Messages.P_JMS_AMS_TOPIC_SMS_CONNECTOR, c0));        
		bfe = new BooleanFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR_FORWARD,
                Messages.P_JMS_AMS_TOPIC_FORWARD, c0);
		bfe.setEnabled(false, c0);
		addField(bfe);
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR,
				Messages.P_JMS_AMS_TSUB_SMS_CONNECTOR, c0));
		
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR,
				Messages.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR, c0));
		bfe = new BooleanFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_FORWARD,
                Messages.P_JMS_AMS_TOPIC_FORWARD, c0);
		bfe.setEnabled(false, c0);
        addField(bfe);
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_EMAIL_CONNECTOR,
				Messages.P_JMS_AMS_TSUB_EMAIL_CONNECTOR, c0));
		
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR,
				Messages.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR, c0));
		bfe = new BooleanFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_FORWARD,
                Messages.P_JMS_AMS_TOPIC_FORWARD, c0);
		bfe.setEnabled(false, c0);
        addField(bfe);
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR,
				Messages.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR, c0));
		
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR,
				Messages.P_JMS_AMS_TOPIC_JMS_CONNECTOR, c0));
		bfe = new BooleanFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR_FORWARD,
                Messages.P_JMS_AMS_TOPIC_FORWARD, c0);
        addField(bfe);
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_JMS_CONNECTOR,
				Messages.P_JMS_AMS_TSUB_JMS_CONNECTOR, c0));

		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_COMMAND,
				Messages.P_JMS_AMS_TOPIC_COMMAND, c0));
		addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END,
				Messages.P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END, c0));
		
		new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

	    addField(new StringFieldEditor(AmsPreferenceKey.P_JMS_AMS_TOPIC_MONITOR,
	            Messages.P_JMS_AMS_TOPIC_MONITOR, c0));

	    new Label(c0, SWT.NONE);new Label(c0, SWT.NONE);

		adjustGridLayout(); 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final void init(final IWorkbench workbench) {
		/*
		 * This methdos does not necessarily need to do anything. But it must be
		 * implemented anyway.
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IPreferenceStore doGetPreferenceStore() {
		return AmsActivator.getDefault().getPreferenceStore();
	}
}