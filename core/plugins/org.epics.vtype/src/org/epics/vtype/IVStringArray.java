/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVStringArray extends IVMetadata implements VStringArray {
    
    private final ListInt sizes;
    private final List<String> data;

    public IVStringArray(List<String> data, ListInt sizes, Alarm alarm, Time time) {
        super(alarm, time);
        this.data = data;
        this.sizes = sizes;
    }

    @Override
    public List<String> getData() {
        return data;
    }

    @Override
    public ListInt getSizes() {
        return sizes;
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }
    
}
