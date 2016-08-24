/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVString;
import static org.diirt.vtype.ValueFactory.timeNow;

import java.util.logging.Logger;

import org.diirt.datasource.ValueCache;
import org.diirt.vtype.VString;

/**
 * @author Kunal Shroff
 *
 */
public class BeastAlarmSeverityAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastAlarmSeverityAdapter.class.getName());

    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        if (connection.getType().equalsIgnoreCase(Messages.AlarmSeverity))
            return 1;
        return 0;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        log.fine(Messages.Active +" ADAPTER:" + message.toString());

        VString alarmSeverity = newVString(message.getAlarmSeverity(), alarmNone(), timeNow());
        cache.writeValue(alarmSeverity);
        return true;
    }
}
