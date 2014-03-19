/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.*;


/**
 *
 * @author carcassi
 */
abstract class TwoArgNumericIntegerFormulaFunction implements FormulaFunction {

    private final String name;
    private final String description;
    private final List<Class<?>> argumentTypes;
    private final List<String> argumentNames;
    
    public TwoArgNumericIntegerFormulaFunction(String name, String description, String arg1Name, String arg2Name) {
        this.name = name;
        this.description = description;
        this.argumentTypes = Arrays.<Class<?>>asList(VNumber.class, VNumber.class);
        this.argumentNames = Arrays.asList(arg1Name, arg2Name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
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
        return VNumber.class;
    }

    @Override
    public Object calculate(List<Object> args) {
        Number arg1 = ((VNumber) args.get(0)).getValue();
        Number arg2 = ((VNumber) args.get(1)).getValue();
        if (arg1 instanceof Float || arg2 instanceof Float ||
                arg1 instanceof Double || arg2 instanceof Double) {
            throw new IllegalArgumentException("Operator '" + getName() + "' only works with integers");
        }
        
        return newVInt(calculate(arg1.intValue(), arg2.intValue()),
                alarmNone(), timeNow(), displayNone());
    }
    
    abstract int calculate(int arg1, int arg2);
    
}
