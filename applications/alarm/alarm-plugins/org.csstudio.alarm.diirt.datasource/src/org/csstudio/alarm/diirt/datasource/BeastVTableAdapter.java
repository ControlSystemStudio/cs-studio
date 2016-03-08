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
        return 1;
    }

    @Override
    public boolean updateCache(ValueCache cache,
            BeastConnectionPayload connection, BeastMessagePayload message) {
        log.fine("VTable ADAPTER:" + message.toString());
        // if (filter(message, connection.getFilter())) {
        // try {
        // if (message.getMessage() instanceof MapMessage) {
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();

        keys.add("Name");
        values.add(message.getName());

        keys.add("AlarmStatus");
        values.add(message.getAlarmStatus());

        keys.add("CurrentStatus");
        values.add(message.getCurrentState());

        keys.add("Active");
        values.add(String.valueOf(message.isActive()));

        keys.add("Description");
        values.add(message.getDescription());

        keys.add("Value");
        values.add(message.getValue());

        keys.add("Enable");
        values.add(String.valueOf(message.getEnable()));

        keys.add("Type");
        values.add(message.getType());

        keys.add("AlarmPVsCount");
        values.add(String.valueOf(message.getAlarmsCount()));

        VTable table = newVTable(
                column("Key", newVStringArray(keys, alarmNone(), timeNow())),
                column("Value", newVStringArray(values, alarmNone(), timeNow())));
        cache.writeValue(table);
        return true;
    }
}
