/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.formula;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.antlr.runtime.*;
import org.epics.vtype.VDouble;
import org.epics.vtype.VNumber;
import org.epics.pvmanager.expression.DesiredRateExpression;
import static org.epics.pvmanager.ExpressionLanguage.*;
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
    
    static DesiredRateExpression<VDouble> add(DesiredRateExpression<? extends VNumber> arg1, DesiredRateExpression<? extends VNumber> arg2) {
        return resultOf(new TwoArgNumericFunction() {

            @Override
            double calculate(double arg1, double arg2) {
                return arg1 + arg2;
            }
        }, arg1, arg2, opName(" + ", arg1, arg2));
    }
    
    static DesiredRateExpression<VDouble> addCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return add(cast(VNumber.class, arg1), cast(VNumber.class, arg2));
    }
    
    static DesiredRateExpression<VDouble> pow(DesiredRateExpression<? extends VNumber> arg1, DesiredRateExpression<? extends VNumber> arg2) {
        return resultOf(new TwoArgNumericFunction() {

            @Override
            double calculate(double arg1, double arg2) {
                return Math.pow(arg1, arg2);
            }
        }, arg1, arg2, opName(" ^ ", arg1, arg2));
    }
    
    static DesiredRateExpression<VDouble> powCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return pow(cast(VNumber.class, arg1), cast(VNumber.class, arg2));
    }
    
    static DesiredRateExpression<VDouble> subtract(DesiredRateExpression<? extends VNumber> arg1, DesiredRateExpression<? extends VNumber> arg2) {
        return resultOf(new TwoArgNumericFunction() {

            @Override
            double calculate(double arg1, double arg2) {
                return arg1 - arg2;
            }
        }, arg1, arg2, opName(" - ", arg1, arg2));
    }
    
    static DesiredRateExpression<VDouble> subtractCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return subtract(cast(VNumber.class, arg1), cast(VNumber.class, arg2));
    }
    
    static DesiredRateExpression<VDouble> negate(DesiredRateExpression<? extends VNumber> arg) {
        return resultOf(new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return - arg;
            }
        }, arg, opName("-", arg));
    }
    
    static DesiredRateExpression<VDouble> negateCast(DesiredRateExpression<?> arg) {
        return negate(cast(VNumber.class, arg));
    }
    
    static DesiredRateExpression<VDouble> multiply(DesiredRateExpression<? extends VNumber> arg1, DesiredRateExpression<? extends VNumber> arg2) {
        return resultOf(new TwoArgNumericFunction() {

            @Override
            double calculate(double arg1, double arg2) {
                return arg1 * arg2;
            }
        }, arg1, arg2, opName(" * ", arg1, arg2));
    }
    
    static DesiredRateExpression<VDouble> multiplyCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return multiply(cast(VNumber.class, arg1), cast(VNumber.class, arg2));
    }
    
    static DesiredRateExpression<VDouble> divide(DesiredRateExpression<? extends VNumber> arg1, DesiredRateExpression<? extends VNumber> arg2) {
        return resultOf(new TwoArgNumericFunction() {

            @Override
            double calculate(double arg1, double arg2) {
                return arg1 / arg2;
            }
        }, arg1, arg2, opName(" / ", arg1, arg2));
    }
    
    static DesiredRateExpression<VDouble> divideCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return divide(cast(VNumber.class, arg1), cast(VNumber.class, arg2));
    }
    
    static DesiredRateExpression<VDouble> reminder(DesiredRateExpression<? extends VNumber> arg1, DesiredRateExpression<? extends VNumber> arg2) {
        return resultOf(new TwoArgNumericFunction() {

            @Override
            double calculate(double arg1, double arg2) {
                return arg1 % arg2;
            }
        }, arg1, arg2);
    }
    
    static DesiredRateExpression<VDouble> reminderCast(DesiredRateExpression<?> arg1, DesiredRateExpression<?> arg2) {
        return reminder(cast(VNumber.class, arg1), cast(VNumber.class, arg2));
    }
    
    static DesiredRateExpression<?> function(String function, DesiredRateExpressionList<?> args) {
        Collection<FormulaFunction> matchedFunctions = FormulaRegistry.getDefault().findFunctions(function, args.getDesiredRateExpressions().size());
        if (matchedFunctions.size() > 0) {
            FormulaReadFunction readFunction = new FormulaReadFunction(Expressions.functionsOf(args), matchedFunctions);
            StringBuilder sb = new StringBuilder();
            sb.append(function).append('(');
            boolean first = true;
            for (DesiredRateExpression<?> arg : args.getDesiredRateExpressions()) {
                if (!first) {
                    sb.append(", ");
                } else {
                    first = false;
                }
                sb.append(arg.getName());
            }
            sb.append(')');
            return new DesiredRateExpressionImpl<>(args, readFunction, sb.toString());
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
    
    static <R, A> DesiredRateExpression<R> function(String name, OneArgFunction<R, A> function, Class<A> argClazz, DesiredRateExpressionList<?> args) {
        if (args.getDesiredRateExpressions().size() != 1) {
            throw new IllegalArgumentException(name + " function accepts only one argument");
        }
        DesiredRateExpression<A> arg = cast(argClazz, args.getDesiredRateExpressions().get(0));
        return resultOf(function, arg, funName(name, arg));
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
}
