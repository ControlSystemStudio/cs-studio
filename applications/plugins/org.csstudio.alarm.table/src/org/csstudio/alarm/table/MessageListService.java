package org.csstudio.alarm.table;

import java.util.HashMap;

import org.csstudio.alarm.table.dataModel.AlarmMessageList;
import org.csstudio.alarm.table.dataModel.IMessageListService;
import org.csstudio.alarm.table.dataModel.LogMessageList;
import org.csstudio.alarm.table.jms.JmsAlarmMessageReceiver;
import org.csstudio.alarm.table.jms.JmsMessageReceiver;
import org.csstudio.alarm.table.preferences.TopicSet;

public class MessageListService implements IMessageListService {

	/**
	 * Mapping of TopicSet and related log message list.
	 */
	private HashMap<String, LogMessageList> _logMessageMap = new HashMap<String, LogMessageList>();

	/**
	 * Mapping of TopicSet and related alarm message list.
	 */
	private HashMap<String, AlarmMessageList> _alarmMessageMap = new HashMap<String, AlarmMessageList>();

	@Override
	public AlarmMessageList getAlarmMessageList(TopicSet topicSet) {
		if (_alarmMessageMap.get(topicSet.getName()) == null) {
			initializeAlarmMessageList(topicSet);
		}
		return _alarmMessageMap.get(topicSet.getName());
	}

	/**
	 * If there is no list for this topicSet, create a new one and initialize
	 * the JMSReceiver.
	 * 
	 * @param topicSet
	 */
	private void initializeAlarmMessageList(TopicSet topicSet) {
		AlarmMessageList messageList = new AlarmMessageList();
		JmsAlarmMessageReceiver jmsMessageReceiver = new JmsAlarmMessageReceiver();
		jmsMessageReceiver.initializeJMSConnection(topicSet.getTopics(),
				messageList);
		_alarmMessageMap.put(topicSet.getName(), messageList);
	}


	@Override
	public LogMessageList getLogMessageList(TopicSet topicSet,
			Integer maximumMessageNumber) {
		if (_logMessageMap.get(topicSet.getName()) == null) {
			initializeLogMessageList(topicSet, maximumMessageNumber);
		}
		return _logMessageMap.get(topicSet.getName());
	}

	/**
	 * If there is no list for this topicSet, create a new one and initialize
	 * the JMSReceiver.
	 * 
	 * @param topicSet
	 * @param maximumMessageNumber 
	 */
	private void initializeLogMessageList(TopicSet topicSet, Integer maximumMessageNumber) {
		LogMessageList messageList = new LogMessageList(maximumMessageNumber);
		JmsMessageReceiver jmsMessageReceiver = new JmsMessageReceiver();
		jmsMessageReceiver.initializeJMSConnection(topicSet.getTopics(),
				messageList);
		_logMessageMap.put(topicSet.getName(), messageList);
	}
}
