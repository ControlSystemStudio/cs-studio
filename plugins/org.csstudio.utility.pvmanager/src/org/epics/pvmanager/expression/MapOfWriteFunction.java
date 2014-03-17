/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.expression;

import org.epics.pvmanager.WriteFunction;
import org.epics.pvmanager.QueueCollector;
import java.util.HashMap;
import java.util.Map;
import org.epics.pvmanager.*;

/**
 * A function that takes a map and writes the value to a different expression
 *
 * @author carcassi
 */
class MapOfWriteFunction<T> implements WriteFunction<Map<String, T>> {
    
    private final Map<String, WriteFunction<T>> functions = new HashMap<>();
    private final QueueCollector<MapUpdate<T>> mapUpdateCollector;

    public MapOfWriteFunction(QueueCollector<MapUpdate<T>> mapUpdateCollector) {
        this.mapUpdateCollector = mapUpdateCollector;
    }

    @Override
    public void writeValue(Map<String, T> newValue) {
        for (MapUpdate<T> mapUpdate : mapUpdateCollector.readValue()) {
            for (String name : mapUpdate.getExpressionsToDelete()) {
                functions.remove(name);
            }
            functions.putAll(mapUpdate.getWriteFunctionsToAdd());
        }
        
        for (Map.Entry<String, T> entry : newValue.entrySet()) {
            WriteFunction<T> function = functions.get(entry.getKey());
            if (function != null) {
                function.writeValue(entry.getValue());
            }
        }
    }

    public QueueCollector<MapUpdate<T>> getMapUpdateCollector() {
        return mapUpdateCollector;
    }

}
