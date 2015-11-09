package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.timeNow;

import static org.diirt.vtype.ValueFactory.newVBoolean;
import static org.diirt.vtype.ValueFactory.newVString;
import static org.diirt.vtype.ValueFactory.newVTable;

import java.util.ArrayList;
import java.util.Collection;

import org.diirt.datasource.DataSourceTypeAdapterSet;
import org.diirt.datasource.ValueCache;

public class BeastTypeAdapterSet implements DataSourceTypeAdapterSet {

    private Collection<BeastTypeAdapter> beastTypeAdapter = new ArrayList<BeastTypeAdapter>();
    
    public BeastTypeAdapterSet() {
        beastTypeAdapter.add(new BeastTypeAdapter(){
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
                System.out.println("ADAPTER:" + message.getMessage().toString());
                cache.writeValue(newVString(message.getMessage().toString(), alarmNone(), timeNow()));
                return true;
            }
        });
        
        beastTypeAdapter.add(new BeastTypeAdapter(){
            @Override
            public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
                if(connection.getReadType().equals("VBoolean"))
                    return 1;
                else
                    return 0;
            }
            
            @Override
            public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
                // TODO Auto-generated method stub
                System.out.println("ADAPTER:" + message.getMessage().toString());
                cache.writeValue(newVBoolean(message.getMessage().toString().contains("ACK"), alarmNone(), timeNow()));
                return true;
            }
        });
        
        beastTypeAdapter.add(new BeastTypeAdapter(){
            @Override
            public int match(ValueCache<?> cache, BeastConnectionPayload connection) {
                if(connection.getReadType().equals("VTable"))
                    return 1;
                else
                    return 0;
            }
            
            @Override
            public boolean updateCache(ValueCache cache, BeastConnectionPayload connection, BeastMessagePayload message) {
                // TODO Auto-generated method stub
                System.out.println("ADAPTER:" + message.getMessage().toString());
   //             cache.writeValue(newVTable(types, names, values);
                return true;
            }
        });
    }
    
    @Override
    public Collection<BeastTypeAdapter> getAdapters() {
        return beastTypeAdapter;
    }

}
