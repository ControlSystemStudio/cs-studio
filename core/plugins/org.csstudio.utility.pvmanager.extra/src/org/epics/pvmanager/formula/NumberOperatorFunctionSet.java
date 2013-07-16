/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

/**
 * A set of function for the number scalar operators.
 *
 * @author carcassi
 */
public class NumberOperatorFunctionSet extends FormulaFunctionSet {

    /**
     * Creates a new set.
     */
    public NumberOperatorFunctionSet() {
        super(new FormulaFunctionSetDescription("numericOperators", "Operators for numeric scalar")
                .addFormulaFunction(new TwoArgNumericFormulaFunction("+", "Numeric addition", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 + arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("-", "Numeric subtraction", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 - arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("*", "Numeric multiplication", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 * arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("/", "Numeric division", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 / arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("%", "Numeric remainder", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return arg1 % arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericFormulaFunction("^", "Numeric power", "arg1", "arg2") {
                    @Override
                    double calculate(double arg1, double arg2) {
                        return Math.pow(arg1, arg2);
                    }
                })
                .addFormulaFunction(new OneArgNumericFormulaFunction("-", "Numeric negation", "arg1") {
                    @Override
                    double calculate(double arg1) {
                        return - arg1;
                    }
                })
                .addFormulaFunction(new TwoArgNumericToBooleanFormulaFunction("<=", "Less than or equal", "arg1", "arg2") {
                    @Override
                    boolean calculate(double arg1, double arg2) {
                        return arg1 <= arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericToBooleanFormulaFunction(">=", "Greater than or equal", "arg1", "arg2") {
                    @Override
                    boolean calculate(double arg1, double arg2) {
                        return arg1 >= arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericToBooleanFormulaFunction("<", "Less than", "arg1", "arg2") {
                    @Override
                    boolean calculate(double arg1, double arg2) {
                        return arg1 < arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericToBooleanFormulaFunction(">", "Greater than", "arg1", "arg2") {
                    @Override
                    boolean calculate(double arg1, double arg2) {
                        return arg1 > arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericToBooleanFormulaFunction("==", "Equal", "arg1", "arg2") {
                    @Override
                    boolean calculate(double arg1, double arg2) {
                        return arg1 == arg2;
                    }
                })
                .addFormulaFunction(new TwoArgNumericToBooleanFormulaFunction("!=", "Not equal", "arg1", "arg2") {
                    @Override
                    boolean calculate(double arg1, double arg2) {
                        return arg1 != arg2;
                    }
                })
                .addFormulaFunction(new TwoArgBooleanFormulaFunction("||", "Conditional OR", "arg1", "arg2") {
                    @Override
                    boolean calculate(boolean arg1, boolean arg2) {
                        return arg1 || arg2;
                    }
                })
                .addFormulaFunction(new TwoArgBooleanFormulaFunction("&&", "Conditional AND", "arg1", "arg2") {
                    @Override
                    boolean calculate(boolean arg1, boolean arg2) {
                        return arg1 && arg2;
                    }
                })
                .addFormulaFunction(new ConditionalOperatorFormulaFunction())
                .addFormulaFunction(new LogicalNotFormulaFunction())
                );
    }


}
