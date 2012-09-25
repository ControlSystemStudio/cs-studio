/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A function that takes a map and writes the value to a different expression
 *
 * @author carcassi
 */
class MapOfWriteFunction<T> extends WriteFunction<Map<String, T>> {
    
    private Map<String, WriteFunction<T>> functionMap;

    public MapOfWriteFunction(List<String> names, List<WriteFunction<T>> functions) {
        if (names.size() != functions.size()) {
            throw new IllegalArgumentException("The number of names and functions must be the same.");
        }
        if (new HashSet<String>(names).size() != names.size()) {
            throw new IllegalArgumentException("The names for the map must be all different.");
        }
        
        functionMap = new HashMap<String, WriteFunction<T>>();
        
        for (int nFunction = 0; nFunction < functions.size(); nFunction++) {
            functionMap.put(names.get(nFunction), functions.get(nFunction));
        }
    }


    @Override
    public void setValue(Map<String, T> newValue) {
        for (Map.Entry<String, T> entry : newValue.entrySet()) {
            WriteFunction<T> function = functionMap.get(entry.getKey());
            if (function != null) {
                function.setValue(entry.getValue());
            }
        }
    }

}
