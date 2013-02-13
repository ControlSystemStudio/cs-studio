/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import org.eclipse.osgi.util.NLS;

/** String externalization, created by IDE
 *  @author Kay Kasemir
 */
public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.alarm.beast.messages"; //$NON-NLS-1$

    public static String Alarm_TT;
    public static String AlarmClientModel_NotInitialized;
    public static String AlarmClientModel_ReadingConfiguration;
    public static String AlarmClientModel_ReadingRDB;
    public static String AlarmClientModel_WaitingForJMSFmt;
    public static String AlarmPV_TT;
    public static String AnnunciationFmt;
    public static String DatabaseConnectionErrorFmt;
    public static String FormattedAnnunciationPrefix;
    public static String NoJMSConnection;
    public static String PriorityAnnunciationPattern;
    public static String PriorityAnnunciationPrefix;
    public static String ReadConfigJobName;
    public static String ReadConfigProgressFmt;
    public static String SeverityLevel_INVALID;
    public static String SeverityLevel_INVALID_ACK;
    public static String SeverityLevel_MAJOR;
    public static String SeverityLevel_MAJOR_ACK;
    public static String SeverityLevel_MINOR;
    public static String SeverityLevel_MINOR_ACK;
    public static String SeverityLevel_OK;
    public static String SeverityLevel_UNDEFINED;
    public static String SeverityLevel_UNDEFINED_ACK;
    public static String Unknown;
    public static String VerboseAlarmDescriptionFmt;
    public static String VerboseAlarmPVDescriptionFmt;

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
