/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager.formula;

import java.util.HashMap;
import java.util.Map;
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
        if (args.getDesiredRateExpressions().size() == 1 && oneArgNumericFunction.containsKey(function)) {
            return oneArgNumbericFunction(function, args);
        }
        throw new IllegalArgumentException("No function named '" + function + "' is defined");
    }
    
    private static final Map<String, OneArgNumericFunction> oneArgNumericFunction;
    
    static {
        Map<String, OneArgNumericFunction> map = new HashMap<>();
        
        map.put("abs", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.abs(arg);
            }
        });
        
        map.put("acos", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.acos(arg);
            }
        });
        
        map.put("asin", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.asin(arg);
            }
        });
        
        map.put("atan", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.atan(arg);
            }
        });
        
        map.put("cbrt", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.cbrt(arg);
            }
        });
        
        map.put("ceil", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.ceil(arg);
            }
        });
        
        map.put("cos", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.cos(arg);
            }
        });
        
        map.put("cosh", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.cosh(arg);
            }
        });
        
        map.put("exp", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.exp(arg);
            }
        });
        
        map.put("floor", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.floor(arg);
            }
        });
        
        map.put("log", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.log(arg);
            }
        });
        
        map.put("log10", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.log10(arg);
            }
        });
        
        map.put("signum", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.signum(arg);
            }
        });
        
        map.put("sin", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.sin(arg);
            }
        });
        
        map.put("sinh", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.sinh(arg);
            }
        });
        
        map.put("sqrt", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.sqrt(arg);
            }
        });
        
        map.put("tan", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.tan(arg);
            }
        });
        
        map.put("tanh", new OneArgNumericFunction() {

            @Override
            double calculate(double arg) {
                return Math.tanh(arg);
            }
        });
        
        oneArgNumericFunction = map;
    }
    
    static DesiredRateExpression<VDouble> oneArgNumbericFunction(String name, DesiredRateExpressionList<?> args) {
        return function(name, oneArgNumericFunction.get(name), VNumber.class, args);
    }
    
    static <R, A> DesiredRateExpression<R> function(String name, OneArgFunction<R, A> function, Class<A> argClazz, DesiredRateExpressionList<?> args) {
        if (args.getDesiredRateExpressions().size() != 1) {
            throw new IllegalArgumentException(name + " function accepts only one argument");
        }
        DesiredRateExpression<A> arg = cast(argClazz, args.getDesiredRateExpressions().get(0));
        return resultOf(function, arg, funName(name, arg));
    }
    
    static DesiredRateExpression<VDouble> abs(DesiredRateExpression<? extends VNumber> args) {
        return oneArgNumbericFunction("abs", args);
    }
    
    static DesiredRateExpression<VDouble> acos(DesiredRateExpression<? extends VNumber> args) {
        return oneArgNumbericFunction("acos", args);
    }
    
    static DesiredRateExpression<VDouble> asin(DesiredRateExpression<? extends VNumber> args) {
        return oneArgNumbericFunction("asin", args);
    }
    
    static DesiredRateExpression<VDouble> log(DesiredRateExpression<? extends VNumber> args) {
        return oneArgNumbericFunction("log", args);
    }
    
    static DesiredRateExpression<VDouble> sin(DesiredRateExpression<? extends VNumber> args) {
        return oneArgNumbericFunction("sin", args);
    }
    
    static DesiredRateExpression<VDouble> sqrt(DesiredRateExpression<? extends VNumber> args) {
        return oneArgNumbericFunction("sqrt", args);
    }
}
