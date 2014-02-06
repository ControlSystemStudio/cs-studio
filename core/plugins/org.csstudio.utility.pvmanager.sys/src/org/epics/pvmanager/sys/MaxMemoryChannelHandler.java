/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sys;

import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
class MaxMemoryChannelHandler extends SystemChannelHandler {

    public MaxMemoryChannelHandler(String channelName) {
        super(channelName);
    }

    @Override
    protected Object createValue() {
        return newVDouble(bytesToMebiByte(Runtime.getRuntime().maxMemory()), alarmNone(), timeNow(), memoryDisplay);
    }
    
}
