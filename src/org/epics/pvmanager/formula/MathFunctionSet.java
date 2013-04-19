/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

/**
 *
 * @author carcassi
 */
public class MathFunctionSet extends FormulaFunctionSet {

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
                .addFormulaFunction(new OneArgNumericFormulaFunction("log", "Natural logarithm", "arg") {
                    @Override
                    double calculate(double arg) {
                        return Math.log(arg);
                    }
                }));
    }


}
