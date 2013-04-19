/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author carcassi
 */
public class FormulaFunctions {
    
    public static boolean matchArgumentTypes(List<Object> values, FormulaFunction formula) {
        List<Class<?>> types = formula.getArgumentTypes();
        
        if (!matchArgumentCount(values.size(), formula)) {
            return false;
        }
        
        for (int i = 0; i < values.size(); i++) {
            int j = Math.min(i, types.size() - 1);
            if (!types.get(j).isInstance(values.get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean matchArgumentCount(int nArguments, FormulaFunction formula) {
        // no varargs must match
        if (!formula.isVarargs() && (formula.getArgumentTypes().size() != nArguments)) {
            return false;
        }
        
        // varargs can have 0 arguments
        if (formula.isVarargs() && ((formula.getArgumentTypes().size() - 1) > nArguments)) {
            return false;
        }
        
        return true;
    }
    
    public static FormulaFunction findFirstMatch(List<Object> values, Collection<FormulaFunction> formulaFunctions) {
        for (FormulaFunction formulaFunction : formulaFunctions) {
            if (matchArgumentTypes(values, formulaFunction)) {
                return formulaFunction;
            }
        }
        
        return null;
    }
    
}
