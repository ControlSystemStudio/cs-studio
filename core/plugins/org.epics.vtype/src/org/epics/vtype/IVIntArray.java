/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVIntArray extends IVNumeric implements VIntArray {

    private final int[] array;
    private final ListInt data;
    private final ListInt sizes;

    public IVIntArray(ListInt data, ListInt sizes,
            Alarm alarm, Time time, Display display) {
        super(alarm, time, display);
        this.array = null;
        this.sizes = sizes;
        this.data = data;
    }

    @Override
    public ListInt getSizes() {
        return sizes;
    }

    @Override
    public ListInt getData() {
        return data;
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
