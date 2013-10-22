/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory All rights reserved. Use
 * is subject to license terms.
 */
package org.epics.pvmanager.jms.adapters;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author msekoranja
 *
 */
public class TextMessageToString {

    protected final String value;

    /**
     * @param pvField
     * @param disconnected
     */
    public TextMessageToString(TextMessage message, boolean disconnected) throws JMSException {


        String textMessage = message.getText();
        value = textMessage;
    }

    /* (non-Javadoc)
     * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
     */
    public String getValue() {
        return value;
    }
}
