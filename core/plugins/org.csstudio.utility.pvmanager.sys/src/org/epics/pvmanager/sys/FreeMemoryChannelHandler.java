/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.sys;

import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
class FreeMemoryChannelHandler extends SystemChannelHandler {

    public FreeMemoryChannelHandler(String channelName) {
        super(channelName);
    }

    @Override
    protected Object createValue() {
        return newVDouble(bytesToMebiByte(Runtime.getRuntime().freeMemory()), alarmNone(), timeNow(), memoryDisplay);
    }
    
}
