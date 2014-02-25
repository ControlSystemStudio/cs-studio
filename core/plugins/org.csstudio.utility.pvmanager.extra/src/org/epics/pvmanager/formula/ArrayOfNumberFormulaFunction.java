/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.util.array.ListDouble;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
class ArrayOfNumberFormulaFunction implements FormulaFunction {

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
        return "arrayOf";
    }

    @Override
    public String getDescription() {
        return "Constructs array from a series of numbers";
    }

    @Override
    public List<Class<?>> getArgumentTypes() {
        return Arrays.<Class<?>>asList(VNumber.class);
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("args");
    }

    @Override
    public Class<?> getReturnType() {
        return VNumberArray.class;
    }

    @Override
    public Object calculate(final List<Object> args) {

        ListDouble data = new ListDouble() {

            @Override
            public double getDouble(int index) {
                VNumber number = (VNumber) args.get(index);
                if (number == null || number.getValue() == null)
                    return Double.NaN;
                else
                    return number.getValue().doubleValue();
            }

            @Override
            public int size() {
                return args.size();
            }
        };

        return ValueFactory.newVDoubleArray(data, alarmNone(), newTime(Timestamp.now()), displayNone());
    }

}
