/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import static org.epics.pvmanager.formula.FormulaFunctionSet.namePattern;

/**
 *
 * @author carcassi
 */
public class FormulaFunctionSetDescription {
    
    String name;
    String description;
    Collection<FormulaFunction> formulaFunctions = new HashSet<>();

    public FormulaFunctionSetDescription(String name, String description) {
        this.name = name;
        this.description = description;
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
    }
    
    public FormulaFunctionSetDescription addFormulaFunction(FormulaFunction formulaFunction) {
        formulaFunctions.add(formulaFunction);
        return this;
    }
}
