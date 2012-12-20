
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.check;

import org.csstudio.ams.application.monitor.internal.AmsMonitorPreference;
import org.csstudio.ams.application.monitor.jmx.JmsSubscriptionCleaner;
import org.csstudio.ams.application.monitor.status.AmsCheckAnalyser;
import org.csstudio.ams.application.monitor.status.CheckStatus;
import org.csstudio.ams.application.monitor.status.SmsCheckAnalyser;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 11.04.2012
 */
public class SystemCheckWorker extends Thread {
    
    private static final Logger LOG = LoggerFactory.getLogger(SystemCheckWorker.class);
    
    // TODO: Hier darf keine Konstante stehen!!!!
    //       Das beiﬂt sich mit dem Eintrag in der Plugin customization.
    private static final long MAX_THREAD_WAITTIME = 240000L;
    
    private ISessionService xmppService;
    
    private AmsCheckProcessor amsCheckProcessor;
    
    private NewSmsCheckProcessor smsCheckProcessor;
    
    public SystemCheckWorker(ISessionService xmpp, String ws) {
        amsCheckProcessor = new AmsCheckProcessor("AmsSystemCheck", ws);
        long interval = AmsMonitorPreference.SMS_CHECK_INTERVAL.getValue();
        smsCheckProcessor = new NewSmsCheckProcessor("SmsDeliveryWorkerCheck", ws, interval);
        xmppService = xmpp;
    }
    
    @Override
    public void run() {
        LOG.info("{} starting.", SystemCheckWorker.class.getSimpleName());
        startCheck();
        analyseCheck();
        cleanSubscription();
        LOG.info("{} leaving.", SystemCheckWorker.class.getSimpleName());
    }
    
    private void startCheck() {
        
        LOG.info("Starting AMS check.");
        Thread amsCheckThread = new Thread(amsCheckProcessor);
        amsCheckThread.setName("AmsSystemCheck");
        amsCheckThread.start();
        try {
            amsCheckThread.join(MAX_THREAD_WAITTIME);
        } catch (InterruptedException e) {
            LOG.warn("{} has been interrupted.", amsCheckThread.getName());
        }
        
        if (smsCheckProcessor.doCheckNow() && amsCheckProcessor.isOk()) {
            LOG.info("Starting SmsDeliveryWorker check.");
            Thread smsCheckThread = new Thread(smsCheckProcessor);
            smsCheckThread.setName("SmsDeliveryWorkerCheck");
            smsCheckThread.start();
            try {
                smsCheckThread.join(MAX_THREAD_WAITTIME);
            } catch (InterruptedException e) {
                LOG.warn("{} has been interrupted.", smsCheckThread.getName());
            }
        } else {
            LOG.info("The SmsDeliveryWorker will NOT be checked now.");
        }
    }
    
    private void analyseCheck() {
        if (amsCheckProcessor.hasBeenStarted()) {
            AmsCheckAnalyser amsAnalyser = new AmsCheckAnalyser(amsCheckProcessor, LOG, xmppService);
            amsAnalyser.analyseCheck();
            amsCheckProcessor.saveMonitorStatus();
        }
        if (smsCheckProcessor.hasBeenStarted() && amsCheckProcessor.isOk()) {
            SmsCheckAnalyser smsAnalyser = new SmsCheckAnalyser(smsCheckProcessor, LOG, xmppService);
            smsAnalyser.analyseCheck();
            smsCheckProcessor.saveMonitorStatus();
        }
    }

    private void cleanSubscription() {
        
        JmsSubscriptionCleaner cleaner = null;

        String monitorTopic = AmsMonitorPreference.JMS_CONSUMER_TOPIC_MONITOR.getValue();
        
        if (amsCheckProcessor.hasBeenStarted()) {
            if (amsCheckProcessor.getCurrentCheckStatusInfo().getCheckStatus() == CheckStatus.OK) {
                cleaner = new JmsSubscriptionCleaner(monitorTopic);
                if (cleaner.destroySubscription("AmsSystemCheck")) {
                    LOG.info("Subscription for AmsSystemCheck destroyed.");
                }
            }
        }
        
        if (smsCheckProcessor.hasBeenStarted()) {
            if (smsCheckProcessor.getCurrentCheckStatusInfo().getCheckStatus() == CheckStatus.OK) {
                if (cleaner == null) {
                    cleaner = new JmsSubscriptionCleaner(monitorTopic);
                }
                if (cleaner.destroySubscription("SmsDeliveryWorkerCheck")) {
                    LOG.info("Subscription for SmsDeliveryWorkerCheck destroyed.");
                }
            }
        }
    }
}
