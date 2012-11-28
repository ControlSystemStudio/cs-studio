/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.expression;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.epics.pvmanager.Function;
import org.epics.pvmanager.WriteFunction;

/**
 *
 * @author carcassi
 */
class MapUpdate<T> {
    
    private final Collection<String> expressionsToDelete;
    private final Map<String, Function<T>> readFunctionsToAdd;
    private final Map<String, WriteFunction<T>> writeFunctionsToAdd;
    private final boolean toClear;

    private MapUpdate(Collection<String> expressionsToDelete, Map<String, Function<T>> readFunctionsToAdd,
            Map<String, WriteFunction<T>> writeFunctionsToAdd, boolean toClear) {
        this.expressionsToDelete = expressionsToDelete;
        this.readFunctionsToAdd = readFunctionsToAdd;
        this.writeFunctionsToAdd = writeFunctionsToAdd;
        this.toClear = toClear;
    }

    public Collection<String> getExpressionsToDelete() {
        return expressionsToDelete;
    }

    public Map<String, Function<T>> getReadFunctionsToAdd() {
        return readFunctionsToAdd;
    }

    public Map<String, WriteFunction<T>> getWriteFunctionsToAdd() {
        return writeFunctionsToAdd;
    }

    public boolean isToClear() {
        return toClear;
    }
    
    public static <T> MapUpdate<T> clear() {
        return new MapUpdate<>(Collections.<String>emptyList(), Collections.<String, Function<T>>emptyMap(),
                Collections.<String, WriteFunction<T>>emptyMap(), true);
    }
    
    public static <T> MapUpdate<T> addReadFunction(String name, Function<T> function) {
        return new MapUpdate<>(Collections.<String>emptyList(), Collections.singletonMap(name, function),
                null, false);
    }
    
    public static <T> MapUpdate<T> addWriteFunction(String name, WriteFunction<T> function) {
        return new MapUpdate<>(Collections.<String>emptyList(), null,
                Collections.singletonMap(name, function), false);
    }
    
    public static <T> MapUpdate<T> removeFunction(String name) {
        return new MapUpdate<>(Collections.singleton(name), Collections.<String, Function<T>>emptyMap(),
                Collections.<String, WriteFunction<T>>emptyMap(), true);
    }
    
}
