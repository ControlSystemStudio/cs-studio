
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

package org.csstudio.ams.connector.sms.internal.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.csstudio.ams.PasswordEditor;
import org.csstudio.ams.connector.sms.Messages;
import org.csstudio.ams.connector.sms.SmsConnectorPlugin;
import org.csstudio.ams.connector.sms.internal.SmsConnectorPreferenceKey;

/**
 * A preference page that contains the following types of preferences:<br>
 * </p>
 * <ul>
 * </ul>
 * 
 * @author Alexander Will
 * 
 */
public class SmsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Standard constructor.
	 */
	public SmsPreferencePage() {
		/*
		 * The usage of the style FieldEditorPreferencePage.GRID makes this
		 * preference page have a grid layout.
		 */
		super(FieldEditorPreferencePage.GRID);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unused")
    @Override
	protected final void createFieldEditors() {

        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM_READ_WAITING_PERIOD,
                Messages.P_MODEM_READ_WAITING_PERIOD, getFieldEditorParent()));

        new Label(getFieldEditorParent(), SWT.NONE);new Label(getFieldEditorParent(), SWT.NONE);

	    addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM_COUNT,
	            Messages.P_MODEM_COUNT, getFieldEditorParent()));

        new Label(getFieldEditorParent(), SWT.NONE);new Label(getFieldEditorParent(), SWT.NONE);

        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM1_COMPORT,
				Messages.P_MODEM1_COMPORT, getFieldEditorParent()));
		addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM1_COMBAUDRATE,
				Messages.P_MODEM1_COMBAUDRATE, getFieldEditorParent()));
		addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM1_MANUFACTURE,
				Messages.P_MODEM1_MANUFACTURE, getFieldEditorParent()));
		addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM1_MODEL,
				Messages.P_MODEM1_MODEL, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM1_NUMBER,
                Messages.P_MODEM1_NUMBER, getFieldEditorParent()));
		addField(new PasswordEditor(SmsConnectorPreferenceKey.P_MODEM1_SIMPIM,
				Messages.P_MODEM1_SIMPIM, getFieldEditorParent()));

		new Label(getFieldEditorParent(), SWT.NONE);new Label(getFieldEditorParent(), SWT.NONE);
		
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM2_COMPORT,
                Messages.P_MODEM2_COMPORT, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM2_COMBAUDRATE,
                Messages.P_MODEM2_COMBAUDRATE, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM2_MANUFACTURE,
                Messages.P_MODEM2_MANUFACTURE, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM2_MODEL,
                Messages.P_MODEM2_MODEL, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM2_NUMBER,
                Messages.P_MODEM2_NUMBER, getFieldEditorParent()));
        addField(new PasswordEditor(SmsConnectorPreferenceKey.P_MODEM2_SIMPIM,
                Messages.P_MODEM2_SIMPIM, getFieldEditorParent()));

        new Label(getFieldEditorParent(), SWT.NONE);new Label(getFieldEditorParent(), SWT.NONE);

        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM3_COMPORT,
                Messages.P_MODEM3_COMPORT, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM3_COMBAUDRATE,
                Messages.P_MODEM3_COMBAUDRATE, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM3_MANUFACTURE,
                Messages.P_MODEM3_MANUFACTURE, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM3_MODEL,
                Messages.P_MODEM3_MODEL, getFieldEditorParent()));
        addField(new StringFieldEditor(SmsConnectorPreferenceKey.P_MODEM3_NUMBER,
                Messages.P_MODEM3_NUMBER, getFieldEditorParent()));
        addField(new PasswordEditor(SmsConnectorPreferenceKey.P_MODEM3_SIMPIM,
                Messages.P_MODEM3_SIMPIM, getFieldEditorParent()));

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
		return SmsConnectorPlugin.getDefault().getPreferenceStore();
	}

}
