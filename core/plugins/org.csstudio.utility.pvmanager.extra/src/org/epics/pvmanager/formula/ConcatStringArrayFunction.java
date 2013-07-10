/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;

import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.ValueFactory;

/**
 * @author shroffk
 * 
 */
public class ConcatStringArrayFunction implements FormulaFunction {

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#isPure()
     */
    @Override
    public boolean isPure() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#isVarArgs()
     */
    @Override
    public boolean isVarArgs() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getName()
     */
    @Override
    public String getName() {
	return "concat";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getDescription()
     */
    @Override
    public String getDescription() {
	return "Concatenate the strings of the given string array ";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentTypes()
     */
    @Override
    public List<Class<?>> getArgumentTypes() {
	return Arrays.<Class<?>> asList(VStringArray.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentNames()
     */
    @Override
    public List<String> getArgumentNames() {
	return Arrays.asList("stringArray");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getReturnType()
     */
    @Override
    public Class<?> getReturnType() {
	return VString.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.epics.pvmanager.formula.FormulaFunction#calculate(java.util.List)
     */
    @Override
    public Object calculate(List<Object> args) {
	VStringArray stringArray = (VStringArray) args.get(0);

	if (stringArray == null) {
	    return null;
	}

	StringBuffer sb = new StringBuffer();
	for (String str : stringArray.getData()) {
	    sb.append(str);
	}
	return ValueFactory.newVString(sb.toString(), ValueFactory.alarmNone(),
		ValueFactory.timeNow());

    }

}
