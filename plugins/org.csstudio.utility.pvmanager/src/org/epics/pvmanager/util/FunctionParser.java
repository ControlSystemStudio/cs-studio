/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.epics.util.text.StringUtil;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;

/**
 * Utility class to parse variable names and create simulated signals.
 *
 * @author carcassi
 */
public class FunctionParser {

    /**
     * Parse a function that accepts a scalar value (number or string) or
     * an array value (number or string).
     * 
     * @param string the string to be parsed
     * @param errorMessage the error message
     * @return the name of the function and the argument
     */
    public static List<Object> parseFunctionWithScalarOrArrayArguments(String string, String errorMessage) {
        return parseFunctionWithScalarOrArrayArguments("(\\w+)", string, errorMessage);
    }

    /**
     * Parse a function that accepts a scalar value (number or string) or
     * an array value (number or string).
     * 
     * @param nameRegex regex for function name
     * @param string the string to be parsed
     * @param errorMessage the error message for the exception if parsing fails
     * @return the name of the function and the argument
     */
    public static List<Object> parseFunctionWithScalarOrArrayArguments(String nameRegex, String string, String errorMessage) {
        // Parse the channel name
        List<Object> parsedTokens = FunctionParser.parseFunctionAnyParameter(nameRegex, string);
        
        // Parsing failed
        if (parsedTokens == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        
        // Single argument, return right away
        if (parsedTokens.size() <= 2) {
            return parsedTokens;
        }
        
        // Multiple arguments, collect in array if possible
        Object data = asScalarOrList(parsedTokens.subList(1, parsedTokens.size()));
        if (data == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return Arrays.asList(parsedTokens.get(0), data);
    }
    
    /**
     * Converts the list of arguments into a scalar or
     * an appropriate list. Returns null if it's not possible.
     * 
     * @param objects the argument list
     * @return the value converted or null
     */
    public static Object asScalarOrList(List<Object> objects) {
        if (objects.isEmpty()) {
            return null;
        } else if (objects.size() == 1) {
            return objects.get(0);
        } else if (objects.get(0) instanceof Double) {
            return asListDouble(objects);
        } else if (objects.get(0) instanceof String) {
            return asListString(objects);
        } else {
            return null;
        }
    }
    
    /**
     * Convert the list of arguments to a ListDouble. Returns
     * null if it's not possible.
     * 
     * @param objects a list of arguments
     * @return the converted list or null
     */
    public static ListDouble asListDouble(List<Object> objects) {
        double[] data = new double[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            Object value = objects.get(i);
            if (value instanceof Double) {
                data[i] = (Double) value;
            } else {
                return null;
            }
        }
        return new ArrayDouble(data);
    }

    /**
     * Convert the list of arguments to a List. Returns
     * null if it's not possible.
     * 
     * @param objects a list of arguments
     * @return  the converted list of null
     */
    public static List<String> asListString(List<Object> objects) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            Object value = objects.get(i);
            if (value instanceof String) {
                data.add((String) value);
            } else {
                return null;
            }
        }
        return data;
    }
    
    /**
     * Parses the string and returns the name of the function plus the
     * list of arguments. The arguments can either be doubles or Strings.
     * Returns null if parsing fails.
     * 
     * @param string the string to be parsed
     * @return the function name and arguments; null if parsing fails
     */
    public static List<Object> parseFunctionAnyParameter(String string) {
        return parseFunctionAnyParameter("(\\w+)", string);
    }

    /**
     * Parses the string and returns the name of the function plus the
     * list of arguments. The arguments can either be doubles or Strings.
     * Returns null if parsing fails.
     * 
     * @param nameRegex the syntax for the function name
     * @param string the string to be parsed
     * @return the function name and arguments; null if parsing fails
     */
    public static List<Object> parseFunctionAnyParameter(String nameRegex, String string) {
        if (string.indexOf('(') == -1) {
            if (string.matches(nameRegex)) {
                return Arrays.<Object>asList(string);
            } else {
                return null;
            }
        }
        
        String name = string.substring(0, string.indexOf('('));
        String arguments = string.substring(string.indexOf('(') + 1, string.lastIndexOf(')'));
        
        if (!name.matches(nameRegex)) {
            return null;
        }
        
        List<Object> result = new ArrayList<>();
        result.add(name);
        try {
            List<Object> parsedArguments = StringUtil.parseCSVLine(arguments.trim(), "\\s*,\\s*");
            if (parsedArguments == null) {
                return null;
            }
            result.addAll(parsedArguments);
            return result;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
