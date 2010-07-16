/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.xml;

/** Helper for XML output.
 *  
 *  @author Kay Kasemir, Kunal Shroff
 *  @deprecated Use the XMLWrite instead, because it writes out directly to
 *              PrintWriter instead of creating intermediate strings.
 *              Or use JDOM?
 */
@SuppressWarnings("nls")
@Deprecated
public class XMLHelper
{
    /** Add string for indentation to given level to buffer. */
    public static final void indent(final StringBuilder buf, int level)
    {
        while (level > 0)
        {
            buf.append("    ");
            --level;
        }
    }

    /** @return Returns string for indentation to given level. */
    public static final String indent(int level)
    {
        final StringBuilder buf = new StringBuilder();
        indent(buf, level);
        return buf.toString();
    }
    
    /** @return Returns text with less-than and ampersands replaced by XML escapes.
     *  @param text
     */
    public static final String escapeXMLstring(final String text)
    {
    	StringBuilder b = new StringBuilder(text.length()+3);
        int i;
        for (i=0; i<text.length(); ++i)
        {
            char c = text.charAt(i);
            // Escape '&' into '&amp;'.
            if (c == '&')
                b.append("&amp;");
            // Escape '<' into '&lt;'.
            else if (c == '<')
                b.append("&lt;");
            // Escape '>' into '&gt;'.
	    else if (c == '>')
		b.append("&gt;");
	    // Escape '"' into '&quot;'.
	    else if (c == '"')
		b.append("&quot;");
	    // Escape ''' into '&#039;'.
	    else if (c == '\'')
		b.append("&#039;");
            else if (c < 32 || c > 126)
            {   // Other non-printable. Exact definition not clear.
                b.append("&#");
                b.append((int) c);
                b.append(";");            
            }
            else
                b.append(c);
        }
        return b.toString();
    }

    /** Add XML to buffer
     *  @param buffer Buffer
     *  @param level  Indentation level
     *  @param tag    XML tag
     *  @param value  Value to place in tag
     */
    public static final void XML(final StringBuilder buffer, final int level,
    		final String tag, final String value)
    {
        indent(buffer, level);
        buffer.append("<").append(tag).append(">");
        buffer.append(escapeXMLstring(value));
        buffer.append("</").append(tag).append(">\n");
    }

    /** Format XML value in tag
     *  @param buffer Buffer
     *  @param level  Indentation level
     *  @param tag    XML tag
     *  @param value  Value to place in tag
     *  @return XML for value in tag
     */
    public static final String XML(final int level,
            final String tag, final String value)
    {
        final StringBuilder buffer = new StringBuilder();
        XML(buffer, level, tag, value);
        return buffer.toString();
    }
}
