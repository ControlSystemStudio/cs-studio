/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.adapters;


import javax.jms.JMSException;
import javax.jms.StreamMessage;
import org.epics.vtype.VInt;

/**
 * @author msekoranja
 *
 */
public class StreamMessageToInteger {

    protected final Integer value;

    /**
     * @param pvField
     * @param disconnected
     */
    public StreamMessageToInteger(StreamMessage message, boolean disconnected) throws JMSException {

        int intMessage = message.readInt();
        value = Integer.valueOf(intMessage);

    }

    /* (non-Javadoc)
     * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
     */
    public Integer getValue() {
        return value;
    }
}
