/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sys;

import org.epics.util.time.Timestamp;
import org.epics.util.time.TimestampFormat;
import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
class TimeChannelHandler extends SystemChannelHandler {

    private static final TimestampFormat timeFormat = new TimestampFormat("yyyy/MM/dd hh:mm:ss.SSS");    

    public TimeChannelHandler(String channelName) {
        super(channelName);
    }

    @Override
    protected Object createValue() {
        Timestamp time = Timestamp.now();
        String formatted = timeFormat.format(time);
        return newVString(formatted, alarmNone(), newTime(time));
    }
    
}
