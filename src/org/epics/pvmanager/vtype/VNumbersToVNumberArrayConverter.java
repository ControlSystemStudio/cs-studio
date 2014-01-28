/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.vtype;

import org.epics.vtype.Display;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VNumber;
import org.epics.vtype.ValueFactory;
import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.ReadFunction;
import static org.epics.vtype.ValueFactory.*;
import org.epics.util.array.ListDouble;
import org.epics.util.time.Timestamp;

/**
 * Converts numeric types to VDouble.
 *
 * @author carcassi
 */
class VNumbersToVNumberArrayConverter implements ReadFunction<VNumberArray> {
    
    private final List<? extends ReadFunction<? extends VNumber>> arguments;

    /**
     * Creates a new converter from the given function.
     * 
     * @param argument the argument function
     */
    public VNumbersToVNumberArrayConverter(List<? extends ReadFunction<? extends VNumber>> arguments) {
        this.arguments = arguments;
    }

    @Override
    public VNumberArray readValue() {
        final List<VNumber> values = new ArrayList<VNumber>();
        
        Display meta = displayNone();
        
        for (ReadFunction<? extends VNumber> function : arguments) {
            VNumber number = function.readValue();
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
