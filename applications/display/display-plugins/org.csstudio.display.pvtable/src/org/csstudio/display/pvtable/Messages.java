/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.csstudio.display.pvtable.messages"; //$NON-NLS-1$

    public static String Alarm;
    public static String Description;
    public static String CheckAll;
    public static String CheckAll_TT;
    public static String Delete;
    public static String Delete_TT;
    public static String DeleteMeasure;
    public static String DeleteMeasure_TT;
    public static String DeleteLastMeasure;
    public static String DeleteLastMeasure_TT;
    public static String DeleteAllMeasures;
    public static String DeleteAllMeasures_TT;
    public static String EnterPositiveTolerance;
    public static String EnterTolerance;
    public static String EmptyTable;
    public static String Error;
    public static String ExportXLSAction;
    public static String ExportXLSAction_TT;
    public static String ExportXLSImpossible;
    public static String ExportXLSImpossible_NoMeasure;
    public static String ExportXLSImpossible_NoConf;
    public static String ImpossibleToDelete;
    public static String InformationPopup;
    public static String InformationPopup_ConfAlreadyExist;
    public static String InformationPopup_DelConfHeader;
    public static String InformationPopup_NoConfToDel;
    public static String InformationPopup_NoMeasureToDel;
    public static String Insert;
    public static String Insert_TT;
    public static String InvalidFileExtension;
    public static String Measure;
    public static String MeasureAction;
    public static String MeasureAction_TT;
    public static String MeasureImpossible;
    public static String MeasureImpossible_NoConf;
    public static String MeasureImpossible_NoMeasure;
    public static String MeasureSelected;
    public static String PV;
    public static String QuestionPopupDelAllMeasure;
    public static String QuestionPopupDelAllMeasure_TT;
    public static String Restore;
    public static String Restore_TT;
    public static String RestoreSelection;
    public static String RestoreSelection_TT;
    public static String Saved;
    public static String Saved_Value_TimeStamp;
    public static String Snapshot;
    public static String Snapshot_TT;
    public static String SnapshotSelection;
    public static String SnapshotSelection_TT;
    public static String Time;
    public static String TimestampSave;
    public static String Tolerance;
    public static String Tolerance_TT;
    public static String UncheckAll;
    public static String UncheckAll_TT;
    public static String Value;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
        // prevent instantiation
    }
}
