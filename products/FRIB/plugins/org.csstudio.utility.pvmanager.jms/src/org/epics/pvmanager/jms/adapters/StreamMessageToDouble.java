/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.adapters;

import javax.jms.JMSException;
import javax.jms.StreamMessage;
import org.epics.vtype.VDouble;

/**
 * @author msekoranja
 *
 */
public class StreamMessageToDouble {

    protected final Double value;

    /**
     * @param pvField
     * @param disconnected
     */
    public StreamMessageToDouble(StreamMessage message, boolean disconnected) throws JMSException {


        double doubleMessage = message.readDouble();
        value = Double.valueOf(doubleMessage);
    }

    /* (non-Javadoc)
     * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
     */
    public Double getValue() {
        return value;
    }
}
