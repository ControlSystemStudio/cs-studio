/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.beast;

import org.epics.pvmanager.jms.beast.*;


/**
 *
 * @author carcassi
 */
public interface BeastTypeRegistry {

    /**
     * Given the introspection information of a pv and the type desired, returns
     * the matching converter to create a desired type from the given pvField.
     *
     * @param <T> the normative type
     * @param pvField the connection information
     * @param desiredType the desired normative type; can be null
     * @return null if no match is found
     */
    public <T> BeastTypeConverter<? extends T> findConverter(Class<T> givenType, Class<T> desiredType);

    /**
     * Given a normative type and the pvData serialization, returns a matching
     * converter to fill pvData structures from the given normative type.
     *
     * @param <T> the normative type
     * @param type the normative type to convert
     * @param pvField the desired pvData serialization; can be null
     * @return null if no match is found
     */
    //public <T> JMSTypeConverter<? extends T> findConverter(Class<T> type, Class<T> desiredType);
}
