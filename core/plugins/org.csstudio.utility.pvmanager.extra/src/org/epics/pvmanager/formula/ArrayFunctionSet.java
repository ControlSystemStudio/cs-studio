/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import org.epics.util.array.ListMath;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;

/**
 * A set of functions to work with {@link VNumberArray}s.
 *
 * @author carcassi
 * @author Mark Davis (NSCL/FRIB)
 */
public class ArrayFunctionSet extends FormulaFunctionSet {

    /**
     * Creates a new set.
     */
    public ArrayFunctionSet() {
        super(
                new FormulaFunctionSetDescription("array",
                        "Aggregation and calculations on arrays")
                        .addFormulaFunction(new ArrayOfNumberFormulaFunction())
                        .addFormulaFunction(new ArrayOfStringFormulaFunction())
                        .addFormulaFunction(new ArrayWithBoundariesFormulaFunction())
                        .addFormulaFunction(new ArrayOfPowFormulaFunction())
                        .addFormulaFunction(new ArrayToPowFormulaFunction())
                        .addFormulaFunction(new CaHistogramFormulaFunction())
                        .addFormulaFunction(new HistogramOfFormulaFunction())
                        .addFormulaFunction(new RescaleArrayFormulaFunction())
                        .addFormulaFunction(new MultArrayFormulaFunction())
                        .addFormulaFunction(new DivArrayFormulaFunction())
                        .addFormulaFunction(new SubArrayFormulaFunction())
                        .addFormulaFunction(new ElementAtArrayFormulaFunction())
                        .addFormulaFunction(new ElementAtStringArrayFormulaFunction())

                        .addFormulaFunction(
                                new TwoArgArrayFormulaFunction("+", "Result[x] = array1[x] + array2[x]",
                                                                       "array1", "array2") {

                                    @Override
                                    ListNumber calculate(ListNumber array1, ListNumber array2) {
                                          return ListMath.sum(array1, array2);
                                    }
                                })
                        .addFormulaFunction(
                                new TwoArgArrayFormulaFunction("-", "Result[x] = array1[x] - array2[x]",
                                                                       "array1", "array2") {

                                    @Override
                                    ListNumber calculate(ListNumber array1, ListNumber array2) {
                                          return ListMath.subtract(array1, array2);
                                    }
                                })
                        .addFormulaFunction(
                                new TwoArgArrayNumberFormulaFunction("+", "Result[x] = array[x] + offset",
                                                                         "array", "offset") {

                                    @Override
                                    ListNumber calculate(ListNumber array, Number offset) {
                                          return ListMath.rescale(array, 1.0, offset.doubleValue());
                                    }
                                })
                        .addFormulaFunction(
                                new TwoArgArrayNumberFormulaFunction("-", "Result[x] = array[x] - offset",
                                                                         "array", "offset") {

                                    @Override
                                    ListNumber calculate(ListNumber array, Number offset) {
                                          return ListMath.rescale(array, 1.0, -offset.doubleValue());
                                    }
                                })
                        .addFormulaFunction(
                                new TwoArgNumberArrayFormulaFunction("-", "Result[x] = offset - array[x]",
                                                                         "offset", "array") {

                                    @Override
                                    ListNumber calculate(Number offset, ListNumber array) {
                                          return ListMath.rescale(array, -1.0, offset.doubleValue());
                                    }
                                })
                        .addFormulaFunction(
                                new TwoArgNumberArrayFormulaFunction("/", "Result[x] = numerator / array[x]",
                                                                         "numerator", "array") {

                                    @Override
                                    ListNumber calculate(Number numerator, ListNumber array) {
                                          return ListMath.invrescale(array, numerator.doubleValue(), 0.0);
                                    }
                                })
                        .addFormulaFunction(
                                new TwoArgArrayNumberFormulaFunction("*", "Result[x] = array[x] * num",
                                                                         "array", "num") {

                                    @Override
                                    ListNumber calculate(ListNumber array, Number num) {
                                          return ListMath.rescale(array, num.doubleValue(), 0.0);
                                    }
                                })
                        .addFormulaFunction(
                                new TwoArgArrayNumberFormulaFunction("/", "Result[x] = array[x] / num",
                                                                         "array", "num") {

                                    @Override
                                    ListNumber calculate(ListNumber array, Number num) {
                                          return ListMath.rescale(array, (1 / num.doubleValue()), 0.0);
                                    }
                                })

                        );
    }
}
