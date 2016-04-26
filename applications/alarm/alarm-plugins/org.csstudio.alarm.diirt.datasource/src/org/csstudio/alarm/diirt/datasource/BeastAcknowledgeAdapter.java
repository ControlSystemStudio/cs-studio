/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVBoolean;
import static org.diirt.vtype.ValueFactory.timeNow;

import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.diirt.datasource.ValueCache;
import org.diirt.vtype.VBoolean;

/**
 * @author Kunal Shroff
 *
 */
public class BeastAcknowledgeAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastAcknowledgeAdapter.class.getName());

    private static Collection<String> ackMesssages = Arrays.asList(
            org.csstudio.alarm.beast.Messages.SeverityLevel_INVALID_ACK,
            org.csstudio.alarm.beast.Messages.SeverityLevel_MAJOR_ACK,
            org.csstudio.alarm.beast.Messages.SeverityLevel_MINOR_ACK,
            org.csstudio.alarm.beast.Messages.SeverityLevel_UNDEFINED_ACK);
    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        if (connection.getType().equalsIgnoreCase(Messages.Acknowledge))
            return 1;
        return 0;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        log.fine(Messages.Acknowledge +" ADAPTER:" + message.toString());
        VBoolean acknowledge = newVBoolean(ackMesssages.contains(message.getAlarmSeverity()), alarmNone(), timeNow());
        cache.writeValue(acknowledge);
        return true;
    }
}
