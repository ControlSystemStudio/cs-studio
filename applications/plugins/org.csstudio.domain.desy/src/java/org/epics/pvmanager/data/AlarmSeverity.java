/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

/**
 * Severity of an alarm.
 * <p>
 * One should always bear in mind that the alarm severity of the IOC is set on
 * the record, and not on the individual channel. If you one is not connecting
 * to the value field of the record, the severity does not reflect the state
 * of that field.
 * <p>
 * For example: a record may be INVALID meaning that the value of the field
 * was not correctly read by the hardware; if one connects to the display limit
 * field, the value of that field will still be ok, but the alarm severity (if
 * requested) would say INVALID.
 *
 * @author carcassi
 */
public enum AlarmSeverity {
    
    /**
     * The current value is valid, and there are no alarm.
     */
    NONE,
    
    /**
     * There is a minor problem with the value: the exact meaning is defined
     * by the PV, but typically this means that the value is valid and is
     * outside some working range.
     */
    MINOR,

    /**
     * There is a major problem with the value: the exact meaning is defined
     * by the PV, but typically this means that the value is valid and is
     * outside some working range.
     */
    MAJOR,
    
    /**
     * There is a major problem with the value itself: the exact meaning is defined
     * by the PV, but typically this means that the returned value is not a
     * real representation of the actual value.
     */
    INVALID,

    /**
     * The record cannot be read and its state is undefined: the exact meaning is defined
     * by the PV, but typically this means that the client is either disconnected
     * or connected with no read access. The value is either stale or invalid.
     */
    UNDEFINED;
}
