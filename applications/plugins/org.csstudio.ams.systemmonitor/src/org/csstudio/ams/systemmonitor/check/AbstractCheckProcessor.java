
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.systemmonitor.check;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.jms.JMSException;
import javax.jms.Message;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.ams.systemmonitor.AmsSystemMonitorException;
import org.csstudio.ams.systemmonitor.jms.JmsSender;
import org.csstudio.ams.systemmonitor.jms.MessageHelper;
import org.csstudio.ams.systemmonitor.status.MonitorStatusEntry;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus
 *
 */
public abstract class AbstractCheckProcessor
{
    /** The class logger */
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractCheckProcessor.class);
    
    /** JMS receiver */
    protected JmsRedundantReceiver amsReceiver;
    
    /** JMS sender */
    protected JmsSender amsPublisher;

    /** Helper class that creates and compares JMS messages */
    protected MessageHelper messageHelper;
    
    /**
     * 
     * @throws AmsSystemMonitorException
     */
    public AbstractCheckProcessor(String senderClientId,
                           String receiverClientId,
                           String subscriberName) throws AmsSystemMonitorException {
        messageHelper = new MessageHelper();
        initJms(senderClientId, receiverClientId, subscriberName);
    }

    public abstract void doCheck(MonitorStatusEntry statusEntry) throws AmsSystemMonitorException;
    
    /**
     * 
     * @param message
     */
    protected void acknowledge(Message message) {
        if(message != null) {
            try {
                message.acknowledge();
            } catch(JMSException e) {
                LOG.warn("Cannot acknowledge message: " + message.toString());
            }
        }
    }

    /**
     * 
     * @param dateString
     * @return
     */
    protected long convertDateStringToLong(String dateString) {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        long result = 0;
        
        if(dateString == null) {
            return result;
        }
        
        try {
            result = dateFormat.parse(dateString).getTime();
        } catch(ParseException e) {
            result = 0;
        }
        
        return result;
    }

    /**
     * 
     * @throws AmsSystemMonitorException
     */
    private void initJms(String senderClientId,
                         String receiverClientId,
                         String subscriberName) throws AmsSystemMonitorException {
        
        String url1 = null;
        String url2 = null;
        String topic = null;
        boolean result = false;
        
        IPreferencesService prefs = Platform.getPreferencesService();

        url1 = prefs.getString(AmsActivator.PLUGIN_ID,
                               AmsPreferenceKey.P_JMS_EXTERN_SENDER_PROVIDER_URL,
                               "", null);
        topic = prefs.getString(AmsActivator.PLUGIN_ID,
                                AmsPreferenceKey.P_JMS_EXT_TOPIC_ALARM,
                                "", null);

        LOG.debug("JMS Sender URL: " + url1);
        LOG.debug("JMS Sender Monitor Topic: " + topic);

        amsPublisher = new JmsSender(senderClientId, url1, topic);
        if(amsPublisher.isNotConnected()) {
            closeJms();
            throw new AmsSystemMonitorException("JMS Sender could not be created. URL: " + url1 + " - Topic: " + topic);
        }

        url1 = prefs.getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1, "", null);
        url2 = prefs.getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2, "", null);
        topic = prefs.getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_JMS_AMS_TOPIC_MONITOR, "", null);
        LOG.debug("JMS Receiver URL 1: " + url1);
        LOG.debug("JMS Receiver URL 2: " + url2);
        LOG.debug("JMS Receiver Monitor Topic: " + topic);
        
        // TODO: 
        amsReceiver = new JmsRedundantReceiver(receiverClientId, url1, url2);
        result = amsReceiver.createRedundantSubscriber("amsSystemMonitor",
                                                       topic,
                                                       subscriberName,
                                                       true);
        if(result == false) {
            LOG.error("Could not create redundant receiver for URL " + url1 + ", " + url2 + " and topic " + topic);
            closeJms();
            throw new AmsSystemMonitorException("Could not create redundant receiver for URL " + url1 + ", " + url2 + " and topic " + topic, AmsSystemMonitorException.ERROR_CODE_JMS_CONNECTION);
        }
    }
    
    protected void closeJms() {
        if(amsReceiver != null) { amsReceiver.closeAll(); }
        if(amsPublisher != null) { amsPublisher.closeAll(); }
    }
}
