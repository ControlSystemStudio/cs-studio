/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.swt.stringtable;

import org.eclipse.osgi.util.NLS;

/** Eclipse string externalization
 *  @author Kay Kasemir
 */
public class Messages
{
    private static final String BUNDLE_NAME = "org.csstudio.ui.util.swt.stringtable.messages"; //$NON-NLS-1$

    public static String RowEditDialog_ShellTitle;
    public static String StringTableEditor_AddRowText;
    public static String StringTableEditor_DefaultColumnHeader;
    public static String StringTableEditor_EditToolTip;
    public static String StringTableEditor_MoveUpToolTip;
    public static String StringTableEditor_MoveDownToolTip;
    public static String StringTableEditor_DeleteToolTip;

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
