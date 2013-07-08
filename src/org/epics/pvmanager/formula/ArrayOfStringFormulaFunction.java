/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.alarmNone;
import static org.epics.vtype.ValueFactory.displayNone;
import static org.epics.vtype.ValueFactory.newTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.epics.util.array.ListDouble;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VString;
import org.epics.vtype.VStringArray;
import org.epics.vtype.ValueFactory;

/**
 * @author shroffk
 * 
 */
public class ArrayOfStringFormulaFunction implements FormulaFunction {

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
	return "arrayOf";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getDescription()
     */
    @Override
    public String getDescription() {
	return "Constructs array from a series of string";
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
	return Arrays.asList("args");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getReturnType()
     */
    @Override
    public Class<?> getReturnType() {
	return VStringArray.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.epics.pvmanager.formula.FormulaFunction#calculate(java.util.List)
     */
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
