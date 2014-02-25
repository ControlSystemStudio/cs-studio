/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

/**
 * A function set that corresponds to all methods in {@link Math}.
 *
 * @author carcassi
 */
public class MathFunctionSet extends FormulaFunctionSet {

    /**
     * Creates a new set.
     */
    public MathFunctionSet() {
        super(new FormulaFunctionSetDescription("math", "Basic mathematical functions, wrapped from java.lang.Math")
                .addFormulaFunction(new OneArgNumericFormulaFunction("abs", "Absolute value", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.abs(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("acos", "Arc cosine", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.acos(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("asin", "Arc sine", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.asin(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("atan", "Arc tangent", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.atan(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("cbrt", "Cubic root", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.cbrt(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("ceil", "Ceiling function", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.ceil(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("cos", "Cosine", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.cos(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("cosh", "Hyperbolic cosine", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.cosh(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("exp", "Exponential", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.exp(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("floor", "Floor function", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.floor(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("log", "Natural logarithm", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.log(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("log10", "Base 10 logarithm", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.log10(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("signum", "Sign function", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.signum(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("sin", "Sine", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.sin(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("sinh", "Hyperbolic sine", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.sinh(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("sqrt", "Square root", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.sqrt(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("tan", "Tangent", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.tan(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("tanh", "Hyperbolic tangent", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.tanh(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("toDegrees", "Converts radians to degrees", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.toDegrees(arg);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("toRadians", "Conerts degrees to radians", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.toRadians(arg);
                    }
                })
                .addFormulaFunction(new IntegrateFormulaFunction())
                );
    }


}
