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
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.jms.MapMessage;

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
        if (connection.getReadType().equals("VTable"))
            return 1;
        else
            return 0;
    }

    @Override
    public boolean updateCache(ValueCache cache,
            BeastConnectionPayload connection, BeastMessagePayload message) {
        log.info("VTable ADAPTER:" + message.getMessage().toString());
        if (filter(message, connection.getFilter())) {
            try {
                if (message.getMessage() instanceof MapMessage) {
                    List<String> keys = new ArrayList<String>();
                    List<String> values = new ArrayList<String>();
                    MapMessage map = (MapMessage) message.getMessage();
                    for (Enumeration<String> e = map.getMapNames(); e
                            .hasMoreElements();) {
                        String key = e.nextElement();
                        keys.add(key);
                        values.add(map.getString(key) != null ? map
                                .getString(key) : "");
                        log.info(key + ":" + map.getString(key));
                    }
                    VTable table = newVTable(
                            column("Key",newVStringArray(keys, alarmNone(), timeNow())),
                            column("Value",newVStringArray(values, alarmNone(), timeNow())));
                    cache.writeValue(table);
                    return true;
                } else {
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
