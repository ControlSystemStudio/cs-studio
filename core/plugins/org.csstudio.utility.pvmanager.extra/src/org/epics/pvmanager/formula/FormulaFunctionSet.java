/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author carcassi
 */
public class FormulaFunctionSet {
    static Pattern namePattern = Pattern.compile("[a-zA-Z_]\\w*");
    
    private String name;
    private String description;
    private Collection<FormulaFunction> formulaFunctions;

    public FormulaFunctionSet(FormulaFunctionSetDescription serviceDescription) {
        this.name = serviceDescription.name;
        this.description = serviceDescription.description;
        this.formulaFunctions = Collections.unmodifiableSet(new HashSet<>(serviceDescription.formulaFunctions));
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }
    
    public final Collection<String> getFunctionNames() {
        Set<String> names = new HashSet<>();
        for (FormulaFunction formulaFunction : formulaFunctions) {
            names.add(formulaFunction.getName());
        }
        return names;
    }
    
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

    public final Collection<FormulaFunction> getFunctions() {
        return formulaFunctions;
    }
}
