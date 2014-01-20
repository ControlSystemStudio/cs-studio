/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVFloatArray extends IVNumeric implements VFloatArray {

    private final float[] array;
    private final ListFloat data;
    private final ListInt sizes;

    public IVFloatArray(ListFloat data, ListInt sizes,
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
    public ListFloat getData() {
        return data;
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay() {
        return ValueUtil.defaultArrayDisplay(this);
    }

}
