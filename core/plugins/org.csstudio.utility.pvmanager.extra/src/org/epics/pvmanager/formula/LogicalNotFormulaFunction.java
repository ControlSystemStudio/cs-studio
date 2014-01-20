/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.vtype.VBoolean;
import org.epics.vtype.ValueFactory;


/**
 * Implementation for ! operator.
 *
 * @author carcassi
 */
class LogicalNotFormulaFunction implements FormulaFunction {

    private final List<Class<?>> argumentTypes;
    private final List<String> argumentNames;
    
    public LogicalNotFormulaFunction() {
        this.argumentTypes = Arrays.<Class<?>>asList(VBoolean.class);
        this.argumentNames = Arrays.asList("arg");
    }

    @Override
    public String getName() {
        return "!";
    }

    @Override
    public String getDescription() {
        return "Conditional NOT";
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
        VBoolean value = (VBoolean) args.get(0);
        if (value == null) {
            return null;
        }
        return ValueFactory.newVBoolean(!value.getValue(), value, value);
    }
    
}
