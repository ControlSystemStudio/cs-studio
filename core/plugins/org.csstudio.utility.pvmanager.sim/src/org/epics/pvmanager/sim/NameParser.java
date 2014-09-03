/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sim;

import java.lang.reflect.InvocationTargetException;
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
class NameParser {

    static final Pattern doubleParameter = Pattern.compile("\\s*([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)\\s*");
    static final Pattern commaSeparatedDoubles = Pattern.compile(doubleParameter + "(," + doubleParameter + ")*");
    static final Pattern functionAndParameter = Pattern.compile("(\\w+)(\\(((" + commaSeparatedDoubles + ")?)\\))?");
    static final Pattern functionAndStringParameter = Pattern.compile("(\\w+)(\\((\".*\")\\))?");

    /**
     * Parses a comma separated list of arguments and returns them as a list.
     *
     * @param string a comma separated list of arguments; if null or empty returns
     * the empty list
     * @return the list of parsed arguments
     */
    static List<Object> parseParameters(String string) {
        // Argument is empty
        if (string == null || "".equals(string))
            return Collections.emptyList();

        // Validate input
        if (!commaSeparatedDoubles.matcher(string).matches()) {
            throw new IllegalArgumentException("Arguments must be a comma separated list of double values (was " + string + ")");
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
    static List<Object> parseFunction(String string) {
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

         throw new IllegalArgumentException("Syntax error: function should be like xxx(num1, num2, ...) or xxx(\"string\") and was " + string);
    }

    /**
     * Given a string representing a function call, finds the appropriate call
     * matching the function name, and the appropriate constructor and instanciates
     * it.
     *
     * @param string the function call
     * @return the function
     */
    static Simulation<?> createFunction(String string) {
        List<Object> parameters = parseFunction(string);
        StringBuilder className = new StringBuilder("org.epics.pvmanager.sim.");
        int firstCharPosition = className.length();
        className.append((String) parameters.get(0));
        className.setCharAt(firstCharPosition, Character.toUpperCase(className.charAt(firstCharPosition)));

        try {
            @SuppressWarnings("unchecked")
            Class<SimFunction<?>> clazz = (Class<SimFunction<?>>) Class.forName(className.toString());
            Object[] constructorParams = parameters.subList(1, parameters.size()).toArray();
            Class[] types = new Class[constructorParams.length];
            for (int i = 0; i < types.length; i++) {
                types[i] = constructorParams[i].getClass();
            }
            return clazz.getConstructor(types).newInstance(constructorParams);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Simulation channel " + parameters.get(0) + " is not defined");
        } catch (NoClassDefFoundError ex) {
            if (ex.getMessage().contains("wrong name") && ex.getMessage().lastIndexOf("/") != -1) {
                String suggestedName = ex.getMessage().substring(ex.getMessage().lastIndexOf("/") + 1, ex.getMessage().length() - 1);
                throw new RuntimeException("Function " + parameters.get(0) + " is not defined (Looking for " + suggestedName + "?)");
            }
            throw new RuntimeException("Function " + parameters.get(0) + " is not defined");
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Wrong parameter number for function " + parameters.get(0));
        } catch (SecurityException ex) {
            throw new RuntimeException("Constructor for " + parameters.get(0) + " should be at least package private");
        } catch (InstantiationException ex) {
            throw new RuntimeException("Constructor for " + parameters.get(0) + " failed", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Constructor for " + parameters.get(0) + " should be at least package private");
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Wrong parameter type for function " + parameters.get(0));
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex.getCause().getMessage(), ex);
        }
    }
}
