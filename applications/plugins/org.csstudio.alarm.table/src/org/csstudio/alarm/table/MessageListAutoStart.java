package org.csstudio.alarm.table;

import java.util.List;

import org.csstudio.alarm.table.dataModel.IMessageListService;
import org.csstudio.alarm.table.preferences.TopicSet;
import org.csstudio.alarm.table.preferences.TopicSetColumnService;
import org.csstudio.alarm.table.preferences.alarm.AlarmViewPreferenceConstants;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.platform.startupservice.IStartupServiceListener;

public class MessageListAutoStart implements IStartupServiceListener {

    private IMessageListService _messageListService;

    /**
     * This method is executed by the startup listener from platform at CSS startup. The preferences
     * are read for each table type and the message lists are started for each list with the
     * preference 'auto start' == true.
     */
    @Override
    public void run() {
        // TODO (jpenning) Currently disabled

        // ScopedPreferenceStore prefStore = new ScopedPreferenceStore(
        // new InstanceScope(), JmsLogsPlugin.getDefault().getBundle()
        // .getSymbolicName());
        // String maximumNumberOfMessagesPref = prefStore
        // .getString(LogViewPreferenceConstants.MAX);
        // _messageListService = JmsLogsPlugin.getDefault()
        // .getMessageListService();
        // startLogLists(maximumNumberOfMessagesPref);
        // startAlarmLists();
    }

    private void startLogLists(final String maximumNumberOfMessagesPref) {
        TopicSetColumnService topicSetColumnService = new TopicSetColumnService(LogViewPreferenceConstants.TOPIC_SET,
                                                                                LogViewPreferenceConstants.P_STRING);
        List<TopicSet> topicSets = topicSetColumnService.getTopicSets();
        for (TopicSet topicSet : topicSets) {
            if (topicSet.isStartUp()) {
                _messageListService.initializeLogMessageList(topicSet, Integer
                        .parseInt(maximumNumberOfMessagesPref));
            }
        }
    }

    private void startAlarmLists() {
        TopicSetColumnService topicSetColumnService = new TopicSetColumnService(AlarmViewPreferenceConstants.TOPIC_SET,
                                                                                AlarmViewPreferenceConstants.P_STRINGAlarm);
        List<TopicSet> topicSets = topicSetColumnService.getTopicSets();
        for (TopicSet topicSet : topicSets) {
            if (topicSet.isStartUp()) {
                _messageListService.initializeAlarmMessageList(topicSet);
            }
        }
    }
}
