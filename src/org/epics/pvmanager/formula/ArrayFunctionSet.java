/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

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
		"Aggregation and calculations on arrays").addFormulaFunction(
		new ArrayOfNumberFormulaFunction()).addFormulaFunction(
		new ArrayOfStringFormulaFunction()));
    }

}
