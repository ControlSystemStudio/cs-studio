/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 *
 */
public class ChannelAccessPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    private Text addressListText;

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
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(1, false));

        Group contextGroup = new Group(container, SWT.NONE);
        contextGroup.setLayout(new FormLayout());
        GridData gd_contextGroup = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_contextGroup.widthHint = 458;
        gd_contextGroup.heightHint = 207;
        contextGroup.setLayoutData(gd_contextGroup);
        contextGroup.setText(Messages.CAPP_contextGroup_text);

        Label modeCaption = new Label(contextGroup, SWT.NONE);
        modeCaption.setAlignment(SWT.RIGHT);
        FormData fd_modeCaption = new FormData();
        fd_modeCaption.left = new FormAttachment(0, 10);
        modeCaption.setLayoutData(fd_modeCaption);
        modeCaption.setText(Messages.CAPP_modeCaption_text);

        Button pureJavaRadioButton = new Button(contextGroup, SWT.RADIO);
        pureJavaRadioButton.setSelection(true);
        fd_modeCaption.right = new FormAttachment(pureJavaRadioButton, -6);
        fd_modeCaption.top = new FormAttachment(pureJavaRadioButton, 3, SWT.TOP);
        FormData fd_pureJavaRadioButton = new FormData();
        fd_pureJavaRadioButton.top = new FormAttachment(0, 10);
        fd_pureJavaRadioButton.left = new FormAttachment(0, 130);
        pureJavaRadioButton.setLayoutData(fd_pureJavaRadioButton);
        pureJavaRadioButton.setText(Messages.CAPP_pureJavaRadioButton_text);

        Button jniRadioButton = new Button(contextGroup, SWT.RADIO);
        FormData fd_jniRadioButton = new FormData();
        fd_jniRadioButton.top = new FormAttachment(modeCaption, -3, SWT.TOP);
        fd_jniRadioButton.left = new FormAttachment(pureJavaRadioButton, 6);
        jniRadioButton.setLayoutData(fd_jniRadioButton);
        jniRadioButton.setText(Messages.CAPP_jniRadioButton_text);

        Label addressListCaption = new Label(contextGroup, SWT.NONE);
        addressListCaption.setAlignment(SWT.RIGHT);
        FormData fd_addressListCaption = new FormData();
        fd_addressListCaption.top = new FormAttachment(modeCaption, 10);
        fd_addressListCaption.right = new FormAttachment(modeCaption, 0, SWT.RIGHT);
        fd_addressListCaption.left = new FormAttachment(modeCaption, 0, SWT.LEFT);
        addressListCaption.setLayoutData(fd_addressListCaption);
        addressListCaption.setText(Messages.CAPP_addressListCaption_text);

        addressListText = new Text(contextGroup, SWT.BORDER);
        addressListText.setText("");
        FormData fd_addressListText = new FormData();
        fd_addressListText.top = new FormAttachment(pureJavaRadioButton, 6);
        fd_addressListText.left = new FormAttachment(pureJavaRadioButton, 0, SWT.LEFT);
        addressListText.setLayoutData(fd_addressListText);

        Button autoCheckButton = new Button(contextGroup, SWT.CHECK);
        fd_addressListText.right = new FormAttachment(autoCheckButton, -6);
        FormData fd_autoCheckButton = new FormData();
        fd_autoCheckButton.top = new FormAttachment(addressListCaption, -3, SWT.TOP);
        fd_autoCheckButton.right = new FormAttachment(100, -10);
        autoCheckButton.setLayoutData(fd_autoCheckButton);
        autoCheckButton.setText(Messages.CAPP_autoCheckButton_text);

        Label lblConnectionTimeout = new Label(contextGroup, SWT.NONE);
        lblConnectionTimeout.setAlignment(SWT.RIGHT);
        FormData fd_lblConnectionTimeout = new FormData();
        fd_lblConnectionTimeout.top = new FormAttachment(addressListCaption, 13);
        fd_lblConnectionTimeout.left = new FormAttachment(modeCaption, 0, SWT.LEFT);
        lblConnectionTimeout.setLayoutData(fd_lblConnectionTimeout);
        lblConnectionTimeout.setText(Messages.CAPP_connectionTimeoutCaption_text);

        Spinner connectionTimeoutSpinner = new Spinner(contextGroup, SWT.BORDER);
        connectionTimeoutSpinner.setMaximum(300);
        FormData fd_connectionTimeoutSpinner = new FormData();
        fd_connectionTimeoutSpinner.right = new FormAttachment(pureJavaRadioButton, 0, SWT.RIGHT);
        fd_connectionTimeoutSpinner.top = new FormAttachment(addressListText, 6);
        fd_connectionTimeoutSpinner.left = new FormAttachment(pureJavaRadioButton, 0, SWT.LEFT);
        connectionTimeoutSpinner.setLayoutData(fd_connectionTimeoutSpinner);

        Label connectionTimeoutUnitCaption = new Label(contextGroup, SWT.NONE);
        FormData fd_connectionTimeoutUnitCaption = new FormData();
        fd_connectionTimeoutUnitCaption.top = new FormAttachment(lblConnectionTimeout, 0, SWT.TOP);
        fd_connectionTimeoutUnitCaption.left = new FormAttachment(jniRadioButton, 0, SWT.LEFT);
        connectionTimeoutUnitCaption.setLayoutData(fd_connectionTimeoutUnitCaption);
        connectionTimeoutUnitCaption.setText(Messages.CAPP_connectionTimeoutUnitCaption_text);

        Label beaconPeriodCaption = new Label(contextGroup, SWT.NONE);
        beaconPeriodCaption.setText(Messages.CAPP_beaconPeriodCaption_text);
        beaconPeriodCaption.setAlignment(SWT.RIGHT);
        FormData fd_beaconPeriodCaption = new FormData();
        fd_beaconPeriodCaption.left = new FormAttachment(0, 1);
        fd_beaconPeriodCaption.right = new FormAttachment(modeCaption, 0, SWT.RIGHT);
        beaconPeriodCaption.setLayoutData(fd_beaconPeriodCaption);

        Spinner beaconPeriodSpinner = new Spinner(contextGroup, SWT.BORDER);
        beaconPeriodSpinner.setMaximum(300);
        fd_beaconPeriodCaption.top = new FormAttachment(beaconPeriodSpinner, 5, SWT.TOP);
        FormData fd_beaconPeriodSpinner = new FormData();
        fd_beaconPeriodSpinner.right = new FormAttachment(pureJavaRadioButton, 0, SWT.RIGHT);
        fd_beaconPeriodSpinner.top = new FormAttachment(connectionTimeoutSpinner, 6);
        fd_beaconPeriodSpinner.left = new FormAttachment(pureJavaRadioButton, 0, SWT.LEFT);
        beaconPeriodSpinner.setLayoutData(fd_beaconPeriodSpinner);

        Label label_1 = new Label(contextGroup, SWT.NONE);
        label_1.setText(Messages.CAPP_beaconPeriodUnitCaption_text);
        FormData fd_label_1 = new FormData();
        fd_label_1.top = new FormAttachment(beaconPeriodCaption, 0, SWT.TOP);
        fd_label_1.left = new FormAttachment(jniRadioButton, 0, SWT.LEFT);
        label_1.setLayoutData(fd_label_1);

        Label repeaterPortCaption = new Label(contextGroup, SWT.NONE);
        repeaterPortCaption.setText(Messages.CAPP_repeaterPortCaption_text);
        repeaterPortCaption.setAlignment(SWT.RIGHT);
        FormData fd_repeaterPortCaption = new FormData();
        fd_repeaterPortCaption.left = new FormAttachment(modeCaption, 0, SWT.LEFT);
        fd_repeaterPortCaption.right = new FormAttachment(modeCaption, 0, SWT.RIGHT);
        repeaterPortCaption.setLayoutData(fd_repeaterPortCaption);

        Spinner repeaterPortSpinner = new Spinner(contextGroup, SWT.BORDER);
        fd_repeaterPortCaption.top = new FormAttachment(repeaterPortSpinner, 5, SWT.TOP);
        repeaterPortSpinner.setMaximum(65535);
        repeaterPortSpinner.setMinimum(1024);
        FormData fd_repeaterPortSpinner = new FormData();
        fd_repeaterPortSpinner.right = new FormAttachment(pureJavaRadioButton, 0, SWT.RIGHT);
        fd_repeaterPortSpinner.top = new FormAttachment(beaconPeriodSpinner, 6);
        fd_repeaterPortSpinner.left = new FormAttachment(pureJavaRadioButton, 0, SWT.LEFT);
        repeaterPortSpinner.setLayoutData(fd_repeaterPortSpinner);

        Label serverPortSpinnerCaption = new Label(contextGroup, SWT.NONE);
        serverPortSpinnerCaption.setText(Messages.CAPP_serverPortSpinnerCaption_text);
        serverPortSpinnerCaption.setAlignment(SWT.RIGHT);
        FormData fd_serverPortSpinnerCaption = new FormData();
        fd_serverPortSpinnerCaption.left = new FormAttachment(modeCaption, 0, SWT.LEFT);
        fd_serverPortSpinnerCaption.right = new FormAttachment(modeCaption, 0, SWT.RIGHT);
        serverPortSpinnerCaption.setLayoutData(fd_serverPortSpinnerCaption);

        Spinner serverPortSpinner = new Spinner(contextGroup, SWT.BORDER);
        fd_serverPortSpinnerCaption.top = new FormAttachment(serverPortSpinner, 5, SWT.TOP);
        serverPortSpinner.setMaximum(65535);
        serverPortSpinner.setMinimum(1024);
        FormData fd_serverPortSpinner = new FormData();
        fd_serverPortSpinner.right = new FormAttachment(pureJavaRadioButton, 0, SWT.RIGHT);
        fd_serverPortSpinner.top = new FormAttachment(repeaterPortSpinner, 6);
        fd_serverPortSpinner.left = new FormAttachment(pureJavaRadioButton, 0, SWT.LEFT);
        serverPortSpinner.setLayoutData(fd_serverPortSpinner);

        Label maxArraySizeSpinnerCaption = new Label(contextGroup, SWT.NONE);
        maxArraySizeSpinnerCaption.setText(Messages.CAPP_maxArraySizeSpinnerCaption_text);
        maxArraySizeSpinnerCaption.setAlignment(SWT.RIGHT);
        FormData fd_maxArraySizeSpinnerCaption = new FormData();
        fd_maxArraySizeSpinnerCaption.left = new FormAttachment(modeCaption, 0, SWT.LEFT);
        fd_maxArraySizeSpinnerCaption.right = new FormAttachment(modeCaption, 0, SWT.RIGHT);
        maxArraySizeSpinnerCaption.setLayoutData(fd_maxArraySizeSpinnerCaption);

        Spinner maxArraySizeSpinner = new Spinner(contextGroup, SWT.BORDER);
        fd_maxArraySizeSpinnerCaption.top = new FormAttachment(maxArraySizeSpinner, 5, SWT.TOP);
        maxArraySizeSpinner.setIncrement(16);
        maxArraySizeSpinner.setPageIncrement(1024);
        maxArraySizeSpinner.setMaximum(524288);
        maxArraySizeSpinner.setMinimum(1024);
        FormData fd_maxArraySizeSpinner = new FormData();
        fd_maxArraySizeSpinner.right = new FormAttachment(pureJavaRadioButton, 0, SWT.RIGHT);
        fd_maxArraySizeSpinner.top = new FormAttachment(serverPortSpinner, 6);
        fd_maxArraySizeSpinner.left = new FormAttachment(pureJavaRadioButton, 0, SWT.LEFT);
        maxArraySizeSpinner.setLayoutData(fd_maxArraySizeSpinner);

        Label label_2 = new Label(contextGroup, SWT.NONE);
        label_2.setText(Messages.CAPP_maxArraySizeSpinnerUnitCaption_text);
        FormData fd_label_2 = new FormData();
        fd_label_2.top = new FormAttachment(maxArraySizeSpinnerCaption, 0, SWT.TOP);
        fd_label_2.left = new FormAttachment(jniRadioButton, 0, SWT.LEFT);
        label_2.setLayoutData(fd_label_2);

        Group optionsGroup = new Group(container, SWT.NONE);
        GridData gd_optionsGroup = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_optionsGroup.heightHint = 132;
        gd_optionsGroup.widthHint = 77;
        optionsGroup.setLayoutData(gd_optionsGroup);
        optionsGroup.setText(Messages.CAPP_optionsGroup_text);

        Label subscriptionCaption = new Label(optionsGroup, SWT.NONE);
        subscriptionCaption.setAlignment(SWT.RIGHT);
        subscriptionCaption.setBounds(10, 13, 121, 14);
        subscriptionCaption.setText(Messages.CAPP_subscriptionCaption_text);

        Label variableLengthArrayCaption = new Label(optionsGroup, SWT.NONE);
        variableLengthArrayCaption.setAlignment(SWT.RIGHT);
        variableLengthArrayCaption.setBounds(10, 108, 121, 14);
        variableLengthArrayCaption.setText(Messages.CAPP_variableLengthArrayCaption_text);

        Button valueRadioButton = new Button(optionsGroup, SWT.RADIO);
        valueRadioButton.setSelection(true);
        valueRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        valueRadioButton.setBounds(137, 10, 62, 18);
        valueRadioButton.setText(Messages.CAPP_valueRadioButton_text);

        Button archiveRadioButton = new Button(optionsGroup, SWT.RADIO);
        archiveRadioButton.setBounds(205, 10, 62, 18);
        archiveRadioButton.setText(Messages.CAPP_archiveRadioButton_text);

        Button alarmRadioButton = new Button(optionsGroup, SWT.RADIO);
        alarmRadioButton.setBounds(273, 10, 62, 18);
        alarmRadioButton.setText(Messages.CAPP_alarmRadioButton_text);

        Button notifyCheckBox = new Button(optionsGroup, SWT.CHECK);
        notifyCheckBox.setBounds(137, 33, 164, 18);
        notifyCheckBox.setText(Messages.CAPP_notifyCheckBox_text);

        Button honorCheckBox = new Button(optionsGroup, SWT.CHECK);
        honorCheckBox.setBounds(137, 57, 62, 18);
        honorCheckBox.setText(Messages.CAPP_honorCheckBox_text);

        Label zeroPrecisionCaption = new Label(optionsGroup, SWT.NONE);
        zeroPrecisionCaption.setAlignment(SWT.RIGHT);
        zeroPrecisionCaption.setBounds(10, 60, 121, 14);
        zeroPrecisionCaption.setText(Messages.CAPP_zeroPrecisionCaption_text);

        Button valueOnlyCheckBox = new Button(optionsGroup, SWT.CHECK);
        valueOnlyCheckBox.setBounds(137, 81, 78, 18);
        valueOnlyCheckBox.setText(Messages.CAPP_valueOnlyCheckBox_text);

        Label rtypMonitorCaption = new Label(optionsGroup, SWT.NONE);
        rtypMonitorCaption.setAlignment(SWT.RIGHT);
        rtypMonitorCaption.setBounds(10, 84, 121, 14);
        rtypMonitorCaption.setText(Messages.CAPP_rtypMonitorCaption_text);

        Button autoRadioButton = new Button(optionsGroup, SWT.RADIO);
        autoRadioButton.setSelection(true);
        autoRadioButton.setBounds(137, 105, 62, 18);
        autoRadioButton.setText(Messages.CAPP_autoRadioButton_text);

        Button trueRadioButton = new Button(optionsGroup, SWT.RADIO);
        trueRadioButton.setBounds(205, 105, 62, 18);
        trueRadioButton.setText(Messages.CAPP_trueRadioButton_text);

        Button falseRadioButton = new Button(optionsGroup, SWT.RADIO);
        falseRadioButton.setBounds(273, 105, 62, 18);
        falseRadioButton.setText(Messages.CAPP_falseRadioButton_text);

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
