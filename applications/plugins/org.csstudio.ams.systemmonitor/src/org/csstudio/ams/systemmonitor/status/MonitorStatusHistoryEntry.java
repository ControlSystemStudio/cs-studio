
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

package org.csstudio.ams.systemmonitor.status;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.ams.systemmonitor.check.CheckResult;

/**
 * @author Markus Moeller
 *
 */
public class MonitorStatusHistoryEntry implements Serializable
{
    /** Generated serial version id */
    private static final long serialVersionUID = 7715138797216167112L;

    /** Timestamp of this check entry(at which time did the AmsSystemMonitor the check) */
    private long timeStamp;
    
    /** Timestamp of JMS message. Used to identify timed out checks. */
    private long timeStampOfUnansweredCheck;

    /** The check status / answer of the SmsDeliveryWorker */
    private CheckResult checkStatus;
    
    /** Flag that indicates whether or not a SMS was sent for this status entry */
    private boolean smsSent;

    /**
     * 
     * @param result
     * @param time
     * @param jmsTimeStamp
     * @param sent
     */
    public MonitorStatusHistoryEntry(CheckResult result, long time, long jmsTimeStamp, boolean sent)
    {
        timeStamp = time;
        timeStampOfUnansweredCheck = jmsTimeStamp;
        checkStatus = result;
        smsSent = sent;
    }
    
    /**
     * @return the timeStamp
     */
    public long getTimestamp()
    {
        return timeStamp;
    }

    /**
     * 
     * @return The time stamp
     */
    public long getJmsTimeStamp()
    {
        return timeStampOfUnansweredCheck;
    }
    
    /**
     * @return the checkStatus
     */
    public CheckResult getCheckStatus()
    {
        return checkStatus;
    }
    
    /**
     * @return the smsLocked
     */
    public boolean wasSmsSent()
    {
        return smsSent;
    }
    
    @Override
    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuffer s = new StringBuffer();
        
        s.append("HistoryEntry{checkTime=" + dateFormat.format(new Date(timeStamp)) + "(" + timeStamp + "),unansweredCheckTime=" + timeStampOfUnansweredCheck +"," + checkStatus + ",smsSent=" + smsSent + "}");
        
        return s.toString();
    }
}
