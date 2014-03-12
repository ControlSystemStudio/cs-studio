/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.pvmanager.util.NullUtils;
import org.epics.vtype.VNumber;
import static org.epics.vtype.ValueFactory.*;
import org.epics.vtype.ValueUtil;


/**
 * Abstract class for formula functions that take two integer VNumber as arguments
 * and return an integer VNumber.
 * <p>
 * This class takes care of:
 * <ul>
 *    <li>extracting the Number from the VNumbes</li>
 *    <li>null handling - returns null if one argument is null</li>
 *    <li>alarm handling - returns highest alarm</li>
 *    <li>time handling - returns latest time, or now if no time is available</li>
 *    <li>display handling - returns display none</li>
 * </ul>
 * 
 * @author shroffk
 * 
 */
public abstract class AbstractVIntNumberVIntNumberToVIntNumberFormulaFunction implements FormulaFunction {

    private final String name;
    private final String description;
    private final List<Class<?>> argumentTypes;
    private final List<String> argumentNames;
    
    /**
     * Creates a new function.
     * 
     * @param name the name of the function
     * @param description a short description
     * @param arg1Name first argument name
     * @param arg2Name second argument name
     */
    public AbstractVIntNumberVIntNumberToVIntNumberFormulaFunction(String name, String description, String arg1Name, String arg2Name) {
        this.name = name;
        this.description = description;
        this.argumentTypes = Arrays.<Class<?>>asList(VNumber.class, VNumber.class);
        this.argumentNames = Arrays.asList(arg1Name, arg2Name);
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
    public boolean isPure() {
        return true;
    }

    @Override
    public boolean isVarArgs() {
        return false;
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
        return VNumber.class;
    }

    @Override
    public Object calculate(List<Object> args) {
        if (NullUtils.containsNull(args)) {
            return null;
        }
        
        Number arg1 = ((VNumber) args.get(0)).getValue();
        Number arg2 = ((VNumber) args.get(1)).getValue();
        if (arg1 instanceof Float || arg2 instanceof Float ||
                arg1 instanceof Double || arg2 instanceof Double) {
            throw new IllegalArgumentException("Operator '" + getName() + "' only works with integers");
        }
        
	return newVInt(
		calculate(arg1.intValue(), arg2.intValue()),
                ValueUtil.highestSeverityOf(args, false),
		ValueUtil.latestValidTimeOrNowOf(args),
                displayNone());
    }
    
    /**
     * Calculates the result based on the two arguments. This is the only
     * method one has to implement.
     * 
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @return the result
     */
    abstract int calculate(int arg1, int arg2);
    
}
