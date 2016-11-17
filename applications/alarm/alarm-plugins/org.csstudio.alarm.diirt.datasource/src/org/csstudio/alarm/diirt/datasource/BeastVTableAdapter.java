/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVStringArray;
import static org.diirt.vtype.ValueFactory.timeNow;
import static org.diirt.vtype.table.VTableFactory.column;
import static org.diirt.vtype.table.VTableFactory.newVTable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.diirt.datasource.ValueCache;
import org.diirt.vtype.VTable;

/**
 * @author Kunal Shroff
 *
 */
public class BeastVTableAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastVTableAdapter.class.getName());

    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        if (connection.getType().equalsIgnoreCase("Default"))
            return 1;
        return 0;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        log.fine("VTable ADAPTER:" + message.toString());

        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();

        keys.add(Messages.Name);
        values.add(message.getName());

        keys.add(Messages.AlarmSeverity);
        values.add(message.getAlarmSeverity());

        keys.add(Messages.CurrentSeverity);
        values.add(message.getCurrentSeverity());

        keys.add(Messages.CurrentStatus);
        values.add(message.getCurrentMessage());

        keys.add(Messages.Active);
        values.add(String.valueOf(message.isActive()));

        keys.add(Messages.AlarmState);
        values.add(message.getDescription());

        keys.add(Messages.Value);
        values.add(message.getValue());

        keys.add(Messages.Enable);
        values.add(String.valueOf(message.getEnable()));

        keys.add(Messages.Type);
        values.add(message.getType());

        keys.add(Messages.AlarmCount);
        values.add(String.valueOf(message.getAlarmsCount()));

        keys.add(Messages.Time);
        values.add(message.getTime());

        VTable table = newVTable(
                column("Key", newVStringArray(keys, alarmNone(), timeNow())),
                column("Value", newVStringArray(values, alarmNone(), timeNow())));
        cache.writeValue(table);
        return true;
    }
}
