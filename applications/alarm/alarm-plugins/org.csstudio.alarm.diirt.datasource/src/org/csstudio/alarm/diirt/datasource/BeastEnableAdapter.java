/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVBoolean;
import static org.diirt.vtype.ValueFactory.timeNow;

import java.util.logging.Logger;

import org.diirt.datasource.ValueCache;
import org.diirt.vtype.VBoolean;

/**
 * @author Kunal Shroff
 *
 */
public class BeastEnableAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastEnableAdapter.class.getName());

    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        if (connection.getType().equalsIgnoreCase(Messages.Enable))
            return 1;
        return 0;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        log.fine(Messages.Enable +" ADAPTER:" + message.toString());
        
        VBoolean enable = newVBoolean(message.getEnable(), alarmNone(), timeNow());
        cache.writeValue(enable);
        return true;
    }
}
