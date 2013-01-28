/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.formula;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.*;
import org.epics.vtype.VDouble;
import org.epics.vtype.VNumber;
import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.formula.FormulaLexer;
import org.epics.pvmanager.formula.FormulaParser;
import static org.epics.pvmanager.ExpressionLanguage.*;
import org.epics.pvmanager.expression.DesiredRateExpressionList;

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
    
    public static DesiredRateExpression<?> formula(String formula) {
        try {
            DesiredRateExpression<?> exp = createParser(formula).formula();
            if (exp == null) {
                throw new NullPointerException("Parsing failed");
            }
            return exp;
        } catch (RecognitionException ex) {
            throw new IllegalArgumentException("Error parsing formula: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Malformed formula '" + formula + "'", ex);
        }
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
        switch (function) {
            case "abs":
                return abs(args);
            case "acos":
                return acos(args);
            case "log":
                return log(args);
            case "sin":
                return sin(args);
            case "sqrt":
                return sqrt(args);
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
    
    static DesiredRateExpression<VDouble> log(DesiredRateExpressionList<?> args) {
        return function("log", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.log(arg);
            }
        }, VNumber.class, args);
    }
    
    static DesiredRateExpression<VDouble> sin(DesiredRateExpressionList<?> args) {
        return function("sin", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.sin(arg);
            }
        }, VNumber.class, args);
    }
    
    static DesiredRateExpression<VDouble> abs(DesiredRateExpressionList<?> args) {
        return function("abs", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.abs(arg);
            }
        }, VNumber.class, args);
    }
    
    static DesiredRateExpression<VDouble> acos(DesiredRateExpressionList<?> args) {
        return function("acos", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.acos(arg);
            }
        }, VNumber.class, args);
    }
    
    static DesiredRateExpression<VDouble> sqrt(DesiredRateExpressionList<?> args) {
        return function("sqrt", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.sqrt(arg);
            }
        }, VNumber.class, args);
    }
}
