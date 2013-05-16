/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype.table;

/**
 *
 * @author carcassi
 */
public abstract class Column {
    
    private final String name;
    private final Class<?> type;
    private final boolean generated;

    public Column(String name, Class<?> type, boolean generated) {
        this.name = name;
        this.type = type;
        this.generated = generated;
    }
    
    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
    
    public boolean isGenerated() {
        return generated;
    }
    
    public abstract Object getData(int size);
    
}
