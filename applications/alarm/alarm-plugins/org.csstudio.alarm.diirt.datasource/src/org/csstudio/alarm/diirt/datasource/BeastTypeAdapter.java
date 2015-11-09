package org.csstudio.alarm.diirt.datasource;

import org.diirt.datasource.DataSourceTypeAdapter;
import org.diirt.datasource.ValueCache;

import static org.diirt.vtype.ValueFactory.*;

public class BeastTypeAdapter implements DataSourceTypeAdapter<BeastConnectionPayload, BeastMessagePayload>{

    @Override
    public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
        System.out.println("check Match");
        return 1;
    }

    @Override
    public Object getSubscriptionParameter(ValueCache<?> cache, BeastConnectionPayload connection) {
        return null;
    }

    @Override
    public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
        // TODO Auto-generated method stub
        System.out.println("ADAPTER:" + message.getMessage().toString());
        cache.writeValue(newVString(message.getMessage().toString(), alarmNone(), timeNow()));
        return true;
    }

}
