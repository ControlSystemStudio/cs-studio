/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.xml;

import java.io.PrintWriter;

/** Helper for XML output.
 *  
 *  Not 100% clear how Unicode etc. all work, but the general idea is this:
 *  The XML will only contain ASCII chars.
 *  Every code point > 126 could be UTF-8 encoded, but we're not doing that.
 * 
 *  Instead, the XML might include things like "&#246;", i.e. plain
 *  ASCII '&', '#', '2', ... to represent the German u-Umlaut, which
 *  itself is not UTF-8 but recognized by XML parsers as a Unicode
 *  code point.
 * 
 *  @author Kay Kasemir, Kunal Shroff
 */
@SuppressWarnings("nls")
public class XMLWriter
{
    /** Create the XML header for standalone UTF-8
     *  @param out    PrintWriter
     */
    public static void header(final PrintWriter out)
    {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    }

    /** Add string for indentation to given level to buffer.
     *  @param out    PrintWriter
     *  @param level  Intendation level
     */
    public static final void indent(final PrintWriter out, int level)
    {
        while (level > 0)
        {
            out.print("    ");
            --level;
        }
    }

    /** Prints text with certain characters replaced by XML escapes.
     *  Seems to work for German Umlaut as well as Russian Cyrillic chars.
     *  @param out    PrintWriter
     *  @param text
     */
    public static final void escapeXMLstring(final PrintWriter out, final String text)
    {
        int i;
        for (i = 0; i < text.length(); ++i)
        {
            char c = text.charAt(i);
            // Escape '&' into '&amp;'.
            if (c == '&')
                out.append("&amp;");
            // Escape '<' into '&lt;'.
            else if (c == '<')
                out.append("&lt;");
            // Escape '>' into '&gt;'.
            else if (c == '>')
                out.append("&gt;");
            // Escape '"' into '&quot;'.
            else if (c == '"')
                out.append("&quot;");
            // Escape ''' into '&#039;'.
            else if (c == '\'')
                out.append("&#039;");
            else if (c < 32 || c > 126)
            {   // Other non-printable. Exact definition not clear.
                out.append("&#");
                out.append(Integer.toString((int) c));
                out.append(";");
            }
            else
                out.append(c);
        }
    }

    /** Start XML tag. No newline.
     *  @param out    PrintWriter
     *  @param level  Indentation level
     *  @param tag    XML tag
     */
    public static final void start(final PrintWriter out, final int level, final String tag)
    {
        indent(out, level);
        out.append("<").append(tag).append(">");
    }

    /** End XML tag. No newline.
     *  @param out    PrintWriter
     *  @param level  Indentation level
     *  @param tag    XML tag
     */
    public static final void end(final PrintWriter out, final int level, final String tag)
    {
        indent(out, level);
        out.append("</").append(tag).append(">");
    }

    /** Add XML for a one-line tagged value to buffer. Includes newline.
     *  @param out    PrintWriter
     *  @param level  Indentation level
     *  @param tag    XML tag
     *  @param value  Value to place in tag
     */
    public static final void XML(final PrintWriter out, final int level,
            final String tag, final Object value)
    {
        start(out, level, tag);
        escapeXMLstring(out, value.toString());
        end(out, 0, tag);
        out.append("\n");
    }
}
