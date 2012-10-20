/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import org.eclipse.osgi.util.NLS;

/** Access to extenalized strings.
 *  @author Eclipse Externalization Wizard
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.ui.messages"; //$NON-NLS-1$
    public static String Acknowledge_Action;
    public static String AcknowledgedAlarmsFmt;
    public static String AddComponent;
    public static String AddComponentDialog_CannotAddError;
    public static String AddComponentDialog_ComponentTT;
    public static String AddComponentDialog_EmptyNameError;
    public static String AddComponentDialog_FacilityTT;
    public static String AddComponentDialog_Guidance;
    public static String AddComponentDialog_Name;
    public static String AddComponentDialog_NameTT;
    public static String AddComponentDialog_Parent;
    public static String AddComponentDialog_PV_TT;
    public static String AddComponentDialog_SubComponentTT;
    public static String AddComponentDialog_Type;
    public static String AddComponentTT;
    public static String AlarmComponent;
    public static String AlarmCurrentMessage;
    public static String AlarmCurrentSeverity;
    public static String AlarmDescription;
    public static String AlarmDescriptionUnknown;
    public static String AlarmArea;
    public static String AlarmMessage;
    public static String AlarmPerspectiveAction;
    public static String AlarmPV;
    public static String AlarmSeverity;
    public static String AlarmTime;
    public static String AlarmValue;
    public static String AutoActionInfo;
    public static String AutoActionInfoFmt;
    public static String AutoActionError;
    public static String AutoActionErrorFmt;
    public static String AutoActionValidErrorFmt;
    public static String CannotUpdateConfigurationErrorFmt;
    public static String Command;
    public static String CommandError;
    public static String CommandErrorFmt;
    public static String Config_Annunciate;
    public static String Config_AnnunciateTT;
    public static String Config_Behavior;
    public static String Config_Commands;
    public static String Config_CommandsTT;
    public static String Config_AutomatedActions;
    public static String Config_AutomatedActionsTT;
    public static String Config_Count;
    public static String Config_CountError;
    public static String Config_CountTT;
    public static String Config_Delay;
    public static String Config_DelayError;
    public static String Config_DelayTT;
    public static String Config_Description;
    public static String Config_DescriptionTT;
    public static String Config_DisableWarning;
    public static String Config_Displays;
    public static String Config_DisplaysTT;
    public static String Config_Enabled;
    public static String Config_EnabledTT;
    public static String Config_Filter;
    public static String Config_FilterTT;
    public static String Config_Guidance;
    public static String Config_GuidanceTT;
    public static String Config_ItemFmt;
    public static String Config_ItemInfoFmt;
    public static String Config_Latch;
    public static String Config_LatchingTT;
    public static String Config_Message;
    public static String Config_Title;
    public static String ConfigureItem;
	public static String CopyToClipboard;
    public static String CurrentAlarmsFmt;
    public static String DefaultEMailBodyStart;
	public static String DefaultEMailSender;
	public static String DefaultEMailTitle;
	public static String Detail;
	public static String Delay;
    public static String DuplicatePV;
    public static String DuplicatePVMesgFmt;
    public static String Duration;
    public static String DurationMsgFmt;
    public static String EditGDCItemDialog_Detail;
    public static String EditGDCItemDialog_Title;
    public static String EditAAItemDialog_Delay;
    public static String EditAAItemDialog_Detail;
    public static String EditAAItemDialog_Title;
    public static String Error;
    public static String ErrorInFilter;
	public static String Filter;
    public static String FilterTT;
    public static String GlobalAlarm_ToolTipFmt;
    public static String MaintenanceMode;
    public static String MaintenanceModeDisableMsg;
    public static String MaintenanceModeEnableMsg;
    public static String MaintenanceModeTT;
    public static String MoreCommandsInfo;
    public static String MoreDisplaysInfo;
    public static String MoreGuidanceInfo;
    public static String MoreAutoActionsInfo;
    public static String MoreTag;
    public static String MoveConfirmationFmt;
    public static String MoveItem;
    public static String MoveItemMsg;
    public static String NormalModeTT;
    public static String Preferences_CommandDirectory;
    public static String Preferences_ConfigSelection;
	public static String Preferences_JMS_IdleTimeout;
    public static String Preferences_JMS_URL;
    public static String Preferences_JMS_User;
    public static String Preferences_JMS_Password;
    public static String Preferences_MaxContextEntries;
    public static String Preferences_Message;
    public static String Preferences_RDB_URL;
    public static String Preferences_RDB_User;
    public static String Preferences_RDB_Password;
    public static String Preferences_Readonly;
    public static String Preferences_RestartMessage;
    public static String Preferences_RootComponent;
    public static String RelatedDisplayErrorFmt;
    public static String RelatedDisplayHTTP_Error;
    public static String RemovalErrorFmt;
    public static String RemoveComponents;
    public static String RemoveConfirmationFmt;
    public static String RenameItem;
    public static String RenameItemMsg;
    public static String RootElementFMT;
    public static String SendToElogAction_ErrorFmt;
    public static String SendToElogAction_InitialTitle;
    public static String SendToElogAction_Message;
    public static String ServerErrorFmt;
    public static String ServerTimeout;
    public static String StringTableEditor_AddRowText;
    public static String StringTableEditor_AddToolTip;
    public static String StringTableEditor_EditToolTip;
    public static String StringTableEditor_MoveUpToolTip;
    public static String StringTableEditor_MoveDownToolTip;
    public static String StringTableEditor_DeleteToolTip;
    public static String Title;
    public static String UnacknowledgeAction;
    public static String Unselect;
    public static String UnselectTT;
    public static String WaitingForServer;
	static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // Prevent instantiation
    }
}
