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


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
public class ChannelAccessPreferencePage extends BasePreferencePage {

    //  JCA no more available since DIIRT 3.1.7 ---------------------------
    //public static final String[][] AVAILABLE_MODES              = {
    //    { Messages.CAPP_pureJavaRadioButton_text, Boolean.TRUE.toString()  },
    //    { Messages.CAPP_jcaRadioButton_text,      Boolean.FALSE.toString() },
    //};
    //  -------------------------------------------------------------------
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

    private Composite             contextInnerGroup;
    private Group                 contextOuterGroup;
    private StringFieldEditor     addressListEditor;
    private BooleanFieldEditor    autoAddressListEditor;
    private DoubleFieldEditor     beaconPeriodEditor;
    private DoubleFieldEditor     connectionTimeoutEditor;
    private IntegerFieldEditor    customMaskEditor;
    private IntegerFieldEditor    maxArraySizeEditor;
    //  JCA no more available since DIIRT 3.1.7 ---------------------------
    //private RadioGroupFieldEditor pureJavaEditor;
    //  -------------------------------------------------------------------
    private IntegerFieldEditor    repeaterPortEditor;
    private IntegerFieldEditor    serverPortEditor;
    private Composite             optionsInnerGroup;
    private Group                 optionsOuterGroup;
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

        contextOuterGroup = new Group(container, SWT.NONE);

        contextOuterGroup.setText(Messages.CAPP_contextGroup_text);
        contextOuterGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        contextOuterGroup.setLayout(new GridLayout());

        contextInnerGroup = new Composite(contextOuterGroup, SWT.DOUBLE_BUFFERED);

        contextInnerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        contextInnerGroup.setLayout(new GridLayout());

        //  JCA no more available since DIIRT 3.1.7 ---------------------------
        //pureJavaEditor = new RadioGroupFieldEditor(PREF_PURE_JAVA, Messages.CAPP_modeCaption_text, 2, AVAILABLE_MODES, contextInnerGroup, false);
        //
        //addField(pureJavaEditor, contextInnerGroup, true, () -> store.getDefaultString(PREF_PURE_JAVA), () -> store.getString(PREF_PURE_JAVA));
        //  -------------------------------------------------------------------

        addressListEditor = new StringFieldEditor(PREF_ADDR_LIST, Messages.CAPP_addressListCaption_text, contextInnerGroup);

        addField(addressListEditor, contextInnerGroup, true, () -> store.getDefaultString(PREF_ADDR_LIST), () -> store.getString(PREF_ADDR_LIST));

        autoAddressListEditor = new BooleanFieldEditor(PREF_AUTO_ADDR_LIST, Messages.CAPP_autoCheckButton_text, BooleanFieldEditor.SEPARATE_LABEL, contextInnerGroup);

        addField(autoAddressListEditor, contextInnerGroup, true, () -> store.getDefaultBoolean(PREF_AUTO_ADDR_LIST), () -> store.getBoolean(PREF_AUTO_ADDR_LIST));

        new Separator(contextInnerGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        connectionTimeoutEditor = new DoubleFieldEditor(PREF_CONNECTION_TIMEOUT, Messages.CAPP_connectionTimeoutCaption_text, contextInnerGroup);

        connectionTimeoutEditor.setValidRange(0, 300);
        connectionTimeoutEditor.setTextLimit(32);
        connectionTimeoutEditor.getTextControl(contextInnerGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(connectionTimeoutEditor, contextInnerGroup, true, () -> store.getDefaultString(PREF_CONNECTION_TIMEOUT), () -> store.getString(PREF_CONNECTION_TIMEOUT));

        beaconPeriodEditor = new DoubleFieldEditor(PREF_BEACON_PERIOD, Messages.CAPP_beaconPeriodCaption_text, contextInnerGroup);

        beaconPeriodEditor.setValidRange(0, 300);
        beaconPeriodEditor.setTextLimit(32);
        beaconPeriodEditor.getTextControl(contextInnerGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(beaconPeriodEditor, contextInnerGroup, true, () -> store.getDefaultString(PREF_BEACON_PERIOD), () -> store.getString(PREF_BEACON_PERIOD));

        new Separator(contextInnerGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        serverPortEditor = new IntegerFieldEditor(PREF_SERVER_PORT, Messages.CAPP_serverPortCaption_text, contextInnerGroup);

        serverPortEditor.setValidRange(1024, 65535);
        serverPortEditor.setTextLimit(32);
        serverPortEditor.getTextControl(contextInnerGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(serverPortEditor, contextInnerGroup, true, () -> store.getDefaultString(PREF_SERVER_PORT), () -> store.getString(PREF_SERVER_PORT));

        repeaterPortEditor = new IntegerFieldEditor(PREF_REPEATER_PORT, Messages.CAPP_repeaterPortCaption_text, contextInnerGroup);

        repeaterPortEditor.setValidRange(1024, 65535);
        repeaterPortEditor.setTextLimit(32);
        repeaterPortEditor.getTextControl(contextInnerGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(repeaterPortEditor, contextInnerGroup, true, () -> store.getDefaultString(PREF_REPEATER_PORT), () -> store.getString(PREF_REPEATER_PORT));

        new Separator(contextInnerGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        maxArraySizeEditor = new IntegerFieldEditor(PREF_MAX_ARRAY_SIZE, Messages.CAPP_maxArraySizeSpinnerCaption_text, contextInnerGroup);

        maxArraySizeEditor.setValidRange(16384, Integer.MAX_VALUE);
        maxArraySizeEditor.setTextLimit(32);
        maxArraySizeEditor.getTextControl(contextInnerGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(maxArraySizeEditor, contextInnerGroup, true, () -> store.getDefaultString(PREF_MAX_ARRAY_SIZE), () -> store.getString(PREF_MAX_ARRAY_SIZE));

        optionsOuterGroup = new Group(container, SWT.NONE);

        optionsOuterGroup.setText(Messages.CAPP_optionsGroup_text);
        optionsOuterGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        optionsOuterGroup.setLayout(new GridLayout());

        optionsInnerGroup = new Composite(optionsOuterGroup, SWT.DOUBLE_BUFFERED);

        optionsInnerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        optionsInnerGroup.setLayout(new GridLayout());

        monitorMaskEditor = new RadioGroupFieldEditor(PREF_MONITOR_MASK, Messages.CAPP_monitorMaskCaption_text, 4, AVAILABLE_MONITOR_MASKS, optionsInnerGroup, false);

        addField(
            monitorMaskEditor,
            optionsInnerGroup,
            true,
            () -> store.getDefaultString(PREF_MONITOR_MASK),
            () -> store.getString(PREF_MONITOR_MASK),
            e -> {

                customMaskEditor.setEnabled(MonitorMask.CUSTOM.name().equals(e.getNewValue()), optionsInnerGroup);

                if ( MonitorMask.VALUE.name().equals(e.getNewValue()) ) {
                    customMaskEditor.getTextControl(optionsInnerGroup).setText(Integer.toString(MonitorMask.VALUE.mask()));
                } else if ( MonitorMask.ARCHIVE.name().equals(e.getNewValue()) ) {
                    customMaskEditor.getTextControl(optionsInnerGroup).setText(Integer.toString(MonitorMask.ARCHIVE.mask()));
                } else if ( MonitorMask.ALARM.name().equals(e.getNewValue()) ) {
                    customMaskEditor.getTextControl(optionsInnerGroup).setText(Integer.toString(MonitorMask.ALARM.mask()));
                }

            }
        );

        customMaskEditor = new IntegerFieldEditor(PREF_CUSTOM_MASK, "", optionsInnerGroup);

        customMaskEditor.setEnabled(MonitorMask.CUSTOM.name().equals(store.getString(PREF_MONITOR_MASK)), optionsInnerGroup);
        customMaskEditor.setValidRange(0, 65536);
        customMaskEditor.setTextLimit(32);
        customMaskEditor.getTextControl(optionsInnerGroup).setLayoutData(createIntegerFieldEditorGridData());

        addField(customMaskEditor, optionsInnerGroup, true, () -> store.getDefaultString(PREF_CUSTOM_MASK), () -> store.getString(PREF_CUSTOM_MASK));

        new Separator(optionsInnerGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        variableArrayEditor = new RadioGroupFieldEditor(PREF_VARIABLE_LENGTH_ARRAY, Messages.CAPP_variableLengthArrayCaption_text, 3, AVAILABLE_VAR_ARRAY_SUPPORTS, optionsInnerGroup, false);

        addField(variableArrayEditor, optionsInnerGroup, true, () -> store.getDefaultString(PREF_VARIABLE_LENGTH_ARRAY), () -> store.getString(PREF_VARIABLE_LENGTH_ARRAY));

        new Separator(optionsInnerGroup).setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));

        dbePropertySupportedEditor = new BooleanFieldEditor(PREF_DBE_PROPERTY_SUPPORTED, Messages.CAPP_dbePropertySupportedCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsInnerGroup);

        addField(dbePropertySupportedEditor, optionsInnerGroup, true, () -> store.getDefaultBoolean(PREF_DBE_PROPERTY_SUPPORTED), () -> store.getBoolean(PREF_DBE_PROPERTY_SUPPORTED));

        honorZeropRecisionEditor = new BooleanFieldEditor(PREF_HONOR_ZERO_PRECISION, Messages.CAPP_honorCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsInnerGroup);

        addField(honorZeropRecisionEditor, optionsInnerGroup, true, () -> store.getDefaultBoolean(PREF_HONOR_ZERO_PRECISION), () -> store.getBoolean(PREF_HONOR_ZERO_PRECISION));

        valueRTYPMonitorEditor = new BooleanFieldEditor(PREF_VALUE_RTYP_MONITOR, Messages.CAPP_valueOnlyCheckBox_text, BooleanFieldEditor.SEPARATE_LABEL, optionsInnerGroup);

        addField(valueRTYPMonitorEditor, optionsInnerGroup, true, () -> store.getDefaultBoolean(PREF_VALUE_RTYP_MONITOR), () -> store.getBoolean(PREF_VALUE_RTYP_MONITOR));

        initializeValues(store);

        return container;

    }

}
