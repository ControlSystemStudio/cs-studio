/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.preferences;


import org.eclipse.osgi.util.NLS;


/**
 * @author Claudio Rosati, European Spallation Source ERIC
 * @version 1.0.0 3 Nov 2016
 */
public class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.csstudio.diirt.util.preferences.messages"; //$NON-NLS-1$

    public static String BPP_performOk_message;
    public static String BPP_performOk_title;

    public static String CAPP_addressListCaption_text;
    public static String CAPP_alarmRadioButton_text;
    public static String CAPP_archiveRadioButton_text;
    public static String CAPP_autoCheckButton_text;
    public static String CAPP_autoRadioButton_text;
    public static String CAPP_beaconPeriodCaption_text;
    public static String CAPP_connectionTimeoutCaption_text;
    public static String CAPP_contextGroup_text;
    public static String CAPP_customRadioButton_text;
    public static String CAPP_dbePropertySupportedCheckBox_text;
    public static String CAPP_description;
    public static String CAPP_falseRadioButton_text;
    public static String CAPP_honorCheckBox_text;
    public static String CAPP_jcaRadioButton_text;
    public static String CAPP_maxArraySizeSpinnerCaption_text;
    public static String CAPP_modeCaption_text;
    public static String CAPP_monitorMaskCaption_text;
    public static String CAPP_optionsGroup_text;
    public static String CAPP_pureJavaRadioButton_text;
    public static String CAPP_repeaterPortCaption_text;
    public static String CAPP_serverPortCaption_text;
    public static String CAPP_title;
    public static String CAPP_trueRadioButton_text;
    public static String CAPP_valueOnlyCheckBox_text;
    public static String CAPP_valueRadioButton_text;
    public static String CAPP_variableLengthArrayCaption_text;

    public static String DPH_verifyDIIRTPath_blankPath_message;
    public static String DPH_verifyDIIRTPath_nullPath_message;
    public static String DPH_verifyDIIRTPath_pathNotExists_message;
    public static String DPH_verifyDIIRTPath_pathNotValid_message;

    public static String DSPP_browseButton_text;
    public static String DSPP_browseDialog_message;
    public static String DSPP_browseDialog_text;
    public static String DSPP_cdsGroup_text;
    public static String DSPP_defaultDataSourceCaption_text;
    public static String DSPP_delimiterCaption_text;
    public static String DSPP_delimiterText_text;
    public static String DSPP_description;
    public static String DSPP_directoryCaption_text;
    public static String DSPP_exportButton_text;
    public static String DSPP_exportDialog_message;
    public static String DSPP_exportDialog_text;
    public static String DSPP_exportFailed_message;
    public static String DSPP_exportFilesExist_message;
    public static String DSPP_exportFilesExist_title;
    public static String DSPP_exportSuccessful_message;
    public static String DSPP_init_directoryText_exceptionMessage;
    public static String DSPP_overrideCheckBox_text;
    public static String DSPP_resolveMessage;
    public static String DSPP_title;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages ( ) {
    }

}
