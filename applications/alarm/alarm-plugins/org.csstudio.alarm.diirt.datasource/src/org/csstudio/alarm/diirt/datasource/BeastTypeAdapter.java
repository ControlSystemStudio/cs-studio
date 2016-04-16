package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVString;
import static org.diirt.vtype.ValueFactory.timeNow;

import org.diirt.datasource.DataSourceTypeAdapter;
import org.diirt.datasource.ValueCache;

/**
 * TODO: with the new datasource, the return type is static and this class may
 * no longer be needed and could be refactored out.
 *
 * @author Kunal Shroff
 *
 */
public abstract class BeastTypeAdapter implements
        DataSourceTypeAdapter<BeastConnectionPayload, BeastMessagePayload> {

    @Override
    public Object getSubscriptionParameter(ValueCache<?> cache,
            BeastConnectionPayload connection) {
        return null;
    }

    @Override
    public boolean updateCache(ValueCache cache,
            BeastConnectionPayload connection, BeastMessagePayload message) {
        // TODO Auto-generated method stub
        System.out.println("ADAPTER:" + message.toString());
        cache.writeValue(newVString(message.toString(), alarmNone(), timeNow()));
        return true;
    }

}
