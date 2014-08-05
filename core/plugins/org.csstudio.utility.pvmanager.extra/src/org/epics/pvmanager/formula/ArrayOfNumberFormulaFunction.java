/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.Arrays;
import java.util.List;
import org.epics.util.array.ListDouble;
import org.epics.util.stats.Statistics;
import org.epics.util.stats.StatisticsUtil;
import org.epics.util.text.NumberFormats;
import org.epics.vtype.Display;
import org.epics.vtype.VNumber;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.ValueFactory;
import static org.epics.vtype.ValueFactory.*;
import org.epics.vtype.ValueUtil;

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
        
        VNumber firstNonNull = null;
        for (Object object : args) {
            if (object != null) {
                firstNonNull = (VNumber) object;
            }
        }
        
        Display display = displayNone();
        if (firstNonNull != null) {
            if (ValueUtil.displayHasValidDisplayLimits(firstNonNull)) {
                display = firstNonNull;
            } else {
                Statistics stats = StatisticsUtil.statisticsOf(data);
                display = newDisplay(stats.getMinimum().doubleValue(), stats.getMinimum().doubleValue(), stats.getMinimum().doubleValue(), 
                        "", NumberFormats.toStringFormat(), stats.getMaximum().doubleValue(), stats.getMaximum().doubleValue(), stats.getMaximum().doubleValue(),
                        stats.getMinimum().doubleValue(), stats.getMaximum().doubleValue());
            }
            
        }

        return ValueFactory.newVNumberArray(data,
                ValueUtil.highestSeverityOf(args, false),
		ValueUtil.latestValidTimeOrNowOf(args),
                display);
    }

}
