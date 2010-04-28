package org.csstudio.alarm.table.dataModel;

import org.csstudio.alarm.table.preferences.TopicSet;

/**
 * Service to get message lists as a model for message tables. The service handles also the jms
 * connection. Don't forget to disconnect.
 * 
 * @author jhatje
 * 
 */
public interface IMessageListService {
    
    /**
     * Get log message list for given {@link TopicSet}.
     * 
     * @return
     */
    public LogMessageList getLogMessageList(TopicSet topicSet, Integer maximumMessageNumber);
    
    /**
     * Get alarm message list for given {@link TopicSet}.
     * 
     * @return
     */
    public AlarmMessageList getAlarmMessageList(TopicSet topicSet);
    
    /**
     * Initialize a list for given {@link TopicSet}.
     * 
     * @param topicSet
     */
    public void initializeAlarmMessageList(TopicSet topicSet);
    
    /**
     * Initialize a list for given {@link TopicSet}.
     * 
     * @param topicSet
     * @param maximumMessageNumber
     */
    public void initializeLogMessageList(TopicSet topicSet, Integer maximumMessageNumber);
    
    /**
     * This service maintains the state of the connection. The connection must be disconnected to
     * free resources.
     */
    public void disconnect();
}
