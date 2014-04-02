/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.displayNone;
import static org.epics.vtype.ValueFactory.newVNumberArray;

import java.util.Arrays;
import java.util.List;
import org.epics.pvmanager.util.NullUtils;

import org.epics.util.array.ListNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueUtil;

/**
 * Abstract class for formula functions that take two VNumberArray as arguments
 * and return a VNumberArray.
 * <p>
 * This class takes care of:
 * <ul>
 *    <li>extracting the ListNumber from the VNumberArrays</li>
 *    <li>null handling - returns null if one argument is null</li>
 *    <li>alarm handling - returns highest alarm</li>
 *    <li>time handling - returns latest time, or now if no time is available</li>
 *    <li>display handling - returns display none</li>
 * </ul>
 * 
 * @author shroffk
 * 
 */
public abstract class AbstractVNumberArrayVNumberArrayToVNumberArrayFormulaFunction implements FormulaFunction {

    private static final List<Class<?>> argumentTypes = Arrays.<Class<?>> asList(VNumberArray.class, VNumberArray.class);

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
    public AbstractVNumberArrayVNumberArrayToVNumberArrayFormulaFunction(String name, String description,
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
        if (NullUtils.containsNull(args)) {
            return null;
        }
        
        VNumberArray arg1 = (VNumberArray) args.get(0);
        VNumberArray arg2 = (VNumberArray) args.get(1);
        
	return newVNumberArray(
		calculate(arg1.getData(), arg2.getData()),
                ValueUtil.highestSeverityOf(args, false),
		ValueUtil.latestValidTimeOrNowOf(args),
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
    abstract ListNumber calculate(ListNumber arg1, ListNumber arg2);

}
