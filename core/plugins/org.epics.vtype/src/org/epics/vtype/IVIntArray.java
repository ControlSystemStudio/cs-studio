/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVIntArray extends IVNumeric implements VIntArray {

    private final ListInt data;
    private final ListInt sizes;
    private final List<ArrayDimensionDisplay> dimensionDisplay;

    public IVIntArray(ListInt data, ListInt sizes,
            Alarm alarm, Time time, Display display) {
        this(data, sizes, null, alarm, time, display);
    }

    public IVIntArray(ListInt data, ListInt sizes, List<ArrayDimensionDisplay> dimDisplay,
            Alarm alarm, Time time, Display display) {
        super(alarm, time, display);
        this.sizes = sizes;
        this.data = data;
        if (dimDisplay == null) {
            this.dimensionDisplay = ValueUtil.defaultArrayDisplay(this);
        } else {
            this.dimensionDisplay = dimDisplay;
        }
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

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay() {
        return dimensionDisplay;
    }

}
