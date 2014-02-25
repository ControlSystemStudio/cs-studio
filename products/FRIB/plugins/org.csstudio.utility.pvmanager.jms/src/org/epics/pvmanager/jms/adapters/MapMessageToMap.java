/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.adapters;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * @author msekoranja
 *
 */
public class MapMessageToMap {

    protected final Map value;

    /**
     * @param pvField
     * @param disconnected
     */
    public MapMessageToMap(MapMessage message, boolean disconnected) throws JMSException {

        value = new HashMap();
        Enumeration en = message.getMapNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            value.put(key, message.getObject(key));
        }

    }

    /* (non-Javadoc)
     * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
     */
    public Map getValue() {
        return value;
    }
}
