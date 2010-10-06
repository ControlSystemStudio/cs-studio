/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import java.text.NumberFormat;

/**
 * Limit and unit information needed for display and control. The limits must
 * be given in terms of the same type of the scalar type, which needs
 * to be a number. The default for the ranges corresponds to the maximum range
 * for the type.
 * <p>
 * Note: NumberFormats. If there is no direct write on metadata,
 * number formats can be used to provide a default way to convert the value
 * to a String.
 * <p>
 *
 * @param <T> a {@link java.lang.Number} type
 * @author carcassi
 */
public interface Display<T extends Number> {

    /**
     * Lowest possible value to be displayed. Never null.
     *
     * @return lower display limit
     */
    @Metadata
    T getLowerDisplayLimit();

    /**
     * Lowest possible value (included). Never null.
     * @return lower limit
     */
    @Metadata
    T getLowerCtrlLimit();

    /**
     * Lowest value before the alarm region. Never null.
     *
     * @return lower alarm limit
     */
    @Metadata
    T getLowerAlarmLimit();

    /**
     * Lowest value before the warning region. Never null.
     *
     * @return lower warning limit
     */
    @Metadata
    T getLowerWarningLimit();

    /**
     * String representation of the units using for all values.
     * Never null. If not yet connected, returns the empty String.
     *
     * @return units
     */
    @Metadata
    String getUnits();

    /**
     * Returns a NumberFormat that creates a String with just the value (no units).
     * Format is locale independent and should be used for all values (values and
     * lower/upper limits). Never null. If not yet connected, the format always
     * returns an empty String.
     *
     * @return the default format for all values
     */
    @Metadata
    NumberFormat getFormat();

    /**
     * Highest value before the warning region. Never null.
     *
     * @return upper warning limit
     */
    @Metadata
    T getUpperWarningLimit();

    /**
     * Highest value before the alarm region. Never null.
     *
     * @return upper alarm limit
     */
    @Metadata
    T getUpperAlarmLimit();

    /**
     * Highest possible value (included). Never null.
     * @return upper limit
     */
    @Metadata
    T getUpperCtrlLimit();

    /**
     * Highest possible value to be displayed. Never null.
     *
     * @return upper display limit
     */
    @Metadata
    T getUpperDisplayLimit();
}
