/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.epics.vtype.ArrayDimensionDisplay;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.table.ListNumberProvider;

/**
 * Formula function that constructs an array with given data and boundaries.
 *
 * @author carcassi
 */
class ArrayWithBoundariesFormulaFunction implements FormulaFunction {

    @Override
    public boolean isPure() {
        return true;
    }
    
    @Override
    public boolean isVarArgs() {
        return true;
    }

    @Override
    public String getName() {
        return "arrayWithBoundaries";
    }

    @Override
    public String getDescription() {
        return "Returns an array with the given values and cell boundaries";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VNumberArray.class, ListNumberProvider.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("dataArray", "numberGenerators");
    }

    @Override
    public Class<?> getReturnType() {
        return VNumberArray.class;
    }

    @Override
    public Object calculate(final List<Object> args) {
        VNumberArray array = (VNumberArray) args.get(0);
        if (array == null) {
            return null;
        }
        if (array.getSizes().size() != args.size() - 1) {
            throw new IllegalArgumentException("Dimension of the array must match the number of ListNumberProvider");
        }

        List<ArrayDimensionDisplay> dimDisplay = new ArrayList<>();
        for (int i = 1; i < args.size(); i++) {
            ListNumberProvider numberGenerator = (ListNumberProvider) args.get(i);
            if (numberGenerator == null) {
                return null;
            } else {
                dimDisplay.add(ValueFactory.newDisplay(numberGenerator.createListNumber(array.getSizes().getInt(i-1) + 1), ""));
            }
        }
        
        
        return ValueFactory.newVNumberArray(array.getData(), array.getSizes(), dimDisplay, array, array, array);
    }
    
}
