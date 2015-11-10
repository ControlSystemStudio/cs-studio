package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.timeNow;
import static org.diirt.vtype.ValueFactory.newVBoolean;
import static org.diirt.vtype.ValueFactory.newVString;
import static org.diirt.vtype.ValueFactory.newVTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import org.diirt.datasource.DataSourceTypeAdapterSet;
import org.diirt.datasource.ValueCache;
import org.diirt.vtype.VTable;

import static org.diirt.vtype.table.VTableFactory.*;
import static org.diirt.vtype.ValueFactory.*;

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
                System.out.println(" VString ADAPTER:" + message.getMessage().toString());
                if(filter(message, connection.getFilter())){
                    cache.writeValue(newVString(message.getMessage().toString(), 
                            alarmNone(),
                            timeNow()));
                    return true;
                } else {
                    return false;
                }
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
            public boolean updateCache(ValueCache cache,
                    BeastConnectionPayload connection,
                    BeastMessagePayload message) {
                System.out.println("VBoolean ADAPTER:" + message.getMessage().toString());
                if (filter(message, connection.getFilter())) {
                    boolean value = false;
                    String ack;
                    try {
                        if (message.getMessage() instanceof MapMessage) {
                            MapMessage mapMessage = (MapMessage) message.getMessage();
                            ack = mapMessage.getString("TEXT");
                            if (ack.equals("ACK")) {
                                value = true;
                            } else if (ack.equals("UNACK")) {
                                value = false;
                            }
                        } else if (message.getMessage() instanceof TextMessage) {
                            String text = ((TextMessage) message.getMessage()).getText();
                            if (text.contains("UNACK")) {
                                value = false;
                            } else if (text.contains("ACK")) {
                                value = true;
                            }
                        } else {
                            return false;
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                    cache.writeValue(newVBoolean(value, alarmNone(), timeNow()));
                    return true;
                } else {
                    return false;
                }
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
                System.out.println("VTable ADAPTER:" + message.getMessage().toString());
                // cache.writeValue(newVTable(types, names, values);
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
                                System.out.println(key + ":"
                                        + map.getString(key));
                            }
                            VTable table = newVTable(
                                    column("Key",
                                            newVStringArray(keys, alarmNone(),
                                                    timeNow())),
                                    column("Value",
                                            newVStringArray(values,
                                                    alarmNone(), timeNow())));
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
        });
    }

    @Override
    public Collection<BeastTypeAdapter> getAdapters() {
        return beastTypeAdapter;
    }

    private boolean filter(BeastMessagePayload message, String filter){
        try {
            if(filter == null || filter.isEmpty()){
//                filter = "(.*)";
                return true;
            }
            if (message.getMessage() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getMessage();
                return textMessage.getText().matches(filter);
            } else if (message.getMessage() instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message.getMessage();
                for (Enumeration<String> e = mapMessage.getMapNames(); e.hasMoreElements();){
                    String key = e.nextElement();
                    System.out.println(key + ":" +mapMessage.getString(key));
                }
                String name = mapMessage.getString("NAME");
                if(name == null || name.isEmpty()){
                    return false;
                }
                return name.matches(filter);
            } else {
                System.out.println("SimpleTopicConsumer - Received: " + message);
                return message.getMessage().toString().matches(filter);
            }
        } catch (JMSException e) {
            return false;
        }
    }
}
