/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

/**
 * Immutable VInt implementation.
 *
 * @author carcassi
 */
class IVDouble extends IVNumeric implements VDouble {
    
    private final Double value;

    IVDouble(Double value, Alarm alarm, Time time, Display display) {
        super(alarm, time, display);
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
