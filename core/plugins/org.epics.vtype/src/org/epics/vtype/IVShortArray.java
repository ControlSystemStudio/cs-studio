/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListShort;

/**
 *
 * @author carcassi
 */
class IVShortArray extends IVNumberArray implements VShortArray {

    private final ListShort data;

    public IVShortArray(ListShort data, ListInt sizes,
            Alarm alarm, Time time, Display display) {
        this(data, sizes, null, alarm, time, display);
    }

    public IVShortArray(ListShort data, ListInt sizes, List<ArrayDimensionDisplay> dimDisplay,
            Alarm alarm, Time time, Display display) {
        super(sizes, dimDisplay, alarm, time, display);
        this.data = data;
    }

    @Override
    public ListShort getData() {
        return data;
    }

}
