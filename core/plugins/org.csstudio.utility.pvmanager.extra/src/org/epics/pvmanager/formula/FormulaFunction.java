/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.List;

/**
 * Definition for a function that can be integrated in the formula language.
 *
 * @author carcassi
 */
public interface FormulaFunction {

    /**
     * Whether the function is a pure function, given the same
     * arguments always returns the same result.
     * 
     * @return true if it's a pure function
     */
    public boolean isPure();
    
    /**
     * Whether the function takes a variable number of arguments.
     * <p>
     * Variable arguments can only be at the end of the argument list,
     * and have the same type.
     * 
     * @return true if the function can accept a variable number of arguments
     */
    public boolean isVarArgs();
    
    /**
     * Return the name of the function.
     * 
     * @return the function name
     */
    public String getName();
    
    /**
     * Return the description of the function.
     * 
     * @return the function description
     */
    public String getDescription();
    
    /**
     * The ordered list of the arguments type.
     * 
     * @return the types of the arguments
     */
    public List<Class<?>> getArgumentTypes();

    /**
     * The ordered list of the argument names.
     * 
     * @return the names of the names
     */
    public List<String> getArgumentNames();

    /**
     * The type of the function result.
     * 
     * @return the result type
     */
    public Class<?> getReturnType();

    /**
     * Calculate the result of the function given the arguments.
     * 
     * @param args the argument list
     * @return the result of the function
     */
    public Object calculate(List<Object> args);
}
