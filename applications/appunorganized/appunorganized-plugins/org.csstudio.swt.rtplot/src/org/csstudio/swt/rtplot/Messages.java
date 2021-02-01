/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import org.eclipse.osgi.util.NLS;

/** Externalized strings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Messages extends NLS
{
    public static String AddAnnotation;
    public static String AddAnnotation_Error;
    public static String AddAnnotation_Content;
    public static String AddAnnotation_Content_Help;
    public static String AddAnnotation_DefaultText;
    public static String AddAnnotation_NoTraces;
    public static String AddAnnotation_Text_TT;
    public static String AddAnnotation_Trace;
    public static String AddAnnotation_Trace_TT;
    public static String Crosshair_Cursor;
    public static String EditAnnotation;
    public static String EditAnnotation_Text;
    public static String EditAnnotation_Trace;
    public static String EditAnnotation_Info;
    public static String Legend_Show;
    public static String NameUnitsFmt;
    public static String Pan;
    public static String Pan_TT;
    public static String Pan_X;
    public static String Pan_Y;
    public static String Plain_Pointer;
    public static String PointType_Circles;
    public static String PointType_Diamonds;
    public static String PointType_Squares;
    public static String PointType_Triangles;
    public static String PointType_XMarks;
    public static String Redo_Fmt_TT;
    public static String Redo_TT;
    public static String Scroll_Off_TT;
    public static String Scroll_On_TT;
    public static String Scroll_OnOff;
    public static String Snapshot;
    public static String Toolbar_Close;
    public static String Toolbar_Show;
    public static String TraceType_Area;
    public static String TraceType_AreaDirect;
    public static String TraceType_ErrorBars;
    public static String TraceType_Lines;
    public static String TraceType_LinesDirect;
    public static String TraceType_SingleLine;
    public static String TraceType_SingleLineDirect;
    public static String Type_None;
    public static String Undo_Fmt_TT;
    public static String Undo_TT;
    public static String UpdateAnnotation;
    public static String Zoom_In;
    public static String Zoom_In_TT;
    public static String Zoom_In_X;
    public static String Zoom_In_Y;
    public static String Zoom_Out;
    public static String Zoom_Out_TT;
    public static String Zoom_Out_X;
    public static String Zoom_Out_Y;
    public static String Zoom_Stagger;
    public static String Zoom_Stagger_TT;
    static
    {
        // initialize resource bundle
        NLS.initializeMessages(Activator.ID + ".messages", Messages.class);
    }

    private Messages()
    {
    }
}
