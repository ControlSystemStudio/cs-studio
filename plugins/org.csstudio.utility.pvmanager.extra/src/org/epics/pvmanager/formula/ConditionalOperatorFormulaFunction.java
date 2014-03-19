/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.vtype.VBoolean;


/**
 * Implementation for ?: operator.
 *
 * @author carcassi
 */
class ConditionalOperatorFormulaFunction implements FormulaFunction {

    private final List<Class<?>> argumentTypes;
    private final List<String> argumentNames;
    
    public ConditionalOperatorFormulaFunction() {
        this.argumentTypes = Arrays.<Class<?>>asList(VBoolean.class, Object.class, Object.class);
        this.argumentNames = Arrays.asList("condition", "valueIfTrue", "valueIfFalse");
    }

    @Override
    public String getName() {
        return "?:";
    }

    @Override
    public String getDescription() {
        return "Conditional operator";
    }

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return argumentTypes;
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    @Override
    public Class<?> getReturnType() {
        return VBoolean.class;
    }

    @Override
    public Object calculate(List<Object> args) {
        VBoolean condition = (VBoolean) args.get(0);
        if (condition == null) {
            return null;
        }
        Object value;
        if (condition.getValue()) {
            value = args.get(1);
        } else {
            value = args.get(2);
        }
        
        return value;
    }
    
}
