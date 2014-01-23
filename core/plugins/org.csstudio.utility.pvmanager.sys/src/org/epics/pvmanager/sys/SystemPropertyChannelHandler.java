/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sys;

import java.util.Objects;
import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
class SystemPropertyChannelHandler extends SystemChannelHandler {
    
    private final String propertyName;
    static final String PREFIX = "system.";
    private String previousValue = null;

    public SystemPropertyChannelHandler(String channelName) {
        super(channelName);
        propertyName = channelName.substring(PREFIX.length());
    }

    @Override
    protected Object createValue() {
        String value = System.getProperty(propertyName);
        if (value == null) {
            value = "";
        }
        if (!Objects.equals(value, previousValue)) {
            previousValue = value;
            return newVString(value, alarmNone(), timeNow());
        } else {
            return null;
        }
    }
    
}
