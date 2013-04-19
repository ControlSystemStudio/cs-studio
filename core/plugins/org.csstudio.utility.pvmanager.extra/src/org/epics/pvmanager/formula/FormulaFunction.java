/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.List;

/**
 *
 * @author carcassi
 */
public interface FormulaFunction {
    
    public boolean isPure();
    
    public boolean isVarargs();
    
    public String getName();
    
    public String getDescription();
    
    public List<Class<?>> getArgumentTypes();
    
    public List<String> getArgumentNames();
    
    public Class<?> getReturnType();
    
    public Object calculate(List<Object> args);
}
