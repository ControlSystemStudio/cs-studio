/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A set of functions that can be used in the formulas.
 * <p>
 * Objects of this class can be registered in the {@link FormulaRegistry} and
 * the functions will be available in the formula language.
 *
 * @author carcassi
 */
public class FormulaFunctionSet {
    static Pattern namePattern = Pattern.compile("[a-zA-Z_]\\w*");
    
    private String name;
    private String description;
    private Collection<FormulaFunction> formulaFunctions;

    /**
     * Creates a new ser of functions to be registered in the formula language.
     * 
     * @param functionSetDescription the description of the function set
     */
    public FormulaFunctionSet(FormulaFunctionSetDescription functionSetDescription) {
        this.name = functionSetDescription.name;
        this.description = functionSetDescription.description;
        this.formulaFunctions = Collections.unmodifiableSet(new HashSet<>(functionSetDescription.formulaFunctions));
    }

    /**
     * Returns the name of the function set.
     * 
     * @return the function set name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the description of the function set.
     * 
     * @return the function set description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * The names of all functions in this set.
     * 
     * @return the function names
     */
    public final Collection<String> getFunctionNames() {
        Set<String> names = new HashSet<>();
        for (FormulaFunction formulaFunction : formulaFunctions) {
            names.add(formulaFunction.getName());
        }
        return names;
    }

    /**
     * Returns all the functions in the set with the given name.
     * 
     * @param name the name of the function
     * @return the matched functions; never null
     */
    public final Collection<FormulaFunction> findFunctions(String name) {
        if (name == null) {
            return Collections.emptyList();
        }
        
        Set<FormulaFunction> formulas = new HashSet<>();
        for (FormulaFunction formulaFunction : formulaFunctions) {
            if (name.equals(formulaFunction.getName())) {
                formulas.add(formulaFunction);
            }
        }
        return formulas;
    }

    /**
     * Returns all functions in the set.
     * 
     * @return the functions in the set
     */
    public final Collection<FormulaFunction> getFunctions() {
        return formulaFunctions;
    }
}
