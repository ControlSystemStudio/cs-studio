/**
 *
 */
package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVString;
import static org.diirt.vtype.ValueFactory.timeNow;

import java.util.logging.Logger;

import org.diirt.datasource.ValueCache;

/**
 * @author Kunal Shroff
 *
 */
@Deprecated
public class BeastVStringAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastVStringAdapter.class.getName());

    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
            return 1;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        // TODO Auto-generated method stub
        log.info(" VString ADAPTER:" + message.toString());
            cache.writeValue(newVString(message.toString(),
                    alarmNone(),
                    timeNow()));
            return true;
    }
}
