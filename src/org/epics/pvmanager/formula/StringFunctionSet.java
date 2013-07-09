/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

/**
 * @author shroffk
 * 
 */
public class StringFunctionSet extends FormulaFunctionSet {

    public StringFunctionSet() {
	super(new FormulaFunctionSetDescription("String",
		"Function to aggregate and manipulate strings")
		.addFormulaFunction(new ConcatStringArrayFunction())
		.addFormulaFunction(new ConcatStringsFunction())
		.addFormulaFunction(new PvFormulaFunction())
		.addFormulaFunction(new PvsFormulaFunction())
                );
    }

}
