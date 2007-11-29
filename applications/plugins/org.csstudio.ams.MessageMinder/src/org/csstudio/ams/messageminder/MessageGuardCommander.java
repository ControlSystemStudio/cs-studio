
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
 * $Id$
 */

package org.csstudio.ams.messageminder;

import java.util.HashMap;
import java.util.Iterator;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.csstudio.ams.Activator;
import org.csstudio.ams.Log;
import org.csstudio.ams.internal.SampleService;
import org.csstudio.ams.messageminder.preference.PreferenceConstants;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.libs.jms.JmsRedundantProducer;
import org.csstudio.platform.libs.jms.JmsRedundantReceiver;
import org.csstudio.platform.libs.jms.JmsRedundantProducer.ProducerId;
import org.csstudio.platform.statistic.Collector;
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
 * @author $Author$
 * @version $Revision$
 * @since 30.10.2007
 */
public class MessageGuardCommander extends Job {

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
    private HashMap<MessageKey, MessageTimeList> _massageMap;
    /**
     * The id of the Producer.
     */
    private ProducerId _producerID;
    /**
     * The time stamp white the time who the massage map was last clean up. 
     */
    private ITimestamp _lastClean;
    /**
     *  The time in second that wait to next clean.
     */
    private long _time2Clean;
    /**
     * The time in second there are old the message to old an new message was can send.
     */
    private long _toOldTime;
    /**
     * A list whit the fields they are use as key.
     */
    private String[] _keyWords;
    private Collector _messageControlTimeCollector;
    private Collector _messageDeleteTimeCollector; 
    
    /**
     * @param name The name of this Job.
     */
    public MessageGuardCommander(final String name) {
        super(name);
        IEclipsePreferences storeAct = new DefaultScope().getNode(MessageMinderActivator.PLUGIN_ID);
        
        connect();
        _time2Clean = storeAct.getLong(PreferenceConstants.P_LONG_TIME2CLEAN,20); // sec
        _toOldTime = storeAct.getLong(PreferenceConstants.P_LONG_TO_OLD_TIME,60);
        _keyWords = storeAct.get(PreferenceConstants.P_STRING_KEY_WORDS,"HOST,FACILITY,AMS-FILTERID").split(",");
        _lastClean = TimestampFactory.now();
        _massageMap = new HashMap<MessageKey, MessageTimeList>();
        
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
        // IEclipsePreferences storeAct = new DefaultScope().getNode(Activator.PLUGIN_ID);
        IPreferenceStore storeAct = Activator.getDefault().getPreferenceStore();

        /**
         * Nur für debug zwecke wird die P_JMS_AMS_PROVIDER_URL_2 geändert.
         * Der Code kann später wieder entfernt werden.
         * TODO: delete debug code.

        storeAct.put(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_1, "failover:(tcp://kryksrvjmsa.desy.de:50000)");
        storeAct.put(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_2, "failover:(tcp://kryksrvjmsa.desy.de:50001)");
        storeAct.put(org.csstudio.ams.internal.SampleService.P_JMS_AMS_SENDER_PROVIDER_URL,"failover:(tcp://kryksrvjmsa.desy.de:50000,tcp://kryksrvjmsa.desy.de:50001)");
        
        try {
            storeAct.flush();
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /** bis hier */
        
        // --- JMS Receiver Connect---
//        _amsReceiver = new JmsRedundantReceiver("AmsMassageMinderWorkReceiverInternal", storeAct.get(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_1,""),
//                storeAct.get(SampleService.P_JMS_AMS_PROVIDER_URL_2,""));
        _amsReceiver = new JmsRedundantReceiver("AmsMassageMinderWorkReceiverInternal", storeAct.getString(SampleService.P_JMS_AMS_PROVIDER_URL_1),
                storeAct.getString(SampleService.P_JMS_AMS_PROVIDER_URL_2));
        if(!_amsReceiver.isConnected()) {
            Log.log(this, Log.FATAL, "could not create amsReceiver");
        }
        
        boolean result = _amsReceiver.createRedundantSubscriber("amsSubscriberMessageMinder", storeAct.getString(SampleService.P_JMS_AMS_TOPIC_MESSAGEMINDER));

        if(!result){
            Log.log(this, Log.FATAL, "could not create amsSubscriberMessageMinder");
        }
        
        // --- JMS Producer Connect ---
        String[] urls = new String[] {storeAct.getString(SampleService.P_JMS_AMS_SENDER_PROVIDER_URL)};
        _amsProducer = new JmsRedundantProducer("AmsMassageMinderWorkProducerInternal",urls);
        //TODO: remove debug settings
        _producerID = _amsProducer.createProducer(storeAct.getString(SampleService.P_JMS_AMS_TOPIC_DISTRIBUTOR));
        // _producerID = _amsProducer.createProducer("T_HELGE_TEST_OUT");
        
        //--- Derby DB Connect ---
//      initApplicationDb();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        patrol();
        return Status.CANCEL_STATUS;
    }

    /**
     * The main method, it run permanent.
     * First step check for new message.
     * Second step check if time to clean.
     * Third step sleep.
     */
    private void patrol() {
        Message message = null;
        ITimestamp now;
        System.out.println("StartTime: "+TimestampFactory.now());
        // int counter =0;
        while(true){
            now = TimestampFactory.now();
            
            while(null != (message = _amsReceiver.receive("amsSubscriberMessageMinder"))){// receiveNoWait has a bug with acknowledging in openjms 3
                ITimestamp before = TimestampFactory.now();  
                checkMsg(message,now);
                ITimestamp after = TimestampFactory.now();
                double nsec = after.nanoseconds()-before.nanoseconds();
                _messageControlTimeCollector.setInfo("MessageMinder in Nanosecond");
                _messageControlTimeCollector.setValue((double)nsec);
            }
            if(now.seconds()-_lastClean.seconds()>_time2Clean){
                ITimestamp before = TimestampFactory.now();
                cleanUp(now);
                ITimestamp after = TimestampFactory.now();
                double nsec = after.nanoseconds()-before.nanoseconds();
                _messageDeleteTimeCollector.setInfo("Clean Up in Nanosecond");
                _messageDeleteTimeCollector.setValue(nsec);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /**
     * @param message was check if is in nearly time sent and how many times.  
     * @param now a time stamp with the actual time. 
     */
    private void checkMsg(final Message message, final ITimestamp now) {
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            try {
                String[] keys = new String[_keyWords.length];
                for (int i = 0; i < keys.length; i++) {
                    keys[i] = mapMessage.getString(_keyWords[i]);
                    if(keys[i]==null){
                        keys[i]="";
                    }
                }
                MessageKey key = new MessageKey(keys);
                MessageTimeList value = _massageMap.get(key);
                if(value==null){
                    value=new MessageTimeList();
                    _massageMap.put(key, value);
                }
                if(value.add(now)){
                    send(message);
                    return;
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Delete all time stamp that older as the _toOldTime.
     * Are all time stamp from one list older delete the list from the map.
     * 
     * @param now the actual time  
     */
    private void cleanUp(final ITimestamp now) {
        for(Iterator<MessageKey> ite = _massageMap.keySet().iterator();ite.hasNext();){
            MessageKey key = ite.next();
            MessageTimeList value = _massageMap.get(key); 
            if(now.seconds()-value.getLastDate().seconds()>_toOldTime){
                sendCleanUpMessage(key,value.getLastDate(),value.getUnsentsgCount());    
                value.resetUnsentMsgCount();
                value.clear();
                ite.remove();
            }else{
                for (int i = 0; i < value.size(); i++) {
                    ITimestamp timestamp = value.get(i);
                    if(now.seconds()-timestamp.seconds()>_toOldTime){
                        value.remove(i);
                    }
                }
            }
        }
        _lastClean=now;
    }

    /**
     * @param key of the massage.
     * @param lastDate the last Date when the massage are <b>not</b> send.  
     * @param number the number oft don't send massages. 
     */
    private void sendCleanUpMessage(final MessageKey key, final ITimestamp lastDate, final int number) {
        System.out.println(key.toString()+"\tlast unsend msg: "+lastDate.toString()+"\t and "+number+" unsent msg.");
        // TODO write the sendCleanUpMessage.
        // Soll eine Nachricht versenden die enthält welche und wieviele nachrchten zurück gehalten wurden.
    }

    /**
     * @param sendMessage Message to send.
     */
    private void send(final Message sendMessage) {
        if(!_amsProducer.isClosed()){
            _amsProducer.send(_producerID, sendMessage);
        }
    }
}
