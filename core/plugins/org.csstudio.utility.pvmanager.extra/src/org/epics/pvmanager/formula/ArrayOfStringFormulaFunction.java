/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.alarmNone;
import static org.epics.vtype.ValueFactory.newTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.epics.util.time.Timestamp;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.ValueFactory;

/**
 * @author shroffk
 *
 */
public class ArrayOfStringFormulaFunction implements FormulaFunction {

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public boolean isVarArgs() {
        return true;
    }

    @Override
    public String getName() {
        return "arrayOf";
    }

    @Override
    public String getDescription() {
        return "Constructs array from a series of string";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>> asList(VString.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("strArgs");
    }

    @Override
    public Class<?> getReturnType() {
        return VStringArray.class;
    }

    @Override
    public Object calculate(List<Object> args) {

        List<String> data = new ArrayList<String>();
        for (Object arg : args) {
            VString str = (VString) arg;
            if (str == null || str.getValue() == null)
                data.add("NaN");
            else
                data.add(str.getValue());
        }

        return ValueFactory.newVStringArray(data, alarmNone(),
                newTime(Timestamp.now()));
    }

}
