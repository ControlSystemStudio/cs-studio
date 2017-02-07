/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.diag.epics.pvtree.messages"; //$NON-NLS-1$

    public static String Collapse;
    public static String CollapseTT;
    public static String Constant;
    public static String ExpandAlarms;
    public static String ExpandAlarmsTT;
    public static String ExpandAll;
    public static String ExpandAllTT;
    public static String ManyPVConfirmFmt;
    public static String ManyPVs;
    public static String PV;
    public static String PV_Label;
    public static String PV_TT;
    public static String TreeMode;
    public static String TreeMode_TT;
    public static String UnknownPVType;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {
        // prevent instantiation
    }
}
