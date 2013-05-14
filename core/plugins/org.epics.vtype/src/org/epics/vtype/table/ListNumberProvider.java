/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype.table;

import org.epics.util.array.ListNumber;

/**
 *
 * @author carcassi
 */
public abstract class ListNumberProvider {
    
    private final Class<?> type;

    public ListNumberProvider(Class<?> type) {
        this.type = type;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public abstract ListNumber createListNumber(int size);
}
