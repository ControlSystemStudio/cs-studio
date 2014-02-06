/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;

import org.epics.vtype.VString;
import org.epics.vtype.ValueFactory;

/**
 * @author shroffk
 * 
 */
public class ConcatStringsFunction implements FormulaFunction {

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
	return "concat";
    }

    @Override
    public String getDescription() {
	return "Concatenate the strings";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
	return Arrays.<Class<?>> asList(VString.class);
    }

    @Override
    public List<String> getArgumentNames() {
	return Arrays.asList("string");
    }

    @Override
    public Class<?> getReturnType() {
	return VString.class;
    }

    @Override
    public Object calculate(List<Object> args) {

	StringBuffer sb = new StringBuffer();

	for (Object object : args) {
	    VString str = (VString) object;
	    // TODO should this raise an exception
	    if (str == null) {
		return null;
	    }
	    sb.append(str.getValue());
	}
	return ValueFactory.newVString(sb.toString(), ValueFactory.alarmNone(),
		ValueFactory.timeNow());

    }

}
