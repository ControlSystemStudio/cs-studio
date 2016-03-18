/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.java.string;

import java.util.regex.Pattern;

/** Split string into segments
 *
 *  @author Nick Battam
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

    private static final String QUOTE = "'\\\"";
    private static final String NOT_QUOTE = "^" + QUOTE;
    private static final char SPACE = ' ';

    private static final String splitRegex = "(?="
            + "([" + NOT_QUOTE + "]*"  // any number of non-quotes
            +  "[" + QUOTE + "]"       // a quote
            +  "[" + NOT_QUOTE + "]*"  // any number of non-quotes
            +  "[" + QUOTE + "]"       // a quote
            + ")*"                     // any number of times
            +  "[" + NOT_QUOTE + "]*"  // any number of non quotes
            + "$)";

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
        // space in the following; only if not splitting on TAB
        final String trimmedSource;
        if (splitChar != '\t') {
            trimmedSource = source.replace('\t', SPACE).trim();
        }
        else {
            trimmedSource = source;
        }
        String fullRegex = splitChar + splitRegex;
        if (splitChar == '|') {
            fullRegex = "\\" + fullRegex;
        }
        return Pattern.compile(fullRegex)
                .splitAsStream(trimmedSource)
                .filter(item -> !item.isEmpty())
                .map(item -> item.trim())
                .map(item -> deleteHeadTailQuotes ? removeQuotes(item) : item)
                .toArray(size -> new String[size]);
    }

    /**
     * Remove quotes from start/end of input string
     * If there are no quotes this method has no effect
     *
     * @param input String to parse
     * @return String with wrapping quotes removed.
     */
    static String removeQuotes(String input) {
        final String headtailRegex = "^[" + QUOTE + "]|[" + QUOTE + "]$";
        return input.replaceAll(headtailRegex, "");
    }
}
