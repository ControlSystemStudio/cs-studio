/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
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
	return true;
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
	return "Concatenate the strings";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentTypes()
     */
    @Override
    public List<Class<?>> getArgumentTypes() {
	return Arrays.<Class<?>> asList(VString.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentNames()
     */
    @Override
    public List<String> getArgumentNames() {
	return Arrays.asList("string");
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
