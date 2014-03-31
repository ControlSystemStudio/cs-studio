/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.vtype;

import java.util.List;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListInt;

/**
 *
 * @author carcassi
 */
class IVByteArray extends IVNumberArray implements VByteArray {

    private final ListByte data;

    public IVByteArray(ListByte data, ListInt sizes,
            Alarm alarm, Time time, Display display) {
        this(data, sizes, null, alarm, time, display);
    }

    public IVByteArray(ListByte data, ListInt sizes, List<ArrayDimensionDisplay> dimDisplay,
            Alarm alarm, Time time, Display display) {
        super(sizes, dimDisplay, alarm, time, display);
        this.data = data;
    }

    @Override
    public ListByte getData() {
        return data;
    }

}
