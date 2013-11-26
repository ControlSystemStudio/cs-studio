/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.util.array.ListMath;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;

/**
 * A set of functions to work with {@link VNumberArray}s.
 * 
 * @author carcassi
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
			.addFormulaFunction(new RescaleArrayFormulaFunction())
			.addFormulaFunction(new SubArrayFormulaFunction())
			.addFormulaFunction(new ElementAtArrayFormulaFunction())
			.addFormulaFunction(new ElementAtStringArrayFormulaFunction())
			.addFormulaFunction(
				new TwoArgArrayFormulaFunction("+", "Add Two Arrays", "arg1", "arg2") {

				    @Override
				    ListNumber calculate(ListNumber arg1, ListNumber arg2) {
					return ListMath.sum(arg1, arg2);
				    }
				})
			.addFormulaFunction(
				new TwoArgArrayFormulaFunction("-", "Subtract Two Arrays", "arg1", "arg2") {

				    @Override
				    ListNumber calculate(ListNumber arg1, ListNumber arg2) {
					return ListMath.subtract(arg1, arg2);
				    }
				})
			.addFormulaFunction(
				new TwoArgArrayNumberFormulaFunction("*", "Multiply an array with a number",
					"arg1", "arg2") {

				    @Override
				    ListNumber calculate(ListNumber arg1, Number arg2) {
					return ListMath.rescale(arg1, arg2.doubleValue(), 0.0);
				    }
				})
			.addFormulaFunction(
				new TwoArgArrayNumberFormulaFunction("/",
					"Divide an array with a number",
					"arg1", "arg2") {

				    @Override
				    ListNumber calculate(ListNumber arg1, Number arg2) {
					return ListMath.rescale(arg1, (1 / arg2.doubleValue()), 0.0);
				    }
				}));
    }
}
