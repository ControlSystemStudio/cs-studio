/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.vtype;

import java.util.List;

/**
 * Immutable VMultiDouble implementation.
 *
 * @author carcassi
 */
class IVMultiDouble extends IVNumeric implements VMultiDouble {
    
    private final List<VDouble> values;

    IVMultiDouble(List<VDouble> values, Alarm alarm, Time time, Display display) {
        super(alarm, time, display);
        this.values = values;
    }

    @Override
    public List<VDouble> getValues() {
        return values;
    }

}
