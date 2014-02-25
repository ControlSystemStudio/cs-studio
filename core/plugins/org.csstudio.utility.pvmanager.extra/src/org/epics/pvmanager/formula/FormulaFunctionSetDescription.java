/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Collection;
import java.util.HashSet;
import static org.epics.pvmanager.formula.FormulaFunctionSet.namePattern;

/**
 * The description for a function set to be used in formula language.
 *
 * @author carcassi
 */
public class FormulaFunctionSetDescription {
    
    String name;
    String description;
    Collection<FormulaFunction> formulaFunctions = new HashSet<>();

    /**
     * A new function set description.
     * 
     * @param name the name of the function set
     * @param description the description of the function set
     */
    public FormulaFunctionSetDescription(String name, String description) {
        this.name = name;
        this.description = description;
        if (!namePattern.matcher(name).matches()) {
            throw new IllegalArgumentException("Name must start by a letter and only consist of letters and numbers");
        }
    }

    /**
     * Adds a formula in the set.
     * 
     * @param formulaFunction the formula to add
     * @return this description
     */
    public FormulaFunctionSetDescription addFormulaFunction(FormulaFunction formulaFunction) {
        formulaFunctions.add(formulaFunction);
        return this;
    }
}
