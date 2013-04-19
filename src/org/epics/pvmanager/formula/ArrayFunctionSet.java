/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

/**
 *
 * @author carcassi
 */
public class ArrayFunctionSet extends FormulaFunctionSet {

    public ArrayFunctionSet() {
        super(new FormulaFunctionSetDescription("array", "Aggregation and calculations on arrays")
                .addFormulaFunction(new ArrayOfFormulaFunction()));
    }

}
