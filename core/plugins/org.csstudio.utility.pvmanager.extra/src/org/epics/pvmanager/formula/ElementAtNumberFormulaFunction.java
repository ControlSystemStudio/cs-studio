/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import static org.epics.vtype.ValueFactory.displayNone;
import static org.epics.vtype.ValueFactory.newVNumber;

import java.util.Arrays;
import java.util.List;
import org.epics.pvmanager.util.NullUtils;

import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;

/**
 * @author shroffk
 *
 */
class ElementAtNumberFormulaFunction implements FormulaFunction {

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
        return "elementAt";
    }

    @Override
    public String getDescription() {
        return "Result = array[index]";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>> asList(VNumberArray.class, VNumber.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.epics.pvmanager.formula.FormulaFunction#getArgumentNames()
     */
    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("Array", "index");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.epics.pvmanager.formula.FormulaFunction#getReturnType()
     */
    @Override
    public Class<?> getReturnType() {
        return VNumber.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.epics.pvmanager.formula.FormulaFunction#calculate(java.util.List)
     */
    @Override
    public Object calculate(List<Object> args) {
        if (NullUtils.containsNull(args)) {
            return null;
        }
        
        VNumberArray numberArray = (VNumberArray) args.get(0);
        VNumber index = (VNumber) args.get(1);
        int i = index.getValue().intValue();
        
        return newVNumber(numberArray.getData().getDouble(i),
                numberArray, numberArray, displayNone());
    }

}
