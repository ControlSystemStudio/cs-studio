/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

/**
 * A set of functions to work with Strings.
 * 
 * @author shroffk
 * 
 */
public class StringFunctionSet extends FormulaFunctionSet {

    /**
     * Creates a new set.
     */
    public StringFunctionSet() {
	super(new FormulaFunctionSetDescription("string",
		"Function to aggregate and manipulate strings")
		.addFormulaFunction(new ConcatStringArrayFunction())
		.addFormulaFunction(new ConcatStringsFunction())
		.addFormulaFunction(new PvFormulaFunction())
		.addFormulaFunction(new PvsFormulaFunction())
		.addFormulaFunction(new HighestSeverityFunction())
		.addFormulaFunction(new ToStringFunction())
                );
    }

}
