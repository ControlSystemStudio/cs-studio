/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sys;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import static org.epics.vtype.ValueFactory.*;

/**
 *
 * @author carcassi
 */
class HostnameChannelHandler extends SystemChannelHandler {
    
    private String previousValue = null;

    public HostnameChannelHandler(String channelName) {
        super(channelName);
    }

    @Override
    protected Object createValue() {
        String hostname;
        Alarm alarm;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
            alarm = alarmNone();
        } catch (UnknownHostException ex) {
            hostname = "Unknown host";
            alarm = newAlarm(AlarmSeverity.INVALID, "Undefined");
        }
        if (!Objects.equals(hostname, previousValue)) {
            previousValue = hostname;
            return newVString(hostname, alarm, timeNow());
        } else {
            return null;
        }
    }
    
}
