/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.csstudio.display.rdbtable.messages"; //$NON-NLS-1$
    public static String AddRow;
    public static String AddRow_TT;
    public static String DeleteRow;
    public static String DeleteRow_TT;
    public static String ErrorTitle;
    public static String InfoWritingTable;
    public static String LoginCancelled;
    public static String LoginMsg;
    public static String LoginTitle;
    public static String NewColumnDataFmt;
    public static String RowDeleted_TT;
    public static String RowModified_TT;
    public static String WriteError;
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
