/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import org.epics.util.array.ArrayDouble;
import org.epics.util.array.CircularBufferDouble;
import org.epics.util.array.ListDouble;
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
	super(new FormulaFunctionSetDescription("array",
		"Aggregation and calculations on arrays")
		.addFormulaFunction(new ArrayOfNumberFormulaFunction())
		.addFormulaFunction(new ArrayOfStringFormulaFunction())
		.addFormulaFunction(
			new TwoArgArrayFormulaFunction("+", "Add Two Arrays",
				"arg1", "arg2") {

			    @Override
			    ListDouble calculate(ListNumber arg1,
				    ListNumber arg2) {
				if (arg1.size() != arg2.size()) {
				    throw new RuntimeException(
					    "Invalid arguments: size of two arrays is nor equal");
				}
				double[] result = new double[arg1.size()];
				for (int i = 0; i < arg1.size(); i++) {
				    result[i] = arg1.getDouble(i)
					    + arg2.getDouble(i);
				}
				return new ArrayDouble(result);
			    }
			})
		.addFormulaFunction(
			new TwoArgArrayFormulaFunction("-",
				"Subtract Two Arrays", "arg1", "arg2") {

			    @Override
			    ListDouble calculate(ListNumber arg1,
				    ListNumber arg2) {
				if (arg1.size() != arg2.size()) {
				    throw new RuntimeException(
					    "Invalid arguments: size of two arrays is nor equal");
				}
				double[] result = new double[arg1.size()];
				for (int i = 0; i < arg1.size(); i++) {
				    result[i] = arg1.getDouble(i)
					    - arg2.getDouble(i);
				}
				return new ArrayDouble(result);
			    }
			}));
    }

}
