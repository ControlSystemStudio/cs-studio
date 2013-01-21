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
 * A function that takes a set of inputs and transforms them in a new map.
 *
 * @author carcassi
 */
class MapOfFunction<T> extends Function<Map<String, T>> {

    private List<Function<T>> functions;
    private List<String> names;

    public MapOfFunction(List<String> names, List<Function<T>> functions) {
        if (names.size() != functions.size()) {
            throw new IllegalArgumentException("The number of names and functions must be the same.");
        }
        if (new HashSet<String>(names).size() != names.size()) {
            throw new IllegalArgumentException("The names for the map must be all different.");
        }
        this.functions = functions;
        this.names = names;
    }

    @Override
    public Map<String, T> getValue() {
        Map<String, T> map = new HashMap<String, T>();
        for (int nFunction = 0; nFunction < names.size(); nFunction++) {
            T value = functions.get(nFunction).getValue();
            if (value!= null) {
                map.put(names.get(nFunction), value);
            }
        }
        if (map.isEmpty())
            return null;
        return map;
    }

}
