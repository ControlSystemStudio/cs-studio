package org.csstudio.apputil.text;

/** Helper for creating regular expression from 'glob' pattern
 *  @author Kay Kasemir
 */
public class RegExHelper
{
    /** Convert a file-glob type pattern with '?' and '*'
     *  into a regular expression.
     *  <ul>
     *  <li>'?' can be used to match single character
     *  <li>'*' can be used to zero or more characters
     *  </ul>
     *  @param pattern File-glob
     *  @return Regular expression string.
     */
    @SuppressWarnings("nls")
    public static String regexFromGlob(String pattern)
    {
        // Simplify: Reg ex won't be 'anchored', so remove
        // leading and trailing stuff
        while (pattern.startsWith("*"))
            pattern = pattern.substring(1);
        while (pattern.endsWith("*"))
            pattern = pattern.substring(0, pattern.length()-1);
        
        // Mask stuff that's recognized by a regular expression:
        // '.'
        pattern = pattern.replace(".", "\\.");
        
        // Replace glob ? by reg ex .
        pattern = pattern.replace('?', '.');
        
        // Replace glob * by reg ex .*
        pattern = pattern.replace("*", ".*");
        return pattern;
    }
}
