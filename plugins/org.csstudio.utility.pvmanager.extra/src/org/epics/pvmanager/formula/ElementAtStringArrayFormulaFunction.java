/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.*;

import java.util.Arrays;
import java.util.List;

import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;

/**
 * @author carcassi
 * 
 */
public class ElementAtStringArrayFormulaFunction implements FormulaFunction {

    @Override
    public boolean isPure() {
	return true;
    }

    @Override
    public boolean isVarArgs() {
	return false;
    }

    @Override
    public String getName() {
	return "elementAt";
    }

    @Override
    public String getDescription() {
	return "Returns the element at the specified position in the string array.";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
	return Arrays.<Class<?>> asList(VStringArray.class, VNumber.class);
    }

    @Override
    public List<String> getArgumentNames() {
	return Arrays.asList("array", "index");
    }

    @Override
    public Class<?> getReturnType() {
	return VString.class;
    }

    @Override
    public Object calculate(List<Object> args) {
	VStringArray stringArray = (VStringArray) args.get(0);
        VNumber index = (VNumber) args.get(1);
        if (stringArray == null || index == null) {
            return null;
        }
	int i = index.getValue().intValue();
	return newVString(stringArray.getData().get(i),
		stringArray, stringArray);
    }

}
