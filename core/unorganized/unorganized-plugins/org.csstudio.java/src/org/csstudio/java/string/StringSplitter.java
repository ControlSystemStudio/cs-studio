/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.java.string;

import java.util.ArrayList;
import java.util.List;

/** Split string into segments
 *  @author Kay Kasemir
 *  @author Xihui Chen - Original <code> StringUtil.splitIgnoreInQuotes()</code>
 *
 */
public class StringSplitter
{
    /** Prevent instantiation */
    private StringSplitter()
    {
        // NOP
    }

    private static final char SPACE = ' ';
    private static final char QUOTE = '"';

    /** Split source string into an array of elements separated by the splitting character,
     *  but ignoring split characters enclosed in quotes.
     *
     *  @param trimmedSource String to be split
     *  @param splitChar Character used to split the source string, e.g. ',' or ' '
     *  @param deleteHeadTailQuotes Delete quotes in the head and tail of individual elements
     *                              if <code>true</code>
     *  @return Array of individual elements
     *  @throws Exception on parse error (missing end of quoted string)
     */
    @SuppressWarnings("nls")
    public static String[] splitIgnoreInQuotes(final String source,
                                               final char splitChar,
                                               final boolean deleteHeadTailQuotes) throws Exception
    {
        // Trim, replace tabs with spaces so we only need to handle
        // space in the following
        final String trimmedSource = source.replace('\t', SPACE).trim();
        final List<String> resultList = new ArrayList<String>();
        int pos = 0;
        int start = 0;
        final int length = trimmedSource.length();
        while (pos < length)
        {
            start = pos;
            //skip multiple splitChars
            while (start < length  &&  trimmedSource.charAt(start) == splitChar)
                start++;
            if(start >= length)
                break;
            pos = start;

            while (pos < length  &&  trimmedSource.charAt(pos) !=splitChar)
            {
                //in case of quote, go to the end of next quote
                if (trimmedSource.charAt(pos) == QUOTE)
                {
                    // When locating the ending quote, ignore escaped quotes
                    int end = trimmedSource.indexOf(QUOTE, pos+1);
                    while (end > 0  &&  trimmedSource.charAt(end-1) == '\\')
                        end = trimmedSource.indexOf(QUOTE, end+1);
                    if (end < 0)
                        throw new Exception("Missing end of quoted text in '" +
                                trimmedSource + "'");
                    pos = end + 1;
                }
                else
                    pos++;
            }

            String subString = trimmedSource.substring(start, pos);
            subString = subString.trim();
            if(deleteHeadTailQuotes)
            {   //only delete quotes when both head and tail are quote
                if (subString.charAt(0) == QUOTE  &&
                    subString.charAt(subString.length()-1) == QUOTE)
                    subString = subString.substring(1, subString.length()-1);
            }
            resultList.add(subString);
        }
        return resultList.toArray(new String[resultList.size()]);
    }
}
