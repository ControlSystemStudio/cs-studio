/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse variable names and create simulated signals.
 *
 * @author carcassi
 */
public class FunctionParser {

    static final Pattern doubleParameter = Pattern.compile("\\s*([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\s*");
    static final Pattern commaSeparatedDoubles = Pattern.compile(doubleParameter + "(," + doubleParameter + ")*");
    static final Pattern functionAndParameter = Pattern.compile("(\\w+)(\\(((" + commaSeparatedDoubles + ")?)\\))?");
    static final Pattern functionAndStringParameter = Pattern.compile("(\\w+)(\\((\".*\")\\))?");
    static final Pattern pvNameAndParameter = Pattern.compile("([^\\(]+)(\\(((" + commaSeparatedDoubles + ")?)\\))?");
    static final Pattern pvNameAndStringParameter = Pattern.compile("([^\\(]+)(\\((\".*\")\\))?");
 
    /**
     * Parses a comma separated list of arguments and returns them as a list.
     *
     * @param string a comma separated list of arguments; if null or empty
     * returns the empty list
     * @return the list of parsed arguments
     */
    static List<Object> parseParameters(String string) {
        // Argument is empty
        if (string == null || "".equals(string)) {
            return Collections.emptyList();
        }

        // Validate input
        if (!commaSeparatedDoubles.matcher(string).matches()) {
            return null;
        }

        // Parse parameters
        Matcher matcher = doubleParameter.matcher(string);
        List<Object> parameters = new ArrayList<Object>();
        while (matcher.find()) {
            String parameter = matcher.group();
            Double value = Double.parseDouble(parameter);
            parameters.add(value);
        }

        return parameters;
    }

    /**
     * Parse a function with parameters and returns a list where the first
     * element is the function name and the others are the parsed arguments.
     *
     * @param string a string representing a function
     * @return the name and the parameters
     */
    public static List<Object> parseFunction(String string) {
        Matcher matcher = functionAndParameter.matcher(string);
        // Match comma separate double list
        if (matcher.matches()) {
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(matcher.group(1));
            parameters.addAll(parseParameters(matcher.group(3)));
            return parameters;
        }

        // Match string parameter
        matcher = functionAndStringParameter.matcher(string);
        if (matcher.matches()) {
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(matcher.group(1));
            String quotedString = matcher.group(3);
            parameters.add(quotedString.substring(1, quotedString.length() - 1));
            return parameters;
        }

        return null;
    }

    /**
     * Parse a pv name with parameters and returns a list where the first
     * element is the function name and the others are the parsed arguments.
     *
     * @param string a string representing a function
     * @return the name and the parameters
     */
    public static List<Object> parsePvAndArguments(String string) {
        Matcher matcher = pvNameAndParameter.matcher(string);
        // Match comma separate double list
        if (matcher.matches()) {
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(matcher.group(1));
            parameters.addAll(parseParameters(matcher.group(3)));
            return parameters;
        }

        // Match string parameter
        matcher = pvNameAndStringParameter.matcher(string);
        if (matcher.matches()) {
            List<Object> parameters = new ArrayList<Object>();
            parameters.add(matcher.group(1));
            String quotedString = matcher.group(3);
            parameters.add(quotedString.substring(1, quotedString.length() - 1));
            return parameters;
        }

        return null;
    }
}
