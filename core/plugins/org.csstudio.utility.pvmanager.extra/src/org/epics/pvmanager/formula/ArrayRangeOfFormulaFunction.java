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
import org.epics.util.array.ArrayDouble;

import org.epics.util.array.ListMath;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueUtil;

/**
 * @author carcassi
 *
 */
class ArrayRangeOfFormulaFunction implements FormulaFunction {

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
        return "arrayRangeOf";
    }

    @Override
    public String getDescription() {
        return "Returns the range where the array is defined";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>> asList(VNumberArray.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("array");
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
        
        VNumberArray numberArray = (VNumberArray) args.get(0);
        double min = numberArray.getDimensionDisplay().get(0).getCellBoundaries().getDouble(0);
        double max = numberArray.getDimensionDisplay().get(0).getCellBoundaries().getDouble(numberArray.getSizes().getInt(0));
        
        return newVNumberArray(
                new ArrayDouble(min, max),
                ValueUtil.highestSeverityOf(args, false),
                ValueUtil.latestValidTimeOrNowOf(args),
                displayNone());
    }

}
