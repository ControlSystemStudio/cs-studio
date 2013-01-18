/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVDoubleArray extends IVNumeric implements VDoubleArray {

    private final double[] array;
    private final ListDouble data;
    private final ListInt sizes;

    public IVDoubleArray(ListDouble data, ListInt sizes,
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
    public ListDouble getData() {
        return data;
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
