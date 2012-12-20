/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author carcassi
 */
class ConnectionCollector extends Collector<Boolean> {
    
    private final List<String> names;
    private final List<Function<Boolean>> caches;
    private Boolean connected;
    
    public ConnectionCollector(Map<String, ? extends Function<Boolean>> map) {
        this.names = new ArrayList<String>(map.keySet());
        this.caches = new ArrayList<Function<Boolean>>();
        for (String name : names) {
            this.caches.add(map.get(name));
        }
    }

    @Override
    public synchronized void collect() {
        connected = null;
    }

    @Override
    public synchronized List<Boolean> getValue() {
        if (connected == null) {
            List<Boolean> connections = new ArrayList<Boolean>();
            for (Function<Boolean> func : caches) {
                connections.add(func.getValue());
            }

            connected = calculate(names, connections);
        }
        
        return Collections.singletonList(connected);
    }
    
    protected boolean calculate(List<String> names, List<Boolean> connections) {
        for (Boolean conn : connections) {
            if (conn != Boolean.TRUE) {
                return false;
            }
        }
        return true;
    }
    
}
