package org.csstudio.alarm.table.jms;

import javax.jms.MapMessage;

public interface ISendMapMessage {

    public void startSender(String topic) throws Exception;

    public void stopSender() throws Exception;

    public MapMessage getSessionMessageObject(String topic) throws Exception;

    public void sendMessage(String topic) throws Exception; 
}
