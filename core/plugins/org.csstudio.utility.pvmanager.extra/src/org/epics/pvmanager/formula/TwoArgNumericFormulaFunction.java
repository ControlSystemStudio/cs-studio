/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Alarm;
import org.epics.vtype.Time;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;


/**
 *
 * @author carcassi
 */
abstract class TwoArgNumericFormulaFunction implements FormulaFunction {

    private final String name;
    private final String description;
    private final List<Class<?>> argumentTypes;
    private final List<String> argumentNames;
    
    public TwoArgNumericFormulaFunction(String name, String description, String arg1Name, String arg2Name) {
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
        Object arg1 = args.get(0);
        Object arg2 = args.get(1);
        if (arg1 == null || arg2 == null) {
            return null;
        }
        Alarm alarm = ValueUtil.highestSeverityOf(args, false);
        Time time = ValueUtil.latestTimeOf(args);
        if (time == null) {
            time = ValueFactory.timeNow();
        }
        return ValueFactory.newVDouble(
                calculate(((VNumber) args.get(0)).getValue().doubleValue(),
                ((VNumber) args.get(1)).getValue().doubleValue())
                , alarm, time, ValueFactory.displayNone());
    }
    
    abstract double calculate(double arg1, double arg2);
    
}
