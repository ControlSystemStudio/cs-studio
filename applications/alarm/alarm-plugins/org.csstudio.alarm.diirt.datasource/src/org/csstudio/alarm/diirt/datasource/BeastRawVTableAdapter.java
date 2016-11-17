/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.newAlarm;
import static org.diirt.vtype.ValueFactory.newVTable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.diirt.datasource.ValueCache;
import org.diirt.util.array.ArrayInt;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VTable;

/**
 * @author Kunal Shroff
 *
 */
public class BeastRawVTableAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastRawVTableAdapter.class.getName());
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        if (connection.getType().equalsIgnoreCase("RawTable"))
            return 1;
        return 0;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        log.fine("VTable ADAPTER:" + message.toString());

        List<Class<?>> types = new ArrayList<Class<?>>();
        List<String> names = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();

        // Name
        types.add(String.class);
        names.add(Messages.Name);
        values.add(Arrays.asList(message.getName()));

        // Alarm Serverity
        types.add(String.class);
        names.add(Messages.AlarmSeverity);
        values.add(Arrays.asList(message.getAlarmSeverity()));

        // Current alarm
        types.add(Alarm.class);
        names.add("CurrentAlarm");
        values.add(Arrays.asList(newAlarm(AlarmSeverity.valueOf(message.getCurrentSeverity()), message.getCurrentMessage())));

        // Active
        types.add(Boolean.TYPE);
        names.add(Messages.Active);
        values.add(Arrays.asList(message.isActive()));

        // Alarm State
        types.add(String.class);
        names.add(Messages.AlarmState);
        values.add(Arrays.asList(message.getDescription()));

        // Value
        types.add(String.class);
        names.add(Messages.Value);
        values.add(Arrays.asList(message.getValue()));

        // Enable
        types.add(Boolean.TYPE);
        names.add(Messages.Enable);
        values.add(Arrays.asList(message.getEnable()));

        // Type
        types.add(String.class);
        names.add(Messages.Type);
        values.add(Arrays.asList(message.getType()));

        // Alarm count
        types.add(Integer.TYPE);
        names.add(Messages.AlarmCount);
        values.add(new ArrayInt(message.getAlarmsCount()));

        // Time
        types.add(Instant.class);
        names.add(Messages.Time);
        values.add(Arrays
                .asList(LocalDateTime.parse(message.getTime(), format).atZone(ZoneId.systemDefault()).toInstant()));

        VTable table = newVTable(types, names, values);
        cache.writeValue(table);
        return true;
    }
}
