/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;

import org.epics.util.array.ListDouble;
import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.alarmNone;
import static org.epics.vtype.ValueFactory.timeNow;
import static org.epics.vtype.ValueFactory.displayNone;

/**
 * @author shroffk
 * 
 */
public abstract class TwoArgArrayFormulaFunction implements FormulaFunction {

    private final String name;
    private final String description;
    private final List<Class<?>> argumentTypes;
    private final List<String> argumentNames;

    public TwoArgArrayFormulaFunction(String name, String description,
	    String arg1Name, String arg2Name) {
	this.name = name;
	this.description = description;
	this.argumentTypes = Arrays.<Class<?>> asList(VNumberArray.class,
		VNumberArray.class);
	this.argumentNames = Arrays.asList(arg1Name, arg2Name);
    }

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
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentTypes()
     */
    @Override
    public List<Class<?>> getArgumentTypes() {
	return argumentTypes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentNames()
     */
    @Override
    public List<String> getArgumentNames() {
	return argumentNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.epics.pvmanager.formula.FormulaFunction#getReturnType()
     */
    @Override
    public Class<?> getReturnType() {
	return VNumberArray.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.epics.pvmanager.formula.FormulaFunction#calculate(java.util.List)
     */
    @Override
    public Object calculate(List<Object> args) {
	return ValueFactory.newVDoubleArray(
		calculate(((VNumberArray) args.get(0)).getData(),
			((VNumberArray) args.get(1)).getData()), alarmNone(),
		timeNow(), displayNone());

    }

    abstract ListDouble calculate(ListNumber arg1, ListNumber arg2);

}
