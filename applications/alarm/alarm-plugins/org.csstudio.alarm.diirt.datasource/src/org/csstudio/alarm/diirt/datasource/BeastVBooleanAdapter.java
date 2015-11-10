/**
 * 
 */
package org.csstudio.alarm.diirt.datasource;

import static org.diirt.vtype.ValueFactory.alarmNone;
import static org.diirt.vtype.ValueFactory.newVBoolean;
import static org.diirt.vtype.ValueFactory.timeNow;

import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

import org.diirt.datasource.ValueCache;

/**
 * @author Kunal Shroff
 *
 */
public class BeastVBooleanAdapter extends BeastTypeAdapter {

    private static Logger log = Logger.getLogger(BeastVStringAdapter.class.getName());
    
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
        log.info("VBoolean ADAPTER:" + message.getMessage().toString());
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

}
