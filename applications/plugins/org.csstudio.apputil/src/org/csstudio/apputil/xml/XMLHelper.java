package org.csstudio.apputil.xml;

/** Helper for XML output.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLHelper
{
    /** @return Returns string for indentation to given level. */
    public static final void indent(final StringBuilder buf, int level)
    {
        while (level > 0)
        {
            buf.append("    ");
            --level;
        }
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

    /** Add tag and value to buffer with given indentation level. */
    public static final void XML(final StringBuilder buffer, final int level,
    		final String tag, final String value)
    {
        indent(buffer, level);
        buffer.append("<").append(tag).append(">");
        buffer.append(escapeXMLstring(value));
        buffer.append("</").append(tag).append(">\n");
    }
}
