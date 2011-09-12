/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 *
 * $Id$
 */
package org.csstudio.alarm.table;

import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.AlarmMessageList;
import org.csstudio.alarm.table.dataModel.LogMessageList;
import org.csstudio.alarm.table.jms.AlarmListener;
import org.csstudio.alarm.table.preferences.ITopicSetColumnService;
import org.csstudio.alarm.table.preferences.TopicSet;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.alarm.table.service.ITopicsetService;
import org.csstudio.alarm.table.ui.InitialStateRetriever;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run at startup time of the plugin.
 * Determines the preferences for auto start and retrieval of initial state and acts accordingly.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 29.06.2010
 */
public class MessageListAutoStart implements IStartupServiceListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageListAutoStart.class);

    private ITopicsetService _topicsetForAlarmService = null;

    private ITopicsetService _topicsetForLogService = null;

    /**
     * This method is executed by the startup listener from platform at CSS startup. The preferences
     * are read for each table type and the message lists are started for each list with the
     * preference 'auto start' == true.
     */
    @Override
    public void run() {
        _topicsetForAlarmService = JmsLogsPlugin.getDefault().getTopicsetServiceForAlarmViews();
        _topicsetForLogService = JmsLogsPlugin.getDefault().getTopicsetServiceForLogViews();

        startLogLists();
        startAlarmLists();
    }

    private int getMaximumNumberOfMessages() {
        final ScopedPreferenceStore prefStore =
            new ScopedPreferenceStore(new InstanceScope(),
                                      JmsLogsPlugin.getDefault().getBundle().getSymbolicName());
        final String maximumNumberOfMessagesPref = prefStore
                .getString(LogViewPreferenceConstants.MAX);
        int result = 200; // Default
        try {
            result = Integer.parseInt(maximumNumberOfMessagesPref);
        } catch (final NumberFormatException e) {
            LOG.warn("Invalid value format for maximum number of messages in preferences");
        }
        return result;
    }

    private void startLogLists() {
        ITopicSetColumnService topicSetColumnService = JmsLogsPlugin.getDefault().getTopicSetColumnServiceForLogViews();
        List<TopicSet> topicSets = topicSetColumnService.getTopicSets();
        for (TopicSet topicSet : topicSets) {
            if (topicSet.isStartUp()) {
                try {
                    LOG.debug("Start log list for topic set {}", topicSet.getName());
                    LogMessageList messageList = new LogMessageList(getMaximumNumberOfMessages());
                    _topicsetForLogService
                            .createAndConnectForTopicSet(topicSet,
                                                         messageList,
                                                         new AlarmListener());
                } catch (AlarmConnectionException e) {
                    LOG.error("Could not start log list for topic set {}", topicSet.getName(), e);
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
                    LOG.error("Start alarm list for topic set {}", topicSet.getName());
                    AlarmMessageList messageList = new AlarmMessageList();
                    _topicsetForAlarmService.createAndConnectForTopicSet(topicSet,
                                                                         messageList,
                                                                         new AlarmListener());
                    if (topicSet.isRetrieveInitialState()) {
                        retrieveInitialState(messageList);
                    }
                } catch (AlarmConnectionException e) {
                    LOG.error("Could not start alarm list for topic set {}", topicSet.getName(), e);
                }
            }
        }
    }

    private void retrieveInitialState(@Nonnull final AbstractMessageList messageList) {
        InitialStateRetriever retriever = new InitialStateRetriever(messageList);
        Job job = retriever.newRetrieveInitialStateJob();
        job.setPriority(Job.LONG);
        job.schedule();

//x = JmsLogsPlugin.getDefault().getWorkbench().getProgressService();
//
//
//        // Start the job.
//        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
//                .getAdapter(IWorkbenchSiteProgressService.class);
//
//        progressService.schedule(job, 0, true);
    }
}
