/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.alarmNone;
import static org.epics.vtype.ValueFactory.displayNone;
import static org.epics.vtype.ValueFactory.newVNumberArray;
import static org.epics.vtype.ValueFactory.timeNow;

import java.util.Arrays;
import java.util.List;

import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;

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
	return name;
    }

    @Override
    public String getDescription() {
	return description;
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
	return VNumberArray.class;
    }

    @Override
    public Object calculate(List<Object> args) {
	return newVNumberArray(
		calculate(((VNumberArray) args.get(0)).getData(),
			((VNumberArray) args.get(1)).getData()), alarmNone(),
		timeNow(), displayNone());

    }

    abstract ListNumber calculate(ListNumber arg1, ListNumber arg2);

}
