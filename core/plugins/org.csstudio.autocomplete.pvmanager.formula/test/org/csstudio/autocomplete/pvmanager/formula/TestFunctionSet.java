/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.pvmanager.formula;

import org.epics.pvmanager.formula.FormulaFunctionSet;
import org.epics.pvmanager.formula.FormulaFunctionSetDescription;

/**
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TestFunctionSet extends FormulaFunctionSet {

	/**
	 * Creates a new set.
	 */
	public TestFunctionSet() {
		super(new FormulaFunctionSetDescription("test", "Unitary tests functions")
					.addFormulaFunction(new TestFormulaFunction("sin1", "sin1", "arg"))
					.addFormulaFunction(new TestFormulaFunction("sin2", "sin2", "arg")));
	}

}
