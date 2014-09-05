/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.ListBoolean;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVBooleanArray extends IVMetadata implements VBooleanArray {
    
    private final ListInt sizes;
    private final ListBoolean data;

    public IVBooleanArray(ListBoolean data, ListInt sizes, Alarm alarm, Time time) {
        super(alarm, time);
        this.data = data;
        this.sizes = sizes;
    }

    @Override
    public ListBoolean getData() {
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
