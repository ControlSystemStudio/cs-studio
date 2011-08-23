/*
 * Copyright 2011 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.List;

/**
 *
 * @author carcassi
 */
class IVTable implements VTable {
    
    private final List<Class<?>> types;
    private final List<String> names;
    private final List<Object> values;

    public IVTable(List<Class<?>> types, List<String> names, List<Object> values) {
        this.types = types;
        this.names = names;
        this.values = values;
    }

    @Override
    public int getColumnCount() {
        return names.size();
    }

    @Override
    public Class<?> getColumnType(int column) {
        return types.get(column);
    }

    @Override
    public String getColumnName(int column) {
        return names.get(column);
    }

    @Override
    public Object getColumnArray(int column) {
        return values.get(column);
    }
    
}
