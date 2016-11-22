/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;

import org.csstudio.diirt.util.preferences.pojo.JCAContext.MonitorMask;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;

import gov.aps.jca.Monitor;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 *
 */
public class ChannelAccessPreferencePage extends BasePreferencePage {

    private Group                 contextGroup;
    private StringFieldEditor     addressListEditor;
    private BooleanFieldEditor    autoAddressListEditor;
    private IntegerFieldEditor    beaconPeriodEditor;
    private IntegerFieldEditor    connectionTimeoutEditor;
    private IntegerFieldEditor    customMaskEditor;
    private IntegerFieldEditor    maxArraySizeEditor;
    private RadioGroupFieldEditor pureJavaEditor;
    private IntegerFieldEditor    repeaterPortEditor;
    private IntegerFieldEditor    serverPortEditor;
    private Group                 optionsGroup;
    private RadioGroupFieldEditor monitorMaskEditor;
    private BooleanFieldEditor    sbePropertySupportedEditor;
    private BooleanFieldEditor    honorZeropRecisionEditor;
    private BooleanFieldEditor    valueRTYPMonitorEditor;
    private RadioGroupFieldEditor variableArrayEditor;

    /**
     * Create the preference page.
     */
    public ChannelAccessPreferencePage ( ) {
        setDescription(Messages.CAPP_description);
        setTitle(Messages.CAPP_title);
    }

    /**
     * Create contents of the preference page.
     * @param parent
     */
    @Override
    public Control createContents ( Composite parent ) {

        IPreferenceStore store = getPreferenceStore();
        Composite container = new Composite(parent, SWT.NULL);

        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout());

        contextGroup = new Group(container, SWT.NONE);

        contextGroup.setText(Messages.CAPP_contextGroup_text);
        contextGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        GridLayout contextGroupLayout = new GridLayout();

        contextGroup.setLayout(contextGroupLayout);

        pureJavaEditor = new RadioGroupFieldEditor(DIIRTPreferencesPlugin.PREF_CA_PURE_JAVA, Messages.CAPP_modeCaption_text, 2, DIIRTPreferencesPlugin.AVAILABLE_MODES, contextGroup, false);

        addField(
            pureJavaEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_PURE_JAVA),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_PURE_JAVA)
        );

        addressListEditor = new StringFieldEditor(DIIRTPreferencesPlugin.PREF_CA_ADDR_LIST, Messages.CAPP_addressListCaption_text, contextGroup);

        addField(
            addressListEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_ADDR_LIST),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_ADDR_LIST)
        );

        autoAddressListEditor = new BooleanFieldEditor(DIIRTPreferencesPlugin.PREF_CA_AUTO_ADDR_LIST, Messages.CAPP_autoCheckButton_text, BooleanFieldEditor.SEPARATE_LABEL, contextGroup);

        addField(
            autoAddressListEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_AUTO_ADDR_LIST),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_AUTO_ADDR_LIST)
        );

        new Label(contextGroup, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        connectionTimeoutEditor = new IntegerFieldEditor(DIIRTPreferencesPlugin.PREF_CA_CONNECTION_TIMEOUT, Messages.CAPP_connectionTimeoutCaption_text, contextGroup);

        connectionTimeoutEditor.setValidRange(0, 300);
        connectionTimeoutEditor.setTextLimit(32);
        connectionTimeoutEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());
        addField(
            connectionTimeoutEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_CONNECTION_TIMEOUT),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_CONNECTION_TIMEOUT)
        );

        beaconPeriodEditor = new IntegerFieldEditor(DIIRTPreferencesPlugin.PREF_CA_BEACON_PERIOD, Messages.CAPP_beaconPeriodCaption_text, contextGroup);

        beaconPeriodEditor.setValidRange(0, 300);
        beaconPeriodEditor.setTextLimit(32);
        beaconPeriodEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());
        addField(
            beaconPeriodEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_BEACON_PERIOD),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_BEACON_PERIOD)
        );

        new Label(contextGroup, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        repeaterPortEditor = new IntegerFieldEditor(DIIRTPreferencesPlugin.PREF_CA_REPEATER_PORT, Messages.CAPP_repeaterPortCaption_text, contextGroup);

        repeaterPortEditor.setValidRange(1024, 65535);
        repeaterPortEditor.setTextLimit(32);
        repeaterPortEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());
        addField(
            repeaterPortEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_REPEATER_PORT),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_REPEATER_PORT)
        );

        serverPortEditor = new IntegerFieldEditor(DIIRTPreferencesPlugin.PREF_CA_SERVER_PORT, Messages.CAPP_serverPortCaption_text, contextGroup);

        serverPortEditor.setValidRange(1024, 65535);
        serverPortEditor.setTextLimit(32);
        serverPortEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());
        addField(
            serverPortEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_SERVER_PORT),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_SERVER_PORT)
        );

        new Label(contextGroup, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        maxArraySizeEditor = new IntegerFieldEditor(DIIRTPreferencesPlugin.PREF_CA_MAX_ARRAY_SIZE, Messages.CAPP_maxArraySizeSpinnerCaption_text, contextGroup);

        maxArraySizeEditor.setValidRange(1024, 524288);
        maxArraySizeEditor.setTextLimit(32);
        maxArraySizeEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());
        addField(
            maxArraySizeEditor,
            contextGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_MAX_ARRAY_SIZE),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_MAX_ARRAY_SIZE)
        );

        optionsGroup = new Group(container, SWT.NONE);

        optionsGroup.setText(Messages.CAPP_optionsGroup_text);
        optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        GridLayout optionsGroupLayout = new GridLayout();

        optionsGroup.setLayout(optionsGroupLayout);

        monitorMaskEditor = new RadioGroupFieldEditor(DIIRTPreferencesPlugin.PREF_CA_MONITOR_MASK, Messages.CAPP_monitorMaskCaption_text, 4, DIIRTPreferencesPlugin.AVAILABLE_MONITOR_MASKS, optionsGroup, false);

        addField(
            monitorMaskEditor,
            optionsGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_MONITOR_MASK),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_MONITOR_MASK),
            e -> {

                customMaskEditor.setEnabled(MonitorMask.CUSTOM.name().equals(e.getNewValue()), optionsGroup);

                if ( MonitorMask.VALUE.name().equals(e.getNewValue()) ) {
                    customMaskEditor.getTextControl(optionsGroup).setText(Integer.toString(Monitor.VALUE | Monitor.ALARM));
                } else if ( MonitorMask.ARCHIVE.name().equals(e.getNewValue()) ) {
                    customMaskEditor.getTextControl(optionsGroup).setText(Integer.toString(Monitor.LOG));
                } else if ( MonitorMask.ALARM.name().equals(e.getNewValue()) ) {
                    customMaskEditor.getTextControl(optionsGroup).setText(Integer.toString(Monitor.ALARM));
                }

            }
        );

        customMaskEditor = new IntegerFieldEditor(DIIRTPreferencesPlugin.PREF_CA_CUSTOM_MASK, "", optionsGroup);

        customMaskEditor.setEnabled(MonitorMask.CUSTOM.name().equals(store.getString(DIIRTPreferencesPlugin.PREF_CA_MONITOR_MASK)), optionsGroup);
        customMaskEditor.setValidRange(0, 65536);
        customMaskEditor.setTextLimit(32);
        customMaskEditor.getTextControl(optionsGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(
            customMaskEditor,
            optionsGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_CUSTOM_MASK),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_CUSTOM_MASK)
        );

        new Label(optionsGroup, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        variableArrayEditor = new RadioGroupFieldEditor(DIIRTPreferencesPlugin.PREF_CA_VARIABLE_LENGTH_ARRAY, Messages.CAPP_variableLengthArrayCaption_text, 3, DIIRTPreferencesPlugin.AVAILABLE_VAR_ARRAY_SUPPORTS, optionsGroup, false);

        addField(
            variableArrayEditor,
            optionsGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_VARIABLE_LENGTH_ARRAY),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_VARIABLE_LENGTH_ARRAY)
        );

        new Label(optionsGroup, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        sbePropertySupportedEditor = new BooleanFieldEditor(DIIRTPreferencesPlugin.PREF_CA_DBE_PROPERTY_SUPPORTED, Messages.CAPP_dbePropertySupportedCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsGroup);

        addField(
            sbePropertySupportedEditor,
            optionsGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_DBE_PROPERTY_SUPPORTED),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_DBE_PROPERTY_SUPPORTED)
        );

        honorZeropRecisionEditor = new BooleanFieldEditor(DIIRTPreferencesPlugin.PREF_CA_HONOR_ZERO_PRECISION, Messages.CAPP_honorCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsGroup);

        addField(
            honorZeropRecisionEditor,
            optionsGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_HONOR_ZERO_PRECISION),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_HONOR_ZERO_PRECISION)
        );

        valueRTYPMonitorEditor = new BooleanFieldEditor(DIIRTPreferencesPlugin.PREF_CA_VALUE_RTYP_MONITOR, Messages.CAPP_valueOnlyCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsGroup);

        addField(
            valueRTYPMonitorEditor,
            optionsGroup,
            true,
            () -> store.getDefaultString(DIIRTPreferencesPlugin.PREF_CA_VALUE_RTYP_MONITOR),
            () -> store.getString(DIIRTPreferencesPlugin.PREF_CA_VALUE_RTYP_MONITOR)
        );

        return container;

    }

    /**
     * Initialize the preference page.
     */
    @Override
    public void init ( IWorkbench workbench ) {
        // Initialize the preference page
    }

}
