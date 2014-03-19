/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.runtime.*;
import org.epics.vtype.VDouble;
import org.epics.vtype.VNumber;
import org.epics.pvmanager.expression.DesiredRateExpression;
import static org.epics.pvmanager.ExpressionLanguage.*;
import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.expression.DesiredRateExpressionImpl;
import org.epics.pvmanager.expression.DesiredRateExpressionList;
import org.epics.pvmanager.expression.DesiredRateExpressionListImpl;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpression;
import org.epics.pvmanager.expression.DesiredRateReadWriteExpressionImpl;
import org.epics.pvmanager.expression.Expressions;
import org.epics.pvmanager.expression.WriteExpression;
import org.epics.util.text.StringUtil;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VTable;
import org.epics.vtype.VType;
import org.epics.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
public class ExpressionLanguage {
    private ExpressionLanguage() {
        // No instances
    }
    
    static FormulaParser createParser(String text) {
        CharStream stream = new ANTLRStringStream(text);
        FormulaLexer lexer = new FormulaLexer(stream);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        return new FormulaParser(tokenStream);
    }
    
    /**
     * If the formula represents a single channels it returns the name,
     * null otherwise.
     * 
     * @param formula the formula to parse
     * @return the channel it represents or null
     */
    public static String channelFromFormula(String formula) {
        if (!formula.startsWith("=")) {
            if (formula.trim().matches(StringUtil.SINGLEQUOTED_STRING_REGEX)) {
                return StringUtil.unquote(formula);
            }
            return formula;
        } else {
            formula = formula.substring(1);
        }
        try {
            FormulaParser parser = createParser(formula);
            DesiredRateExpression<?> exp = parser.singlePv();
            if (parser.failed()) {
                return null;
            }
            if (exp instanceof LastOfChannelExpression) {
                LastOfChannelExpression channelExp = (LastOfChannelExpression) exp;
                return channelExp.getName();
            }
            return null;
        } catch (RecognitionException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns the expression that will return the live value of the
     * given formula.
     * 
     * @param formula the formula to parse
     * @return an expression for the formula
     */
    public static DesiredRateReadWriteExpression<?, Object> formula(String formula) {
        DesiredRateExpression<?> exp = parseFormula(formula);
            
        if (exp instanceof LastOfChannelExpression) {
            return new DesiredRateReadWriteExpressionImpl<>(exp, org.epics.pvmanager.vtype.ExpressionLanguage.vType(exp.getName()));
        } else if (exp instanceof ErrorDesiredRateExpression) {
            return new DesiredRateReadWriteExpressionImpl<>(exp, readOnlyWriteExpression("Parsing error")); 
        } else {
            return new DesiredRateReadWriteExpressionImpl<>(exp, readOnlyWriteExpression("Read-only formula"));
        }
    }
    
    private static DesiredRateExpression<?> parseFormula(String formula) {
        if (!formula.startsWith("=")) {
            return cachedPv(channelFromFormula(formula));
        } else {
            formula = formula.substring(1);
        }
        
        RuntimeException parsingError;
        try {
            DesiredRateExpression<?> exp = createParser(formula).formula();
            if (exp == null) {
                throw new NullPointerException("Parsing failed");
            }
            return exp;
        } catch (RecognitionException ex) {
            parsingError = new IllegalArgumentException("Error parsing formula: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            parsingError = new IllegalArgumentException("Malformed formula '" + formula + "'", ex);
        }
        return errorDesiredRateExpression(parsingError); 
    }
    
    /**
     * An expression that returns the value of the formula and return null
     * for empty or null formula.
     * <p>
     * Some expressions allow for null expression arguments to handle
     * optional elements. In those cases, using this method makes
     * undeclared arguments fall through.
     * 
     * @param formula the formula, can be null
     * @return an expression of the given type; null if formula is null or empty
     */
    public static DesiredRateExpression<?> formulaArg(String formula) {
        if (formula == null || formula.trim().isEmpty()) {
            return null;
        }
        
        return parseFormula(formula);
    }
    
    /**
     * An expression that returns the value of the formula making sure
     * it's of the given type.
     * 
     * @param <T> the type to read
     * @param formula the formula
     * @param readType the type to read
     * @return an expression of the given type
     */
    public static <T> DesiredRateExpression<T> formula(String formula, Class<T> readType) {
        DesiredRateExpression<?> exp = parseFormula(formula);
        return checkReturnType(readType, "Value", exp);
    }
    
    static DesiredRateExpression<?> cachedPv(String channelName) {
        return new LastOfChannelExpression<>(channelName, Object.class);
    }
    
    static <T> DesiredRateExpression<T> cast(Class<T> clazz, DesiredRateExpression<?> arg1) {
        if (arg1 instanceof LastOfChannelExpression) {
            return ((LastOfChannelExpression<?>)arg1).cast(clazz);
        }
        @SuppressWarnings("unchecked")
        DesiredRateExpression<T> op1 = (DesiredRateExpression<T>) arg1;
        return op1;
    }
    
    static <T> DesiredRateExpressionList<T> cast(Class<T> clazz, DesiredRateExpressionList<?> args) {
        for (DesiredRateExpression<? extends Object> desiredRateExpression : args.getDesiredRateExpressions()) {
            cast(clazz, desiredRateExpression);
        }
        @SuppressWarnings("unchecked")
        DesiredRateExpressionList<T> op1 = (DesiredRateExpressionList<T>) args;
        return op1;
    }
    
    static String opName(String op, DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return "(" + arg1.getName() + op + arg2.getName() + ")";
    }
    
    static String opName(String op, DesiredRateExpression<?> arg) {
        return op + arg.getName();
    }
    
    static String funName(String fun, DesiredRateExpression<?> arg) {
        return fun + "(" + arg.getName()+ ")";
    }
    
    static DesiredRateExpression<?> powCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function("^", new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }

    static DesiredRateExpression<?> threeArgOp(String opName, DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2, DesiredRateExpression<?> arg3) {
        return function(opName, new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2).and(arg3));
    }

    static DesiredRateExpression<?> twoArgOp(String opName, DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function(opName, new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }

    static DesiredRateExpression<?> oneArgOp(String opName, DesiredRateExpression<?> arg) {
        return function(opName, new DesiredRateExpressionListImpl<Object>().and(arg));
    }
    
    static DesiredRateExpression<?> function(String function, DesiredRateExpressionList<?> args) {
        Collection<FormulaFunction> matchedFunctions = FormulaRegistry.getDefault().findFunctions(function, args.getDesiredRateExpressions().size());
        if (matchedFunctions.size() > 0) {
            FormulaReadFunction readFunction = new FormulaReadFunction(Expressions.functionsOf(args), matchedFunctions);
            List<String> argNames = new ArrayList<>(args.getDesiredRateExpressions().size());
            for (DesiredRateExpression<? extends Object> arg : args.getDesiredRateExpressions()) {
                argNames.add(arg.getName());
            }
            return new FormulaFunctionReadExpression(args, readFunction, FormulaFunctions.format(function, argNames));
        }
        
        throw new IllegalArgumentException("No function named '" + function + "' is defined");
    }
    
    static <T> WriteExpression<T> readOnlyWriteExpression(String errorMessage) {
        return new ReadOnlyWriteExpression<>(errorMessage, "");
    }
    
    static <T> DesiredRateExpression<T> errorDesiredRateExpression(RuntimeException error) {
        return new ErrorDesiredRateExpression<>(error, "");
    }
    
    static <T> DesiredRateExpression<T> checkReturnType(final Class<T> clazz, final String argName, final DesiredRateExpression<?> arg1) {
        return new DesiredRateExpressionImpl<T>(arg1, new ReadFunction<T>() {

            @Override
            public T readValue() {
                Object obj = arg1.getFunction().readValue();
                if (obj == null) {
                    return null;
                }
                
                if (clazz.isInstance(obj)) {
                    return clazz.cast(obj);
                } else {
                    throw new RuntimeException(argName + " must be a " + clazz.getSimpleName() + " (was " + ValueUtil.typeOf(obj).getSimpleName() + ")");
                }
            }
        }, arg1.getName());
    }
}
