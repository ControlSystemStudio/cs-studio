/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.Function;
import org.epics.util.array.ListDouble;
import static org.epics.pvmanager.data.ValueFactory.*;
import org.epics.util.time.Timestamp;

/**
 * Converts numeric types to VDouble.
 *
 * @author carcassi
 */
class VNumbersToVDoubleArrayConverter extends Function<VDoubleArray> {
    
    private final List<? extends Function<? extends VNumber>> arguments;

    /**
     * Creates a new converter from the given function.
     * 
     * @param argument the argument function
     */
    public VNumbersToVDoubleArrayConverter(List<? extends Function<? extends VNumber>> arguments) {
        this.arguments = arguments;
    }

    @Override
    public VDoubleArray getValue() {
        final List<VNumber> values = new ArrayList<VNumber>();
        
        Display meta = displayNone();
        
        for (Function<? extends VNumber> function : arguments) {
            VNumber number = function.getValue();
            values.add(number);
            if (meta == null && number != null)
                meta = number;
        }
        
        ListDouble data = new ListDouble() {

            @Override
            public double getDouble(int index) {
                VNumber number = values.get(index);
                if (number == null || number.getValue() == null)
                    return Double.NaN;
                else
                    return number.getValue().doubleValue();
            }

            @Override
            public int size() {
                return values.size();
            }
        };
        
        return ValueFactory.newVDoubleArray(data, alarmNone(), newTime(Timestamp.now()), displayNone());
    }
    
}
