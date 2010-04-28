package org.csstudio.alarm.table;

import java.util.HashMap;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.table.dataModel.AlarmMessageList;
import org.csstudio.alarm.table.dataModel.IMessageListService;
import org.csstudio.alarm.table.dataModel.LogMessageList;
import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.alarm.table.jms.AlarmConnectionMonitor;
import org.csstudio.alarm.table.jms.AlarmListener;
import org.csstudio.alarm.table.preferences.TopicSet;

public class MessageListService implements IMessageListService {
    
    /**
     * The connection for the messages.
     */
    private IAlarmConnection _alarmConnection;
    
    /**
     * Mapping of TopicSet and related log message list.
     */
    private final HashMap<String, LogMessageList> _logMessageMap = new HashMap<String, LogMessageList>();
    
    /**
     * Mapping of TopicSet and related alarm message list.
     */
    private final HashMap<String, AlarmMessageList> _alarmMessageMap = new HashMap<String, AlarmMessageList>();
    
    @Override
    public AlarmMessageList getAlarmMessageList(final TopicSet topicSet) {
        if (_alarmMessageMap.get(topicSet.getName()) == null) {
            initializeAlarmMessageList(topicSet);
        }
        return _alarmMessageMap.get(topicSet.getName());
    }
    
    /**
     * If there is no list for this topicSet, create a new one and initialize the JMSReceiver.
     * 
     * @param topicSet
     */
    public void initializeAlarmMessageList(final TopicSet topicSet) {
        AlarmMessageList messageList = new AlarmMessageList();
        
        disconnectIfConnected();
        createConnectionAndConnectWithListener(messageList);
        
        // JmsAlarmMessageReceiver jmsMessageReceiver = new JmsAlarmMessageReceiver();
        // jmsMessageReceiver.initializeJMSConnection(topicSet.getTopics(), messageList);
        _alarmMessageMap.put(topicSet.getName(), messageList);
    }
    
    @Override
    public LogMessageList getLogMessageList(final TopicSet topicSet,
                                            final Integer maximumMessageNumber) {
        if (_logMessageMap.get(topicSet.getName()) == null) {
            initializeLogMessageList(topicSet, maximumMessageNumber);
        }
        return _logMessageMap.get(topicSet.getName());
    }
    
    /**
     * If there is no list for this topicSet, create a new one and initialize the JMSReceiver.
     * 
     * @param topicSet
     * @param maximumMessageNumber
     */
    public void initializeLogMessageList(final TopicSet topicSet, final Integer maximumMessageNumber) {
        LogMessageList messageList = new LogMessageList(maximumMessageNumber);
        
        disconnectIfConnected();
        createConnectionAndConnectWithListener(messageList);
        
        // JmsMessageReceiver jmsMessageReceiver = new JmsMessageReceiver();
        // jmsMessageReceiver.initializeJMSConnection(topicSet.getTopics(), messageList);
        _logMessageMap.put(topicSet.getName(), messageList);
    }
    
    private void disconnectIfConnected() {
        // TODO jp The old version checked for _listenerSession.isActive
        if (_alarmConnection != null) {
            _alarmConnection.disconnect();
        }
    }
    
    /**
     * The connection to the message system will be created and immediately connected.
     * 
     * @param messageList this list is set to the listener as the destination for the messages
     */
    private void createConnectionAndConnectWithListener(final MessageList messageList) {
        _alarmConnection = JmsLogsPlugin.getDefault().getAlarmService().newAlarmConnection();
        try {
            AlarmListener alarmListener = null;
            _alarmConnection.connectWithListener(new AlarmConnectionMonitor(), alarmListener);
            alarmListener.setMessageList(messageList);
        } catch (AlarmConnectionException e) {
            // TODO jp error handling when connection fails
            e.printStackTrace();
        }
    }
    
    @Override
    public void disconnect() {
        _alarmConnection.disconnect();
    }
    
}
