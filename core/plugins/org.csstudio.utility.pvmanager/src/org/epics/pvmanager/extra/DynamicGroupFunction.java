/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.List;
import org.epics.pvmanager.Function;

/**
 * Function that implements the dynamic group.
 *
 * @author carcassi
 */
class DynamicGroupFunction extends Function<List<Object>> {
    
    // Guarded by this
    private final List<Function<?>> arguments = new ArrayList<Function<?>>();
    // Guarded by this
    private List<Exception> exceptions = new ArrayList<Exception>();
    // Gaurded by this
    private List<Object> previousValues = new ArrayList<Object>();

    @Override
    public synchronized List<Object> getValue() {
        List<Object> result = new ArrayList<Object>();
        for (int i = 0; i < arguments.size(); i++) {
            Function<?> function = arguments.get(i);
            try {
                // Compute the new value for the ith function.
                // If the value changed, reset the exception
                result.add(function.getValue());
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

    List<Function<?>> getArguments() {
        return arguments;
    }
    
    List<Exception> getExceptions() {
        return exceptions;
    }
    
    List<Object> getPreviousValues() {
        return previousValues;
    }
    
}
