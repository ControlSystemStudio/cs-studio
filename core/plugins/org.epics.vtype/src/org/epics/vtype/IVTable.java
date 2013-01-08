/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import java.util.List;

/**
 *
 * @author carcassi
 */
class IVTable implements VTable {
    
    private final List<Class<?>> types;
    private final List<String> names;
    private final List<Object> values;
    private final int rowCount;

    public IVTable(List<Class<?>> types, List<String> names, List<Object> values) {
        this.types = types;
        this.names = names;
        this.values = values;
        int maxCount = 0;
        for (Object array : values) {
            maxCount = Math.max(maxCount, getArraySize(array));
        }
        this.rowCount = maxCount;
    }
    
    private static int getArraySize(Object array) {
        if (array instanceof Object[]) {
            return ((Object[]) array).length;
        } else if (array instanceof int[]) {
            return ((int[]) array).length;
        } else if (array instanceof double[]) {
            return ((double[]) array).length;
        }
        
        throw new IllegalArgumentException("Object " + array + " is not an array");
    }

    @Override
    public int getColumnCount() {
        return names.size();
    }

    @Override
    public int getRowCount() {
        return rowCount;
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
