/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

/**
 * A function set for table operations.
 *
 * @author carcassi
 */
public class TableFunctionSet extends FormulaFunctionSet {

    /**
     * Creates a new set.
     */
    public TableFunctionSet() {
        super(new FormulaFunctionSetDescription("table", "Function to aggregate and manipulate tables")
                .addFormulaFunction(new ColumnOfVTableFunction())
                .addFormulaFunction(new ColumnFromVNumberArrayFunction())
                .addFormulaFunction(new TableOfFormulaFunction())
                .addFormulaFunction(new RangeFormulaFunction())
                .addFormulaFunction(new StepFormulaFunction())
                .addFormulaFunction(new ColumnFromListNumberGeneratorFunction())
                .addFormulaFunction(new NaturalJoinFunction())
                );
    }


}
