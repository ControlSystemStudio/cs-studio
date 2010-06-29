package org.csstudio.alarm.table;

import java.util.List;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.table.dataModel.AlarmMessageList;
import org.csstudio.alarm.table.dataModel.LogMessageList;
import org.csstudio.alarm.table.jms.AlarmListener;
import org.csstudio.alarm.table.preferences.ITopicSetColumnService;
import org.csstudio.alarm.table.preferences.TopicSet;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.alarm.table.service.ITopicsetService;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class MessageListAutoStart implements IStartupServiceListener {

    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(MessageListAutoStart.class);

    private ITopicsetService _topicsetForAlarmService = null;

    private ITopicsetService _topicsetForLogService = null;

    /**
     * This method is executed by the startup listener from platform at CSS startup. The preferences
     * are read for each table type and the message lists are started for each list with the
     * preference 'auto start' == true.
     */
    @Override
    public void run() {
        // TODO (jpenning) message list auto start currently disabled

        _topicsetForAlarmService = JmsLogsPlugin.getDefault().getTopicsetServiceForAlarmViews();
        _topicsetForLogService = JmsLogsPlugin.getDefault().getTopicsetServiceForLogViews();

//        startLogLists();
//        startAlarmLists();
    }

    private Integer getMaximumNumberOfMessages() {
        final ScopedPreferenceStore prefStore = new ScopedPreferenceStore(new InstanceScope(),
                                                                          JmsLogsPlugin
                                                                                  .getDefault()
                                                                                  .getBundle()
                                                                                  .getSymbolicName());
        final String maximumNumberOfMessagesPref = prefStore
                .getString(LogViewPreferenceConstants.MAX);
        Integer result = 200; // Default
        try {
            result = Integer.parseInt(maximumNumberOfMessagesPref);
        } catch (final NumberFormatException e) {
            LOG.warn("Invalid value format for maximum number" + " of messages in preferences");
        }
        return result;
    }

    private void startLogLists() {
        ITopicSetColumnService topicSetColumnService = JmsLogsPlugin.getDefault().getTopicSetColumnServiceForLogViews();
        List<TopicSet> topicSets = topicSetColumnService.getTopicSets();
        for (TopicSet topicSet : topicSets) {
            if (topicSet.isStartUp()) {
                try {
                    LOG.debug("Start log list for topic set " + topicSet.getName());
                    _topicsetForLogService
                            .createAndConnectForTopicSet(topicSet,
                                                         new LogMessageList(getMaximumNumberOfMessages()),
                                                         new AlarmListener());
                } catch (AlarmConnectionException e) {
                    LOG.error("Could not start log list for topic set " + topicSet.getName(), e);
                }
            }
        }

    }

    private void startAlarmLists() {
        ITopicSetColumnService topicSetColumnService = JmsLogsPlugin.getDefault().getTopicSetColumnServiceForAlarmViews();
        List<TopicSet> topicSets = topicSetColumnService.getTopicSets();
        for (TopicSet topicSet : topicSets) {
            if (topicSet.isStartUp()) {
                try {
                    LOG.error("Start alarm list for topic set " + topicSet.getName());
                    _topicsetForAlarmService.createAndConnectForTopicSet(topicSet,
                                                                         new AlarmMessageList(),
                                                                         new AlarmListener());
                } catch (AlarmConnectionException e) {
                    LOG.error("Could not start alarm list for topic set " + topicSet.getName(), e);
                }
            }
        }
    }
}
