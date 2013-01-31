/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author carcassi
 */
public class StringUtil {

    private StringUtil() {
    }
    
    static final String escapeSequenceRegex = "\\\\(\"|\\\\|\'|r|n|b|t|u[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]|[0-3]?[0-7]?[0-7])";
    static final String quotedStringRegex = "\"([^\"\\\\]|" + StringUtil.escapeSequenceRegex + ")*\"";
    
    static Pattern escapeSequence = Pattern.compile(escapeSequenceRegex);
    
    public static String unquote(String quotedString) {
        return unescapeString(quotedString.substring(1, quotedString.length() - 1));
    }
    
    public static String unescapeString(String escapedString) {
        Matcher match = escapeSequence.matcher(escapedString);
        StringBuffer output = new StringBuffer();
        while(match.find()) {
            match.appendReplacement(output, substitution(match.group()));
        }
        match.appendTail(output);
        return output.toString();
    }
    
    private static String substitution(String escapedToken) {
        switch (escapedToken) {
            case "\\\"":
                return "\"";
            case "\\\\":
                return "\\\\";
            case "\\\'":
                return "\'";
            case "\\r":
                return "\r";
            case "\\n":
                return "\n";
            case "\\b":
                return "\b";
            case "\\t":
                return "\t";
        }
        if (escapedToken.startsWith("\\u")) {
            // It seems that you can't use replace with an escaped
            // unicode sequence. Bug in Java?
            // Parsing myself
            return Character.toString((char) Long.parseLong(escapedToken.substring(2), 16));
        }
        return Character.toString((char) Long.parseLong(escapedToken.substring(1), 8));
    }
    
}
