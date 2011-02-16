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
package org.csstudio.alarm.table.internal.localization;

import org.eclipse.osgi.util.NLS;

/**
 * Access to the localization message resources within this plugin.
 *
 * @author Alexander Will
 */
public final class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.alarm.table.internal.localization.messages"; //$NON-NLS-1$

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        // Nothing to do
    }
    
    // CHECKSTYLE:OFF

    public static String ExpertSearchDialog_Button_And;
    public static String ExpertSearchDialog_Button_Or;
    public static String ExpertSearchDialog_Button_Del;
    public static String ExpertSearchDialog_Label_And;
    public static String ExpertSearchDialog_Label_Or;
    
    public static String JmsLogPreferencePage_sound;
    
    public static String AlarmView_acknowledgeAllDropDown;
    public static String AlarmView_acknowledgeButton;
    public static String AlarmView_acknowledgeTitle;
    public static String AlarmView_soundButtonDisable;
    public static String AlarmView_soundButtonEnable;
    public static String AlarmView_soundButtonTitle;
    
    public static String AlarmViewerPreferencePage_column;
    public static String AlarmViewerPreferencePage_columnsHint;
    public static String AlarmViewerPreferencePage_columnNamesMessageKeys;
    public static String AlarmViewerPreferencePage_enterColumnName;
    
    public static String AmsVerifyView_AmsActionsPruefen;
    
    public static String column;
    public static String showOutdatedMessages;
    public static String fontHint;
    public static String columnsHint;
    public static String columnNamesMessageKeys;
    
    public static String ExpertSearchDialog_expertButton;
    public static String ExpertSearchDialog_end;
    public static String ExpertSearchDialog_endTime;
    public static String ExpertSearchDialog_search;
    public static String ExpertSearchDialog_start;
    public static String ExpertSearchDialog_startEndMessage;
    public static String ExpertSearchDialog_startTime;
    
    public static String JmsLogPreferencePage_color;
    public static String JmsLogPreferencePage_key;
    public static String JmsLogPreferencePage_severityKeys;
    public static String JmsLogPreferencePage_value;
    
    public static String LogArchiveViewerPreferencePage_column;
    public static String LogArchiveViewerPreferencePage_columnsHint;
    public static String LogArchiveViewerPreferencePage_columnNamesMessageKeys;
    public static String LogArchiveViewerPreferencePage_dateFormat;
    public static String LogArchiveViewerPreferencePage_maxAnswerSize;
    public static String LogArchiveViewerPreferencePage_maxAnswerSizeExport;
    public static String LogArchiveViewerPreferencePage_javaDateFormat;
    public static String LogArchiveViewerPreferencePage_newColumnName;
    
    public static String LogView_monitoredJmsTopics;
    public static String LogView_properties;
    public static String LogView_propertiesToolTip;
    public static String LogView_messageArea;
    public static String LogView_messageAreaToolTip;
    public static String LogView_defaultMessageText;
    public static String LogView_defaultMessageDescription;
    public static String LogView_runningSince;
    public static String LogView_reloadText;
    public static String LogView_reloadToolTip;
    public static String LogView_reloadErrorTitle;
    public static String LogView_reloadErrorHint;

    public static String LogView_connectionErrorTitle;
    public static String LogView_connectionErrorHint;
    
    public static String LogViewArchive_3days;
    public static String LogViewArchive_day;
    public static String LogViewArchive_from;
    public static String LogViewArchive_NoMessageInDB;
    public static String LogViewArchive_period;
    public static String LogViewArchive_to;
    public static String LogViewArchive_user;
    public static String LogViewArchive_week;
    public static String LogViewArchive_count;
    public static String LogViewArchive_nextButton;
    
    public static String newColumnName;
    
    public static String ViewArchive_7;
    public static String ViewArchive_8;
    public static String ViewArchive_9;
    public static String ViewArchive_10;
    public static String ViewArchive_11;
    public static String ViewArchive_12;
    public static String ViewArchive_15;
    public static String ViewArchive_16;
    public static String ViewArchive_17;
    public static String ViewArchive_18;
    public static String ViewArchive_19;
    public static String ViewArchive_20;
    public static String ViewArchive_21;
    public static String ViewArchive_22;
    public static String ViewArchive_23;
    public static String ViewArchive_24;
    public static String ViewArchive_25;
    public static String ViewArchive_26;
    public static String ViewArchive_27;
    public static String ViewArchive_28;
    
    
    public static String ViewArchiveCommandGroup;
    public static String ViewArchiveExcelExportButton;
    public static String ViewArchive_deleteButton;
    public static String ViewArchive_fromTime;
    public static String ViewArchive_messagesGroup;
    public static String ViewArchive_toTime;
    
    public static String LogView_updateControlButton;
    

}
