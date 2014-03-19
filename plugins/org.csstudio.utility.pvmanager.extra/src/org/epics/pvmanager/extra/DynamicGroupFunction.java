/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.ReadFunction;

/**
 * Function that implements the dynamic group.
 *
 * @author carcassi
 */
class DynamicGroupFunction implements ReadFunction<List<Object>> {
    
    // Guarded by this
    private final List<ReadFunction<?>> arguments = new ArrayList<ReadFunction<?>>();
    // Guarded by this
    private List<Exception> exceptions = new ArrayList<Exception>();
    // Gaurded by this
    private List<Object> previousValues = new ArrayList<Object>();

    @Override
    public synchronized List<Object> readValue() {
        List<Object> result = new ArrayList<Object>();
        for (int i = 0; i < arguments.size(); i++) {
            ReadFunction<?> function = arguments.get(i);
            try {
                // Compute the new value for the ith function.
                // If the value changed, reset the exception
                result.add(function.readValue());
                if (result.get(i) != previousValues.get(i)) {
                    exceptions.set(i, null);
                }
            } catch (Exception ex) {
                // Computation of value failed. Leave last value
                // and update exception
                exceptions.set(i, ex);
            }
        }
        previousValues = result;
        return result;
    }

    List<ReadFunction<?>> getArguments() {
        return arguments;
    }
    
    List<Exception> getExceptions() {
        return exceptions;
    }
    
    List<Object> getPreviousValues() {
        return previousValues;
    }
    
}
