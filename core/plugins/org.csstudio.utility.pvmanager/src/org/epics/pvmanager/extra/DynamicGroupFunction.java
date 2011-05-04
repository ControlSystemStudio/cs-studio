/*
 * Copyright 2008-2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.extra;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.epics.pvmanager.Function;

/**
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
                result.add(function.getValue());
                if (result.get(i) != previousValues.get(i)) {
                    exceptions.set(i, null);
                }
            } catch (Exception ex) {
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
