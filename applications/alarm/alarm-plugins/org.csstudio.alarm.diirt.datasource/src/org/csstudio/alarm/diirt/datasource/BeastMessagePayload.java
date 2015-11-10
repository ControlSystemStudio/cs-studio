/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.csstudio.alarm.diirt.datasource;

import javax.jms.Message;

/**
 * @author Kunal Shroff
 *
 */
public class BeastMessagePayload {

    private final Message message;
    private final String filter;

    public BeastMessagePayload(Message message, String filter) {
        this.message = message;
        this.filter = filter;
    }

    public Message getMessage() {
        return message;
    }

    public String getFilter() {
        return filter;
    }

    
}
