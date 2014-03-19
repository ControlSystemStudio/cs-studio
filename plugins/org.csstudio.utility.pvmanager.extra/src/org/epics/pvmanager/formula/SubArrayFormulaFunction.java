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

import org.epics.util.array.ListMath;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;

/**
 * @author shroffk
 *
 */
public class SubArrayFormulaFunction implements FormulaFunction {

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
        return "subArray";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.epics.pvmanager.formula.FormulaFunction#getDescription()
     */
    @Override
    public String getDescription() {
        return "Result[] = [ array[fromIndex], ..., array[toIndex-1] ]";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentTypes()
     */
    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>> asList(VNumberArray.class, VNumber.class,
                VNumber.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentNames()
     */
    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("array", "fromIndex", "toIndex");
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
        VNumberArray numberArray = (VNumberArray) args.get(0);
        int fromIndex = ((VNumber) args.get(1)).getValue().intValue();
        int toIndex = ((VNumber) args.get(2)).getValue().intValue();
        return newVNumberArray(
                ListMath.limit(numberArray.getData(), fromIndex, toIndex),
                alarmNone(), timeNow(), displayNone());
    }

}
