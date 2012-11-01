
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

package org.csstudio.ams.application.monitor.check;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.jms.JMSException;
import javax.jms.Message;
import org.csstudio.ams.application.monitor.AmsMonitorException;
import org.csstudio.ams.application.monitor.internal.AmsMonitorPreference;
import org.csstudio.ams.application.monitor.jms.JmsPublisher;
import org.csstudio.ams.application.monitor.message.MessageConverter;
import org.csstudio.ams.application.monitor.message.MessageCreator;
import org.csstudio.ams.application.monitor.status.CheckStatus;
import org.csstudio.ams.application.monitor.status.CheckStatusInfo;
import org.csstudio.ams.application.monitor.status.ErrorReason;
import org.csstudio.ams.application.monitor.status.MonitorStatus;
import org.csstudio.utility.jms.consumer.AsyncJmsConsumer;
import org.csstudio.utility.jms.sharedconnection.ClientConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus
 *
 */
public abstract class AbstractCheckProcessor implements Runnable {
    
    /** The class logger */
    private static Logger LOG;
    
    private String jmsSubscriberName;

    protected MessageConverter messageConverter;
    protected MessageCreator messageCreator;
    protected AsyncJmsConsumer consumer;
    protected JmsPublisher publisher;
    protected MonitorStatus monitorStatus;
    protected String workspaceLocation;
    protected long checkInterval;
    protected boolean hasBeenStarted;

    public AbstractCheckProcessor(Logger logger, String ws, String subscriberName) {
        this(logger, ws, subscriberName, 0L);
    }
    
    public AbstractCheckProcessor(Logger logger, String ws, String subscriberName, long interval) {
        
        // We assume that the value of interval contains minutes
        checkInterval = interval * 60000L;
        hasBeenStarted = false;
        
        if (logger != null) {
            LOG = logger;
        } else {
            LOG = LoggerFactory.getLogger(AbstractCheckProcessor.class);
        }

        workspaceLocation = ws;
        jmsSubscriberName = subscriberName;
        messageConverter = new MessageConverter();
        messageCreator = new MessageCreator();
    }

    @Override
    public abstract void run();
    
    public String getWorkspaceLocation() {
        return workspaceLocation;
    }
    
    public void addCheckStatusInfo(CheckStatus status, ErrorReason error) {
        monitorStatus.addCheckStatusInfo(System.currentTimeMillis(), status, error);
    }
    
    public CheckStatusInfo getCurrentCheckStatusInfo() {
        return monitorStatus.getCurrentCheckStatusInfo();
    }
    
    public boolean currentCheckIsWarn() {
        return monitorStatus.currentCheckIsWarn();
    }
    
    public boolean currentCheckIsError() {
        return monitorStatus.currentCheckIsError();
    }

    public boolean currentCheckIsRestarted() {
        return (monitorStatus.getCurrentCheckStatusInfo().getCheckStatus() == CheckStatus.RESTARTED);
    }
    
    public boolean previousCheckWasRestarted() {
        boolean result = false;
        if (hasPreviousCheckStatusInfo()) {
            result = (monitorStatus.getPreviousCheckStatusInfo().getCheckStatus() == CheckStatus.RESTARTED);
        }
        return result;
    }

    public boolean reachedErrorCount() {
        return monitorStatus.hasMaxErrorCount();
    }
    
    public boolean reachedWarnCount() {
        return monitorStatus.hasMaxWarnCount();
    }

    public boolean isOk() {
        return !monitorStatus.currentCheckIsError() && !monitorStatus.currentCheckIsWarn();
    }
 
    public boolean hasPreviousCheckStatusInfo() {
        return (monitorStatus.getPreviousCheckStatusInfo() != null);
    }
    
    public CheckStatusInfo getPreviousCheckStatusInfo() {
        return monitorStatus.getPreviousCheckStatusInfo();
    }

    public boolean previousCheckWasError() {
        return monitorStatus.previousCheckWasError();
    }
    
    public boolean previousCheckWasWarn() {
        return monitorStatus.previousCheckWasWarn();
    }
    
    public boolean previousCheckCausedRestart() {
        return monitorStatus.previousCheckWasError();
    }

    public void setWarnSent(boolean sent) {
        monitorStatus.setWarnSent(sent);
    }
    
    public boolean wasWarnSent() {
        return monitorStatus.wasWarnSent();
    }
    
    public void setErrorSent(boolean sent) {
        monitorStatus.setErrorSent(sent);
    }
    
    public boolean wasErrorSent() {
        return monitorStatus.wasErrorSent();
    }
    
    public void clearNotificationState() {
        monitorStatus.clearNotificationState();
    }
    
    public boolean wasNotificationSent() {
        return (wasErrorSent() || wasWarnSent());
    }

    protected boolean doCheckNow() {
        boolean check = true;
        if (checkInterval > 0L) {
            CheckStatusInfo o = monitorStatus.getPreviousCheckStatusInfo();
            if (o != null) {
                long time = o.getTimestamp() + checkInterval;
                long current = System.currentTimeMillis();
                check = (time <= current) || (o.getCheckStatus() != CheckStatus.OK);
            }
        }
        return check;
    }
    
    protected boolean hasBeenStarted() {
        return hasBeenStarted;
    }
    
    protected void acknowledge(Message message) {
        if(message != null) {
            try {
                message.acknowledge();
            } catch(JMSException e) {
                LOG.warn("Cannot acknowledge message: " + message.toString());
            }
        }
    }

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

    protected void initJms() throws AmsMonitorException {
        
        String topic = AmsMonitorPreference.JMS_PUBLISHER_TOPIC_ALARM.getValue();

        try {
            publisher = new JmsPublisher(topic);
        } catch (ClientConnectionException e1) {
            throw new AmsMonitorException("Could not create publisher.",
                                                AmsMonitorException.ERROR_CODE_JMS);
        }
        if(publisher.isNotConnected()) {
            closeJms();
            throw new AmsMonitorException("JMS publisher could not be created.",
                                                AmsMonitorException.ERROR_CODE_JMS);
        }

        topic = AmsMonitorPreference.JMS_CONSUMER_TOPIC_MONITOR.getValue();
        try {
            consumer = new AsyncJmsConsumer();
            consumer.createMessageConsumer(topic, true, jmsSubscriberName);
        } catch (ClientConnectionException e) {
            throw new AmsMonitorException("Could not create consumer.",
                                                AmsMonitorException.ERROR_CODE_JMS);
        }
    }
    
    protected void closeJms() {
        if(consumer != null) { consumer.close(); }
        if(publisher != null) { publisher.closeAll(); }
    }
    
    protected boolean readMonitorStatus() {
        
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        boolean success = false;
        
        try {
            
            fis = new FileInputStream(workspaceLocation + jmsSubscriberName + ".ser");
            ois = new ObjectInputStream(fis);
            
            Object obj = ois.readObject();
            if (obj instanceof MonitorStatus) {
                monitorStatus = (MonitorStatus) obj;
                success = true;
            }
            
        } catch (FileNotFoundException fnfe) {
            LOG.error("readMonitorStatus(): [*** FileNotFoundException ***]: " + fnfe.getMessage());
        } catch (IOException ioe) {
            LOG.error("readMonitorStatus(): [*** IOException ***]: " + ioe.getMessage());
        } catch (ClassNotFoundException cnfe) {
            LOG.error("readMonitorStatus(): [*** ClassNotFoundException ***]: " + cnfe.getMessage());
        } finally {
            if(ois!=null){try{ois.close();}catch(Exception e){/*Can be ignored*/}ois=null;}
            if(fis!=null){try{fis.close();}catch(Exception e){/*Can be ignored*/}fis=null;}
        }
        
        return success;
    }
    
    public boolean saveMonitorStatus() {
        
        FileOutputStream  fos = null;
        ObjectOutputStream oos = null;
        boolean success = false;
        
        try {
            fos = new FileOutputStream(workspaceLocation + jmsSubscriberName + ".ser");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(monitorStatus);
            success = true;
        } catch(FileNotFoundException fnfe) {
            LOG.error("saveMonitorStatus(): [*** FileNotFoundException ***]: " + fnfe.getMessage());
            success = false;
        } catch(IOException ioe) {
            LOG.error("saveMonitorStatus(): [*** IOException ***]: " + ioe.getMessage());
            success = false;
        } finally {
            if(oos!=null){try{oos.close();}catch(Exception e){/*Can be ignored*/}oos=null;}
            if(fos!=null){try{fos.close();}catch(Exception e){/*Can be ignored*/}fos=null;}
        }

        return success;
    }
}
