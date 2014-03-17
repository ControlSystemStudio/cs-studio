/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.ReadFunction;
import org.epics.pvmanager.QueueCollector;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.epics.pvmanager.*;

/**
 * A function that takes a set of inputs and transforms them in a new map.
 *
 * @author carcassi
 */
class MapOfReadFunction<T> implements ReadFunction<Map<String, T>> {

    private final Map<String, ReadFunction<T>> functions = new HashMap<>();
    private final QueueCollector<MapUpdate<T>> mapUpdateCollector;
    private Map<String, T> previousValue;

    public MapOfReadFunction(QueueCollector<MapUpdate<T>> mapUpdateCollector) {
        this.mapUpdateCollector = mapUpdateCollector;
    }

    @Override
    public Map<String, T> readValue() {
        for (MapUpdate<T> mapUpdate : mapUpdateCollector.readValue()) {
            for (String name : mapUpdate.getExpressionsToDelete()) {
                functions.remove(name);
            }
            functions.putAll(mapUpdate.getReadFunctionsToAdd());
            previousValue = null;
        }
        
        Map<String, T> map = new HashMap<String, T>();
        for (Map.Entry<String, ReadFunction<T>> entry : functions.entrySet()) {
            String name = entry.getKey();
            T value = entry.getValue().readValue();
            if (value != null) {
                map.put(name, value);
            }
        }
        
        if (Objects.equals(previousValue, map)) {
            return previousValue;
        }
        
        previousValue = map;
        return map;
    }

    public QueueCollector<MapUpdate<T>> getMapUpdateCollector() {
        return mapUpdateCollector;
    }
    
}
