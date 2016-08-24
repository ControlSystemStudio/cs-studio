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
public class BeastActiveAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastActiveAdapter.class.getName());

    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        if (connection.getType().equalsIgnoreCase(Messages.Active))
            return 1;
        return 0;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        log.fine(Messages.Active +" ADAPTER:" + message.toString());

        VBoolean active = newVBoolean(message.isActive(), alarmNone(), timeNow());
        cache.writeValue(active);
        return true;
    }
}
