/*
 * Copyright 2010-11 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager;

import org.epics.pvmanager.util.TimeStamp;

/**
 * Strategy class that extract time information from a given type.
 *
 * @param <T> type for which to add time support
 * @author carcassi
 */
public abstract class TimeSupport<T> extends TypeSupport<T> {

    /**
     * Creates a new support for extracting time information from the given
     * class.
     *
     * @param clazz the type on which to add support
     */
    public TimeSupport(Class<T> clazz) {
        super(clazz, TimeSupport.class);
    }
    
    /**
     * Extracts the TimeStamp of the value using the appropriate type support.
     *
     * @param <T> the type of the value
     * @param value the value from which to extract the timestamp
     * @return the extracted timestamp
     */
    public static <T> TimeStamp timestampOf(final T value) {
        @SuppressWarnings("unchecked")
        Class<T> typeClass = (Class<T>) value.getClass();
        TimeSupport<T> support = (TimeSupport<T>) findTypeSupportFor(TimeSupport.class,
                                                                                 typeClass);
        return support.extractTimestamp(value);
    }
    
    /**
     * Extracts the time information from the given object.
     *
     * @param object object on which to extract time
     * @return the time of the object or null if not available
     */
    protected abstract TimeStamp extractTimestamp(T object);
}
