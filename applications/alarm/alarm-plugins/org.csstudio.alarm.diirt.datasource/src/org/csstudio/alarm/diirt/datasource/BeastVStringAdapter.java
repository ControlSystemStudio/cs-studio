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
public class BeastVStringAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastVStringAdapter.class.getName());
    
    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        if(connection.getReadType().equals("VString"))
            return 1;
        else
            return 0;
    }
    
    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        // TODO Auto-generated method stub
        log.info(" VString ADAPTER:" + message.getMessage().toString());
        if(filter(message, connection.getFilter())){
            cache.writeValue(newVString(message.getMessage().toString(), 
                    alarmNone(),
                    timeNow()));
            return true;
        } else {
            return false;
        }
    }
}
