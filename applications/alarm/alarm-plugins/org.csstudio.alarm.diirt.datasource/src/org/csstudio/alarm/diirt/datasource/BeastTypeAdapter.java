package org.csstudio.alarm.diirt.datasource;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import org.diirt.datasource.DataSourceTypeAdapter;
import org.diirt.datasource.ValueCache;

import static org.diirt.vtype.ValueFactory.*;

public abstract class BeastTypeAdapter implements DataSourceTypeAdapter<BeastConnectionPayload, BeastMessagePayload>{

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

    protected boolean filter(BeastMessagePayload message, String filter){
        try {
            if(filter == null || filter.isEmpty()){
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
