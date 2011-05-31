/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
 */
/*
 * $Id: MessageGuardCommander.java,v 1.26 2010/07/29 12:21:16 bknerr Exp $
 */

package org.csstudio.ams.messageminder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.configdb.FilterActionDAO;
import org.csstudio.ams.dbAccess.configdb.FilterActionTObject;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.ams.messageminder.preference.MessageMinderPreferenceKey;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.statistic.Collector;
import org.csstudio.platform.utility.jms.JmsRedundantProducer;
import org.csstudio.platform.utility.jms.JmsRedundantProducer.ProducerId;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

//import org.osgi.service.prefs.BackingStoreException;

/**
 * @author hrickens
 * @author $Author: bknerr $
 * @version $Revision: 1.26 $
 * @since 30.10.2007
 */
public class MessageGuardCommander extends Job {

    private final class ThreadUpdateTopicMessageMap extends Job
    {
        private ThreadUpdateTopicMessageMap(final String name)
        {
            super(name);
        }

		@Override
		protected IStatus run(final IProgressMonitor monitor)
		{
            while (_runUpdateTopicMessageMap)
            {
                try
                {
                    // update ones per hour
                    Thread.sleep(3600000);
                }
                catch(final InterruptedException e)
                {
                    CentralLogger.getInstance().warn(this, e);
                }

                Connection conDb = null;

                try
                {
                    conDb = AmsConnectionFactory.getApplicationDB();
                    if (conDb == null)
                    {
                        CentralLogger.getInstance().warn(this, "Could not init application database");
                    }
                    else
                    {
                        final Set<String> keySet = _topicMessageMap.keySet();
                        Boolean newValue;

                        for(final String filterID : keySet)
                        {
                            newValue = null;

                            final FilterActionTObject[] actionTObject = FilterActionDAO.selectByFilter(conDb,
                                    Integer.parseInt(filterID));

                            for(final FilterActionTObject o : actionTObject)
                            {
                                if(o.getFilterActionTypeRef() == AmsConstants.FILTERACTIONTYPE_TO_JMS)
                                {
                                    newValue = true;
                                    break;
                                }
                            }

                            newValue = (newValue == null) ? false : newValue;
                            final Boolean oldValue = _topicMessageMap.get(filterID);

                            if(newValue != oldValue)
                            {
                                _topicMessageMap.replace(filterID, newValue);
                            }
                        }
                    }
                }
                catch(final SQLException e)
                {
                    CentralLogger.getInstance().warn(this,e);
                }
                finally
                {
                    if(conDb!=null)
                    {
                        try
                        {
                            conDb.close();
                        }
                        catch(final SQLException e)
                        {
                            CentralLogger.getInstance().warn(this,e);
                        }
                    }
                }
            }

            return Status.CANCEL_STATUS;
        }
    }

    /**
     *
     */
    private static final String AMS_COMMAND_KEY_NAME = "COMMAND";

    private final static String MSGVALUE_TCMD_RELOAD = "AMS_RELOAD_CFG";

    private final static String MSGVALUE_TCMD_RELOAD_CFG_START = MessageGuardCommander.MSGVALUE_TCMD_RELOAD
            + "_START";
    private final static String MSGVALUE_TCMD_RELOAD_CFG_END = MessageGuardCommander.MSGVALUE_TCMD_RELOAD
            + "_END";

    /**
     * The (AMS) JMS Redundant Receiver.
     */
    private JmsRedundantReceiver _amsReceiver;
    /**
     * The (AMS) JMS Redundant Producer.
     */
    private JmsRedundantProducer _amsProducer;
    /**
     * A Map with the the Messages time stamp that no older then _toOldTime.
     */
    private final HashMap<MessageKey, MessageTimeList> _messageMap;
    /**
     * The id of the Producer.
     */
    private ProducerId _producerID;
    /**
     * The time stamp white the time who the massage map was last clean up.
     */
    private ITimestamp _lastClean;
    /**
     * The time in second that wait to next clean.
     */
    private final long _time2Clean;
    /**
     * The time in second there are old the message to old an new message was can send.
     */
    private final long _toOldTime;
    /**
     * A list whit the fields they are use as key.
     */
    private final String[] _keyWords;
    private final Collector _messageControlTimeCollector;
    private final Collector _messageDeleteTimeCollector;
    private boolean _run = true;

    private final ConcurrentHashMap<String, Boolean> _topicMessageMap;

    private boolean _runUpdateTopicMessageMap = true;

    /**
     * @param name
     *            The name of this Job.
     */
    public MessageGuardCommander(final String name) {
        super(name);
        _topicMessageMap = new ConcurrentHashMap<String, Boolean>();
        final IEclipsePreferences storeAct = new DefaultScope().getNode(MessageMinderActivator.PLUGIN_ID);

        connect();
        _time2Clean = storeAct.getLong(MessageMinderPreferenceKey.P_LONG_TIME2CLEAN, 20); // sec
        _toOldTime = storeAct.getLong(MessageMinderPreferenceKey.P_LONG_TO_OLD_TIME, 60);
        _keyWords = storeAct.get(MessageMinderPreferenceKey.P_STRING_KEY_WORDS,
                "HOST,FACILITY,AMS-FILTERID").split(",");
        _lastClean = TimestampFactory.now();
        _messageMap = new HashMap<MessageKey, MessageTimeList>();

        /*
         * initialize statistic
         */
        // delete
        _messageDeleteTimeCollector = new Collector();
        _messageDeleteTimeCollector.setApplication(name);
        _messageDeleteTimeCollector.setDescriptor("Time for a clean up run [ns]");
        _messageDeleteTimeCollector.setContinuousPrint(true);

        _messageControlTimeCollector = new Collector();
        _messageControlTimeCollector.setApplication(name);
        _messageControlTimeCollector.setDescriptor("Time to Control a Message [ns]");
        _messageControlTimeCollector.setContinuousPrint(true);

    }

    /**
     *
     */
    private void connect() {
        // IEclipsePreferences storeAct = new
        // DefaultScope().getNode(Activator.PLUGIN_ID);
        final IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();

        final boolean durable = Boolean.parseBoolean(storeAct
                .getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));

        /**
         * Nur fÃ¼r debug zwecke wird die P_JMS_AMS_PROVIDER_URL_2 geï¿½ndert. Der Code kann
         * spï¿½ter wieder entfernt werden. TODO: delete debug code.
         *
         * storeAct.put(org.csstudio.ams.internal.SampleService. P_JMS_AMS_PROVIDER_URL_1,
         * "failover:(tcp://kryksrvjmsa.desy.de:50000)"); storeAct.put(org.csstudio
         * .ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_2,
         * "failover:(tcp://kryksrvjmsa.desy.de:50001)"); storeAct.put(org.csstudio
         * .ams.internal.SampleService.P_JMS_AMS_SENDER_PROVIDER_URL,
         * "failover:(tcp://kryksrvjmsa.desy.de:50000,tcp://kryksrvjmsa.desy.de:50001)" );
         *
         * try { storeAct.flush(); } catch (BackingStoreException e) { // TODO Auto-generated catch
         * block e.printStackTrace(); }
         *
         * /** bis hier
         */

        // --- JMS Receiver Connect---
        // _amsReceiver = new
        // JmsRedundantReceiver("AmsMassageMinderWorkReceiverInternal",
        // storeAct.get(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_1,""),
        // storeAct.get(SampleService.P_JMS_AMS_PROVIDER_URL_2,""));
        _amsReceiver = new JmsRedundantReceiver("AmsMassageMinderWorkReceiverInternal", storeAct
                .getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1), storeAct
                .getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2));
        if (!_amsReceiver.isConnected()) {
            Log.log(this, Log.FATAL, "could not create amsReceiver");
        }

        final boolean result = _amsReceiver.createRedundantSubscriber("amsSubscriberMessageMinder",
                storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_MESSAGEMINDER), storeAct
                        .getString(AmsPreferenceKey.P_JMS_AMS_TSUB_MESSAGEMINDER), durable);

        if (!result) {
            Log.log(this, Log.FATAL, "could not create amsSubscriberMessageMinder");
        }

        // --- JMS Producer Connect ---
        final String[] urls = new String[] { storeAct
                .getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL) };
        _amsProducer = new JmsRedundantProducer("AmsMassageMinderWorkProducerInternal", urls);
        // TODO: remove debug settings
        _producerID = _amsProducer.createProducer(storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR));
//        _producerID = _amsProducer.createProducer("MESSAGE_MINDER_TEST");
        // _producerID = _amsProducer.createProducer("T_HELGE_TEST_OUT");

        // --- Derby DB Connect ---
        // initApplicationDb();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        final ThreadUpdateTopicMessageMap updateTopicMessageMap = new ThreadUpdateTopicMessageMap("UpdateTopicMessageMap");
        updateTopicMessageMap.schedule();
//        updateTopicMessageMap.run(monitor);
		patrol();
        return Status.CANCEL_STATUS;
    }

    /**
     * The main method, it run permanent. First step check for new message. Second step check if
     * time to clean. Third step sleep.
     */
    private void patrol() {
        Message message = null;
        ITimestamp now;
        CentralLogger.getInstance().info(this, "StartTime: " + TimestampFactory.now());
        // int counter =0;
        while (_run) {
            now = TimestampFactory.now();

            while (null != (message = _amsReceiver.receive("amsSubscriberMessageMinder"))) {// receiveNoWait
                // has a bug
                // with
                // acknowledging
                // in openjms 3
                // ADDED BY Markus MÃ¶ller, 2008-08-14
                this.acknowledge(message);
                final ITimestamp before = TimestampFactory.now();
                checkMsg(message, now);
                final ITimestamp after = TimestampFactory.now();
                final double nsec = after.nanoseconds() - before.nanoseconds();
                _messageControlTimeCollector.setInfo("MessageMinder in Nanosecond");
                _messageControlTimeCollector.setValue(nsec);
            }
            if (now.seconds() - _lastClean.seconds() > _time2Clean) {
                final ITimestamp before = TimestampFactory.now();
                cleanUp(now);
                final ITimestamp after = TimestampFactory.now();
                final double nsec = after.nanoseconds() - before.nanoseconds();
                _messageDeleteTimeCollector.setInfo("Clean Up in Nanosecond");
                _messageDeleteTimeCollector.setValue(nsec);
            }
            try {
                Thread.sleep(200);
            } catch (final InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /**
     * @param message
     *            was check if is in nearly time sent and how many times.
     * @param now
     *            a time stamp with the actual time.
     */
    private void checkMsg(final Message message, final ITimestamp now) {
        if (message instanceof MapMessage) {
            final MapMessage mapMessage = (MapMessage) message;
            try {
                CentralLogger.getInstance().info(this, "name: " + mapMessage.getString("NAME"));
                final String command = mapMessage.getString(AMS_COMMAND_KEY_NAME);
                if ((command != null)
                        && (command.equals(MSGVALUE_TCMD_RELOAD_CFG_START) || command
                                .equals(MSGVALUE_TCMD_RELOAD_CFG_END))) {
                    send(message);
                    return;
                }
                // is Action id related to topic.
                final String filterID = mapMessage.getString(AmsConstants.MSGPROP_FILTERID);
                if (isTopicAction(filterID)) {
                    send(message);
                    return;
                }
                final String[] keys = new String[_keyWords.length];
                for (int i = 0; i < keys.length; i++) {
                    keys[i] = mapMessage.getString(_keyWords[i]);
                    if (keys[i] == null) {
                        keys[i] = "";
                    }
                }
                final MessageKey key = new MessageKey(keys);
                MessageTimeList value = _messageMap.get(key);
                if (value == null) {
                    value = new MessageTimeList();
                    _messageMap.put(key, value);
                }
                if (value.add(now)) {
                    send(message);
                    return;
                }
            } catch (final JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isTopicAction(final String filterID)
    {
        if ((filterID != null) && (filterID.trim().length() > 0))
        {
            Connection conDb = null;
            try
            {
                Boolean topicMsg = _topicMessageMap.get(filterID);
                if (topicMsg == null)
                {
                    conDb = AmsConnectionFactory.getApplicationDB();
                    if (conDb == null)
                    {
                        Log.log(this, Log.FATAL, "could not init application database");
                        return false;
                    }

                    final FilterActionTObject[] actionTObject = FilterActionDAO.selectByFilter(conDb, Integer
                            .parseInt(filterID));

                    // Check all filter actions
                    for(final FilterActionTObject o : actionTObject)
                    {
                        if(o.getFilterActionTypeRef() == AmsConstants.FILTERACTIONTYPE_TO_JMS)
                        {
                            topicMsg = true;
                            break;
                        }
                    }

                    topicMsg = (topicMsg == null) ? false : topicMsg;

                    _topicMessageMap.putIfAbsent(filterID, topicMsg);
                }

                return topicMsg.booleanValue();
            }
            catch(final Exception e)
            {
                Log.log(this, Log.FATAL, "could not init application database");
            }
            finally
            {
                if (conDb != null)
                {
                    try
                    {
                        conDb.close();
                    }
                    catch(final SQLException e)
                    {
                        CentralLogger.getInstance().warn(this, e);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Delete all time stamp that older as the _toOldTime. Are all time stamp from one list older
     * delete the list from the map.
     *
     * @param now
     *            the actual time
     */
    private void cleanUp(final ITimestamp now) {
        for (final Iterator<MessageKey> ite = _messageMap.keySet().iterator(); ite.hasNext();) {
            final MessageKey key = ite.next();
            final MessageTimeList value = _messageMap.get(key);
            if (now.seconds() - value.getLastDate().seconds() > _toOldTime) {
                sendCleanUpMessage(key, value.getLastDate(), value.getUnsentsgCount());
                value.resetUnsentMsgCount();
                value.clear();
                ite.remove();
            } else {
                for (int i = 0; i < value.size(); i++) {
                    final ITimestamp timestamp = value.get(i);
                    if (now.seconds() - timestamp.seconds() > _toOldTime) {
                        value.remove(i);
                    }
                }
            }
        }
        _lastClean = now;
    }

    /**
     * @param key
     *            of the massage.
     * @param lastDate
     *            the last Date when the massage are <b>not</b> send.
     * @param number
     *            the number oft don't send massages.
     */
    private void sendCleanUpMessage(final MessageKey key, final ITimestamp lastDate,
            final int number) {
        CentralLogger.getInstance().info(this, key.toString() + "\tlast unsend msg: " + lastDate.toString() + "\t and "
                + number + " unsent msg.");
        // TODO write the sendCleanUpMessage.
        // Soll eine Nachricht versenden die enthï¿½lt welche und wieviele
        // nachrchten zurï¿½ck gehalten wurden.
    }

    /**
     * @param sendMessage
     *            Message to send.
     */
    private void send(final Message sendMessage) {
        if (!_amsProducer.isClosed()) {
            _amsProducer.send(_producerID, sendMessage);
        }
    }

    /**
     * Acknowledges the current message.
     *
     * @param msg
     *            Message object that should be acknowledged
     *
     * @return
     */
    private boolean acknowledge(final Message msg) {
        try {
            msg.acknowledge();
            return true;
        } catch (final Exception e) {
            Log.log(this, Log.FATAL, "could not acknowledge", e);
        }

        return false;
    }

    public synchronized void setRun(final boolean run) {
        _run = run;
    }

    @Override
    protected void canceling() {
        super.canceling();
        _runUpdateTopicMessageMap = false;
    }
}
