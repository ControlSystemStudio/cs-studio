/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.epics.pvmanager.expression.DesiredRateExpression;

/**
 *
 * @author carcassi
 */
public class FormulaFunctions {
    
    public static boolean matchArgumentTypes(List<Object> values, FormulaFunction formula) {
        List<Class<?>> types = formula.getArgumentTypes();
        
        if (!matchArgumentCount(values.size(), formula)) {
            return false;
        }
        
        for (int i = 0; i < values.size(); i++) {
            int j = Math.min(i, types.size() - 1);
            if (!types.get(j).isInstance(values.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean matchArgumentCount(int nArguments, FormulaFunction formula) {
        // no varargs must match
        if (!formula.isVarargs() && (formula.getArgumentTypes().size() != nArguments)) {
            return false;
        }
        
        // varargs can have 0 arguments
        if (formula.isVarargs() && ((formula.getArgumentTypes().size() - 1) > nArguments)) {
            return false;
        }
        
        return true;
    }
    
    public static FormulaFunction findFirstMatch(List<Object> values, Collection<FormulaFunction> formulaFunctions) {
        for (FormulaFunction formulaFunction : formulaFunctions) {
            if (matchArgumentTypes(values, formulaFunction)) {
                return formulaFunction;
            }
        }
        
        return null;
    }
    
    public static String formatSignature(FormulaFunction function) {
        // Prepare arguments
        List<String> arguments = new ArrayList<>();
        for (int i = 0; i < function.getArgumentTypes().size() - 1; i++) {
            arguments.add(function.getArgumentTypes().get(i).getSimpleName() + " "
                    + function.getArgumentNames().get(i));
        }
        StringBuilder lastArgument = new StringBuilder();
        lastArgument.append(function.getArgumentTypes().get(function.getArgumentTypes().size() - 1).getSimpleName());
        if (function.isVarargs()) {
            lastArgument.append("...");
        }
        lastArgument.append(" ").append(function.getArgumentNames().get(function.getArgumentTypes().size() - 1));
        arguments.add(lastArgument.toString());

        // Format strings
        StringBuilder sb = new StringBuilder();
        sb.append(format(function.getName(), arguments));
        sb.append(": ");
        sb.append(function.getReturnType().getSimpleName());
        return sb.toString();
    }
    
    private static Pattern postfixTwoArg = Pattern.compile("\\+|-|\\*|/|%|\\^");
    private static Pattern prefixOneArg = Pattern.compile("-");
    
    public static String format(String function, List<String> args) {
        if (args.size() == 2 && postfixTwoArg.matcher(function).matches()) {
            return formatPostfixTwoArgs(function, args);
        }
        if (args.size() == 1 && prefixOneArg.matcher(function).matches()) {
            return formatPrefixOneArg(function, args);
        }
        return formatFunction(function, args);
    }
    
    private static String formatPostfixTwoArgs(String function, List<String> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("(")
          .append(args.get(0))
          .append(" ")
          .append(function)
          .append(" ")
          .append(args.get(1))
          .append(")");
        return sb.toString();
    }
    
    private static String formatPrefixOneArg(String function, List<String> args) {
        StringBuilder sb = new StringBuilder();
        sb.append(function)
          .append(args.get(0));
        return sb.toString();
    }
    
    private static String formatFunction(String function, List<String> args) {
        StringBuilder sb = new StringBuilder();
        sb.append(function).append('(');
        boolean first = true;
        for (String arg : args) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(arg);
        }
        sb.append(')');
        return sb.toString();
    }
    
}
