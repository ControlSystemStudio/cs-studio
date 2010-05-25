/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: TopicsetService.java,v 1.1 2010/04/28
 * 07:44:07 jpenning Exp $
 */
package org.csstudio.alarm.table.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.alarm.table.jms.AlarmConnectionMonitor;
import org.csstudio.alarm.table.jms.IAlarmTableListener;
import org.csstudio.alarm.table.preferences.TopicSet;

/**
 * Implementation of the topic set service. A map is maintained, keeping the alarm connection and
 * the message lists as values. The key is given by the name of the topic set.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 27.04.2010
 */
public class TopicsetService implements ITopicsetService {

    private final Map<String, Element> _topicSetMap = new HashMap<String, Element>();
    private final String _name;

    public TopicsetService(@Nonnull final String name) {
        _name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createAndConnectForTopicSet(final TopicSet topicSet,
                                            final MessageList messageList,
                                            final IAlarmTableListener alarmTableListener) throws AlarmConnectionException {
        assert !hasTopicSet(topicSet) : "Failed: !hasTopicSet(" + topicSet.getName() + ")";
        assert messageList != null : "Failed: messageList != null";
        assert alarmTableListener != null : "Failed: alarmTableListener != null";

        Element element = new Element();
        element._connection = JmsLogsPlugin.getDefault().getAlarmService().newAlarmConnection();
        element._messageList = messageList;
        element._alarmTableListener = alarmTableListener;
        element._alarmTableListener.setMessageList(element._messageList);
        element._connection.connectWithListenerForTopics(new AlarmConnectionMonitor(),
                                                         element._alarmTableListener,
                                                         topicSet.getTopics()
                                                                 .toArray(new String[0]));

        _topicSetMap.put(topicSet.getName(), element);

        assert hasTopicSet(topicSet) : "Failed: hasTopicSet(" + topicSet.getName() + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectAll() {
        for (Element element : _topicSetMap.values()) {
            element._connection.disconnect();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlarmConnection getAlarmConnectionForTopicSet(final TopicSet topicSet) {
        assert hasTopicSet(topicSet) : "Failed: hasTopicSet(" + topicSet.getName() + ")";
        return _topicSetMap.get(topicSet.getName())._connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageList getMessageListForTopicSet(final TopicSet topicSet) {
        assert hasTopicSet(topicSet) : "Failed: hasTopicSet(" + topicSet.getName() + ")";
        return _topicSetMap.get(topicSet.getName())._messageList;
    }

    @Override
    public IAlarmTableListener getAlarmTableListenerForTopicSet(final TopicSet topicSet) {
        assert hasTopicSet(topicSet) : "Failed: hasTopicSet(" + topicSet.getName() + ")";
        return _topicSetMap.get(topicSet.getName())._alarmTableListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasTopicSet(final TopicSet topicSet) {
        return _topicSetMap.containsKey(topicSet.getName());
    }

    @Override
    public String toString() {
        return "Topicset-Service " + _name;
    }

    /**
     * Container for the value of the map. Used only internally.
     */
    private static final class Element {
        IAlarmConnection _connection;
        MessageList _messageList;
        IAlarmTableListener _alarmTableListener;
    }
}
