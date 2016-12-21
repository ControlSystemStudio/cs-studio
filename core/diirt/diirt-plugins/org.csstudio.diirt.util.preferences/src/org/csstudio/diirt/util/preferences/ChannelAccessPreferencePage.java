/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_ADDR_LIST;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_AUTO_ADDR_LIST;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_BEACON_PERIOD;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_CONNECTION_TIMEOUT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_CUSTOM_MASK;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_HONOR_ZERO_PRECISION;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_MAX_ARRAY_SIZE;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_MONITOR_MASK;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_PURE_JAVA;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_REPEATER_PORT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_SERVER_PORT;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_VALUE_RTYP_MONITOR;
import static org.csstudio.diirt.util.core.preferences.pojo.ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY;

import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.csstudio.diirt.util.preferences.jface.DoubleFieldEditor;
import org.csstudio.diirt.util.preferences.swt.Separator;
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

import gov.aps.jca.Monitor;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
public class ChannelAccessPreferencePage extends BasePreferencePage {

    public static final String[][] AVAILABLE_MODES              = {
        { Messages.CAPP_pureJavaRadioButton_text, Boolean.TRUE.toString()  },
        { Messages.CAPP_jcaRadioButton_text,      Boolean.FALSE.toString() },
    };
    public static final String[][] AVAILABLE_MONITOR_MASKS      = {
        { Messages.CAPP_valueRadioButton_text,   MonitorMask.VALUE.name()   },
        { Messages.CAPP_archiveRadioButton_text, MonitorMask.ARCHIVE.name() },
        { Messages.CAPP_alarmRadioButton_text,   MonitorMask.ALARM.name()   },
        { Messages.CAPP_customRadioButton_text,  MonitorMask.CUSTOM.name()  },
    };
    public static final String[][] AVAILABLE_VAR_ARRAY_SUPPORTS = {
        { Messages.CAPP_autoRadioButton_text,  VariableArraySupport.AUTO.representation()  },
        { Messages.CAPP_trueRadioButton_text,  VariableArraySupport.TRUE.representation()  },
        { Messages.CAPP_falseRadioButton_text, VariableArraySupport.FALSE.representation() },
    };

    private Group                 contextGroup;
    private StringFieldEditor     addressListEditor;
    private BooleanFieldEditor    autoAddressListEditor;
    private DoubleFieldEditor     beaconPeriodEditor;
    private DoubleFieldEditor     connectionTimeoutEditor;
    private IntegerFieldEditor    customMaskEditor;
    private IntegerFieldEditor    maxArraySizeEditor;
    private RadioGroupFieldEditor pureJavaEditor;
    private IntegerFieldEditor    repeaterPortEditor;
    private IntegerFieldEditor    serverPortEditor;
    private Group                 optionsGroup;
    private RadioGroupFieldEditor monitorMaskEditor;
    private BooleanFieldEditor    dbePropertySupportedEditor;
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

        pureJavaEditor = new RadioGroupFieldEditor(PREF_PURE_JAVA, Messages.CAPP_modeCaption_text, 2, AVAILABLE_MODES, contextGroup, false);

        addField(pureJavaEditor, contextGroup, true, () -> store.getDefaultString(PREF_PURE_JAVA), () -> store.getString(PREF_PURE_JAVA));

        addressListEditor = new StringFieldEditor(PREF_ADDR_LIST, Messages.CAPP_addressListCaption_text, contextGroup);

        addField(addressListEditor, contextGroup, true, () -> store.getDefaultString(PREF_ADDR_LIST), () -> store.getString(PREF_ADDR_LIST));

        autoAddressListEditor = new BooleanFieldEditor(PREF_AUTO_ADDR_LIST, Messages.CAPP_autoCheckButton_text, BooleanFieldEditor.SEPARATE_LABEL, contextGroup);

        addField(autoAddressListEditor, contextGroup, true, () -> store.getDefaultBoolean(PREF_AUTO_ADDR_LIST), () -> store.getBoolean(PREF_AUTO_ADDR_LIST));

        new Separator(contextGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        connectionTimeoutEditor = new DoubleFieldEditor(PREF_CONNECTION_TIMEOUT, Messages.CAPP_connectionTimeoutCaption_text, contextGroup);

        connectionTimeoutEditor.setValidRange(0, 300);
        connectionTimeoutEditor.setTextLimit(32);
        connectionTimeoutEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(connectionTimeoutEditor, contextGroup, true, () -> store.getDefaultString(PREF_CONNECTION_TIMEOUT), () -> store.getString(PREF_CONNECTION_TIMEOUT));

        beaconPeriodEditor = new DoubleFieldEditor(PREF_BEACON_PERIOD, Messages.CAPP_beaconPeriodCaption_text, contextGroup);

        beaconPeriodEditor.setValidRange(0, 300);
        beaconPeriodEditor.setTextLimit(32);
        beaconPeriodEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(beaconPeriodEditor, contextGroup, true, () -> store.getDefaultString(PREF_BEACON_PERIOD), () -> store.getString(PREF_BEACON_PERIOD));

        new Separator(contextGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        repeaterPortEditor = new IntegerFieldEditor(PREF_REPEATER_PORT, Messages.CAPP_repeaterPortCaption_text, contextGroup);

        repeaterPortEditor.setValidRange(1024, 65535);
        repeaterPortEditor.setTextLimit(32);
        repeaterPortEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(repeaterPortEditor, contextGroup, true, () -> store.getDefaultString(PREF_REPEATER_PORT), () -> store.getString(PREF_REPEATER_PORT));

        serverPortEditor = new IntegerFieldEditor(PREF_SERVER_PORT, Messages.CAPP_serverPortCaption_text, contextGroup);

        serverPortEditor.setValidRange(1024, 65535);
        serverPortEditor.setTextLimit(32);
        serverPortEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(serverPortEditor, contextGroup, true, () -> store.getDefaultString(PREF_SERVER_PORT), () -> store.getString(PREF_SERVER_PORT));

        new Separator(contextGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        maxArraySizeEditor = new IntegerFieldEditor(PREF_MAX_ARRAY_SIZE, Messages.CAPP_maxArraySizeSpinnerCaption_text, contextGroup);

        maxArraySizeEditor.setValidRange(1024, 524288);
        maxArraySizeEditor.setTextLimit(32);
        maxArraySizeEditor.getTextControl(contextGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(maxArraySizeEditor, contextGroup, true, () -> store.getDefaultString(PREF_MAX_ARRAY_SIZE), () -> store.getString(PREF_MAX_ARRAY_SIZE));

        optionsGroup = new Group(container, SWT.NONE);

        optionsGroup.setText(Messages.CAPP_optionsGroup_text);
        optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        GridLayout optionsGroupLayout = new GridLayout();

        optionsGroup.setLayout(optionsGroupLayout);

        monitorMaskEditor = new RadioGroupFieldEditor(PREF_MONITOR_MASK, Messages.CAPP_monitorMaskCaption_text, 4, AVAILABLE_MONITOR_MASKS, optionsGroup, false);

        addField(
            monitorMaskEditor,
            optionsGroup,
            true,
            () -> store.getDefaultString(PREF_MONITOR_MASK),
            () -> store.getString(PREF_MONITOR_MASK),
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

        customMaskEditor = new IntegerFieldEditor(PREF_CUSTOM_MASK, "", optionsGroup);

        customMaskEditor.setEnabled(MonitorMask.CUSTOM.name().equals(store.getString(PREF_MONITOR_MASK)), optionsGroup);
        customMaskEditor.setValidRange(0, 65536);
        customMaskEditor.setTextLimit(32);
        customMaskEditor.getTextControl(optionsGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(customMaskEditor, optionsGroup, true, () -> store.getDefaultString(PREF_CUSTOM_MASK), () -> store.getString(PREF_CUSTOM_MASK));

        new Separator(optionsGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        variableArrayEditor = new RadioGroupFieldEditor(PREF_VARIABLE_LENGTH_ARRAY, Messages.CAPP_variableLengthArrayCaption_text, 3, AVAILABLE_VAR_ARRAY_SUPPORTS, optionsGroup, false);

        addField(variableArrayEditor, optionsGroup, true, () -> store.getDefaultString(PREF_VARIABLE_LENGTH_ARRAY), () -> store.getString(PREF_VARIABLE_LENGTH_ARRAY));

        new Separator(optionsGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        dbePropertySupportedEditor = new BooleanFieldEditor(PREF_DBE_PROPERTY_SUPPORTED, Messages.CAPP_dbePropertySupportedCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsGroup);

        addField(dbePropertySupportedEditor, optionsGroup, true, () -> store.getDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED), () -> store.getBoolean(PREF_DBE_PROPERTY_SUPPORTED));

        honorZeropRecisionEditor = new BooleanFieldEditor(PREF_HONOR_ZERO_PRECISION, Messages.CAPP_honorCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsGroup);

        addField(honorZeropRecisionEditor, optionsGroup, true, () -> store.getDefaultBoolean(PREF_HONOR_ZERO_PRECISION), () -> store.getBoolean(PREF_HONOR_ZERO_PRECISION));

        valueRTYPMonitorEditor = new BooleanFieldEditor(PREF_VALUE_RTYP_MONITOR, Messages.CAPP_valueOnlyCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsGroup);

        addField(valueRTYPMonitorEditor, optionsGroup, true, () -> store.getDefaultBoolean(PREF_VALUE_RTYP_MONITOR), () -> store.getBoolean(PREF_VALUE_RTYP_MONITOR));

        initializeValues(store);

        return container;

    }

    @Override
    protected void initializeCancelStore ( IPreferenceStore store, IPreferenceStore cancelStore ) {

        cancelStore.setDefault(PREF_DBE_PROPERTY_SUPPORTED, store.getDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        cancelStore.setDefault(PREF_HONOR_ZERO_PRECISION,   store.getDefaultBoolean(PREF_HONOR_ZERO_PRECISION));
        cancelStore.setDefault(PREF_MONITOR_MASK,           store.getDefaultString(PREF_MONITOR_MASK));
        cancelStore.setDefault(PREF_CUSTOM_MASK,            store.getDefaultInt(PREF_CUSTOM_MASK));
        cancelStore.setDefault(PREF_VALUE_RTYP_MONITOR,     store.getDefaultBoolean(PREF_VALUE_RTYP_MONITOR));
        cancelStore.setDefault(PREF_VARIABLE_LENGTH_ARRAY,  store.getDefaultString(PREF_VARIABLE_LENGTH_ARRAY));
        cancelStore.setDefault(PREF_ADDR_LIST,              store.getDefaultString(PREF_ADDR_LIST));
        cancelStore.setDefault(PREF_AUTO_ADDR_LIST,         store.getDefaultBoolean(PREF_AUTO_ADDR_LIST));
        cancelStore.setDefault(PREF_BEACON_PERIOD,          store.getDefaultDouble(PREF_BEACON_PERIOD));
        cancelStore.setDefault(PREF_CONNECTION_TIMEOUT,     store.getDefaultDouble(PREF_CONNECTION_TIMEOUT));
        cancelStore.setDefault(PREF_MAX_ARRAY_SIZE,         store.getDefaultInt(PREF_MAX_ARRAY_SIZE));
        cancelStore.setDefault(PREF_PURE_JAVA,              store.getDefaultBoolean(PREF_PURE_JAVA));
        cancelStore.setDefault(PREF_REPEATER_PORT,          store.getDefaultInt(PREF_REPEATER_PORT));
        cancelStore.setDefault(PREF_SERVER_PORT,            store.getDefaultInt(PREF_SERVER_PORT));

        cancelStore.setValue(PREF_DBE_PROPERTY_SUPPORTED, store.getBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        cancelStore.setValue(PREF_HONOR_ZERO_PRECISION,   store.getBoolean(PREF_HONOR_ZERO_PRECISION));
        cancelStore.setValue(PREF_MONITOR_MASK,           store.getString(PREF_MONITOR_MASK));
        cancelStore.setValue(PREF_CUSTOM_MASK,            store.getInt(PREF_CUSTOM_MASK));
        cancelStore.setValue(PREF_VALUE_RTYP_MONITOR,     store.getBoolean(PREF_VALUE_RTYP_MONITOR));
        cancelStore.setValue(PREF_VARIABLE_LENGTH_ARRAY,  store.getString(PREF_VARIABLE_LENGTH_ARRAY));
        cancelStore.setValue(PREF_ADDR_LIST,              store.getString(PREF_ADDR_LIST));
        cancelStore.setValue(PREF_AUTO_ADDR_LIST,         store.getBoolean(PREF_AUTO_ADDR_LIST));
        cancelStore.setValue(PREF_BEACON_PERIOD,          store.getDouble(PREF_BEACON_PERIOD));
        cancelStore.setValue(PREF_CONNECTION_TIMEOUT,     store.getDouble(PREF_CONNECTION_TIMEOUT));
        cancelStore.setValue(PREF_MAX_ARRAY_SIZE,         store.getInt(PREF_MAX_ARRAY_SIZE));
        cancelStore.setValue(PREF_PURE_JAVA,              store.getBoolean(PREF_PURE_JAVA));
        cancelStore.setValue(PREF_REPEATER_PORT,          store.getInt(PREF_REPEATER_PORT));
        cancelStore.setValue(PREF_SERVER_PORT,            store.getInt(PREF_SERVER_PORT));

    }

    @Override
    protected void performCancel ( IPreferenceStore store, IPreferenceStore cancelStore ) {

        store.setDefault(PREF_DBE_PROPERTY_SUPPORTED, cancelStore.getDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        store.setDefault(PREF_HONOR_ZERO_PRECISION,   cancelStore.getDefaultBoolean(PREF_HONOR_ZERO_PRECISION));
        store.setDefault(PREF_MONITOR_MASK,           cancelStore.getDefaultString(PREF_MONITOR_MASK));
        store.setDefault(PREF_CUSTOM_MASK,            cancelStore.getDefaultInt(PREF_CUSTOM_MASK));
        store.setDefault(PREF_VALUE_RTYP_MONITOR,     cancelStore.getDefaultBoolean(PREF_VALUE_RTYP_MONITOR));
        store.setDefault(PREF_VARIABLE_LENGTH_ARRAY,  cancelStore.getDefaultString(PREF_VARIABLE_LENGTH_ARRAY));
        store.setDefault(PREF_ADDR_LIST,              cancelStore.getDefaultString(PREF_ADDR_LIST));
        store.setDefault(PREF_AUTO_ADDR_LIST,         cancelStore.getDefaultBoolean(PREF_AUTO_ADDR_LIST));
        store.setDefault(PREF_BEACON_PERIOD,          cancelStore.getDefaultDouble(PREF_BEACON_PERIOD));
        store.setDefault(PREF_CONNECTION_TIMEOUT,     cancelStore.getDefaultDouble(PREF_CONNECTION_TIMEOUT));
        store.setDefault(PREF_MAX_ARRAY_SIZE,         cancelStore.getDefaultInt(PREF_MAX_ARRAY_SIZE));
        store.setDefault(PREF_PURE_JAVA,              cancelStore.getDefaultBoolean(PREF_PURE_JAVA));
        store.setDefault(PREF_REPEATER_PORT,          cancelStore.getDefaultInt(PREF_REPEATER_PORT));
        store.setDefault(PREF_SERVER_PORT,            cancelStore.getDefaultInt(PREF_SERVER_PORT));

        store.setValue(PREF_DBE_PROPERTY_SUPPORTED, cancelStore.getBoolean(PREF_DBE_PROPERTY_SUPPORTED));
        store.setValue(PREF_HONOR_ZERO_PRECISION,   cancelStore.getBoolean(PREF_HONOR_ZERO_PRECISION));
        store.setValue(PREF_MONITOR_MASK,           cancelStore.getString(PREF_MONITOR_MASK));
        store.setValue(PREF_CUSTOM_MASK,            cancelStore.getInt(PREF_CUSTOM_MASK));
        store.setValue(PREF_VALUE_RTYP_MONITOR,     cancelStore.getBoolean(PREF_VALUE_RTYP_MONITOR));
        store.setValue(PREF_VARIABLE_LENGTH_ARRAY,  cancelStore.getString(PREF_VARIABLE_LENGTH_ARRAY));
        store.setValue(PREF_ADDR_LIST,              cancelStore.getString(PREF_ADDR_LIST));
        store.setValue(PREF_AUTO_ADDR_LIST,         cancelStore.getBoolean(PREF_AUTO_ADDR_LIST));
        store.setValue(PREF_BEACON_PERIOD,          cancelStore.getDouble(PREF_BEACON_PERIOD));
        store.setValue(PREF_CONNECTION_TIMEOUT,     cancelStore.getDouble(PREF_CONNECTION_TIMEOUT));
        store.setValue(PREF_MAX_ARRAY_SIZE,         cancelStore.getInt(PREF_MAX_ARRAY_SIZE));
        store.setValue(PREF_PURE_JAVA,              cancelStore.getBoolean(PREF_PURE_JAVA));
        store.setValue(PREF_REPEATER_PORT,          cancelStore.getInt(PREF_REPEATER_PORT));
        store.setValue(PREF_SERVER_PORT,            cancelStore.getInt(PREF_SERVER_PORT));

    }

}
