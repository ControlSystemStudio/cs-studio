/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        RuntimeException parsingError;
        try {
            DesiredRateExpression<?> exp = createParser(formula).formula();
            if (exp == null) {
                throw new NullPointerException("Parsing failed");
            }
            
            if (exp instanceof LastOfChannelExpression) {
                return new DesiredRateReadWriteExpressionImpl<>(exp, org.epics.pvmanager.vtype.ExpressionLanguage.vType(exp.getName()));
            } else {
                return new DesiredRateReadWriteExpressionImpl<>(exp, readOnlyWriteExpression("Read-only formula"));
            }
        } catch (RecognitionException ex) {
            parsingError = new IllegalArgumentException("Error parsing formula: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            parsingError = new IllegalArgumentException("Malformed formula '" + formula + "'", ex);
        }
        return new DesiredRateReadWriteExpressionImpl<>(errorDesiredRateExpression(parsingError), readOnlyWriteExpression("Parsing error")); 
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
        // TODO: refactor better; make sure it does check the final type
        RuntimeException parsingError;
        try {
            DesiredRateExpression<?> exp = createParser(formula).formula();
            if (exp == null) {
                throw new NullPointerException("Parsing failed");
            }
            
            return checkReturnType(readType, exp);
        } catch (RecognitionException ex) {
            parsingError = new IllegalArgumentException("Error parsing formula: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            parsingError = new IllegalArgumentException("Malformed formula '" + formula + "'", ex);
        }
        return errorDesiredRateExpression(parsingError); 
    }
    
    static DesiredRateExpression<?> cachedPv(String channelName) {
        return new LastOfChannelExpression<Object>(channelName, Object.class);
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
    
    static DesiredRateExpression<?> addCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function("+", new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }
    
    static DesiredRateExpression<?> powCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function("^", new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }
    
    static DesiredRateExpression<?> subtractCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function("-", new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }
    
    static DesiredRateExpression<?> negateCast(DesiredRateExpression<?> arg) {
        return function("-", new DesiredRateExpressionListImpl<Object>().and(arg));
    }
    
    static DesiredRateExpression<?> multiplyCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function("*", new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }

    static DesiredRateExpression<?> divideCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function("/", new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }
    
    static DesiredRateExpression<?> remainderCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return function("%", new DesiredRateExpressionListImpl<Object>().and(arg1).and(arg2));
    }
    
    static DesiredRateExpression<?> function(String function, DesiredRateExpressionList<?> args) {
        Collection<FormulaFunction> matchedFunctions = FormulaRegistry.getDefault().findFunctions(function, args.getDesiredRateExpressions().size());
        if (matchedFunctions.size() > 0) {
            FormulaReadFunction readFunction = new FormulaReadFunction(Expressions.functionsOf(args), matchedFunctions);
            List<String> argNames = new ArrayList<>(args.getDesiredRateExpressions().size());
            for (DesiredRateExpression<? extends Object> arg : args.getDesiredRateExpressions()) {
                argNames.add(arg.getName());
            }
            return new DesiredRateExpressionImpl<>(args, readFunction, FormulaFunctions.format(function, argNames));
        }
        
        if ("columnOf".equals(function)) {
            if (args.getDesiredRateExpressions().size() != 2) {
                throw new IllegalArgumentException("columnOf takes 2 arguments");
            }
            return columnOf(cast(VTable.class, args.getDesiredRateExpressions().get(0)),
                    cast(VString.class, args.getDesiredRateExpressions().get(1)));
        }
        throw new IllegalArgumentException("No function named '" + function + "' is defined");
    }
    
    static <T> WriteExpression<T> readOnlyWriteExpression(String errorMessage) {
        return new ReadOnlyWriteExpression<>(errorMessage, "");
    }
    
    static <T> DesiredRateExpression<T> errorDesiredRateExpression(RuntimeException error) {
        return new ErrorDesiredRateExpression<>(error, "");
    }
    
    static DesiredRateExpression<VType>
            columnOf(DesiredRateExpression<VTable> tableExpression, DesiredRateExpression<VString> columnExpression) {
        ColumnOfVTableConverter converter =
                new ColumnOfVTableConverter(tableExpression.getFunction(), columnExpression.getFunction());
        return new DesiredRateExpressionImpl<VType>(new DesiredRateExpressionListImpl<Object>()
                .and(tableExpression).and(columnExpression), converter, "columnOf");
    }
    
    static <T> DesiredRateExpression<T> checkReturnType(final Class<T> clazz, final DesiredRateExpression<?> arg1) {
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
                    throw new RuntimeException("Formula does not return " + clazz.getSimpleName() + " (was " + ValueUtil.typeOf(obj).getSimpleName() + ")");
                }
            }
        }, arg1.getName());
    }
}
