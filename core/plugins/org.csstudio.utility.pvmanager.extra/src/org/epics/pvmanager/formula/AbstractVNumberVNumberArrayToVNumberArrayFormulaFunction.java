/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.displayNone;

import java.util.Arrays;
import java.util.List;

import org.epics.util.array.ListNumber;
import org.epics.vtype.Alarm;
import org.epics.vtype.Time;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.newVNumberArray;
import org.epics.vtype.ValueUtil;

/**
 * Abstract class for formula functions that take a VNumberArray and a VNumber as arguments
 * and return a VNumberArray.
 * <p>
 * This class takes care of:
 * <ul>
 *    <li>extracting the Number and ListNumber from the VNumber and VNumberArray</li>
 *    <li>null handling - returns null if one argument is null</li>
 *    <li>alarm handling - returns highest alarm</li>
 *    <li>time handling - returns latest time, or now if no time is available</li>
 *    <li>display handling - returns display none</li>
 * </ul>
 * 
 * @author shroffk
 * 
 */
public abstract class AbstractVNumberVNumberArrayToVNumberArrayFormulaFunction implements
	FormulaFunction {
    
    private static final List<Class<?>> argumentTypes = Arrays.<Class<?>> asList(VNumber.class, VNumberArray.class);

    private final String name;
    private final String description;
    private final List<String> argumentNames;

    /**
     * Creates a new function.
     * 
     * @param name the name of the function
     * @param description a short description
     * @param arg1Name first argument name
     * @param arg2Name second argument name
     */
    public AbstractVNumberVNumberArrayToVNumberArrayFormulaFunction(String name, String description,
	    String arg1Name, String arg2Name) {
	this.name = name;
	this.description = description;
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
        VNumber arg1 = (VNumber) args.get(0);
        VNumberArray arg2 = (VNumberArray) args.get(1);
        // If one argument is null, return null
        if (arg1 == null || arg2 == null) {
            return null;
        }
        // Get highest alarm
        Alarm alarm = ValueUtil.highestSeverityOf(args, false);
        // Get latest time or now
        Time time = ValueUtil.latestTimeOf(args);
        if (time == null) {
            time = ValueFactory.timeNow();
        }
	return newVNumberArray(
		calculate(arg1.getValue(), arg2.getData()),
                alarm,
		time,
                displayNone());
    }

    /**
     * Calculates the result based on the two arguments. This is the only
     * method one has to implement.
     * 
     * @param arg1 the first argument; not null
     * @param arg2 the second argument; not null
     * @return the result; not null
     */
    abstract ListNumber calculate(Number arg1, ListNumber arg2);

}
