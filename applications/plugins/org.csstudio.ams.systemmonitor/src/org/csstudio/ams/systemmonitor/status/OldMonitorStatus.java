
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeSet;
import org.csstudio.ams.systemmonitor.check.CheckResult;

/**
 * @author Markus Moeller
 *
 */
public class OldMonitorStatus implements Serializable
{
    /** Generated serial version id */
    private static final long serialVersionUID = -4888901552949523514L;

    /** Name of this status object */
    private String monitorStatusName;

    /** History of check status, Key = timestamp value = Check status */
    private TreeSet<MonitorStatusHistoryEntry> history;

    /** Calendar object that holds the last check date */
    private GregorianCalendar currentCheckDate;
    
    /** The last check status / answer of the SmsDeliveryWorker */
    private CheckResult currentCheckStatus;
        
    /** Flag, that indicates if a check have to be done. Only used if a check interval is defined.*/
    private boolean forceCheck;

    /** Flag, that indicates if a SMS was sent for this entry.*/
    private boolean smsSent;
    
    /** Flag, that indicates if the status was ERROR before. Used if the status changes from ERROR to WARN
     *  and then to OK. In this case we have to send an SMS instead of an e-mail.
     */
    private boolean wasErrorStatus;
    
    /** Time stamp (in ms) of the last unanswered check. */
    private long timeStampOfUnansweredCheck;
    
    /** Maximal number of history entries */
    private final int MAX_HISTORY_COUNT = 21;
    
    public OldMonitorStatus(String name)
    {
        monitorStatusName = name;
        history = new TreeSet<MonitorStatusHistoryEntry>(new HistoryEntryComperator());
        currentCheckDate = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        currentCheckStatus = CheckResult.OK;
        forceCheck = false;
        smsSent = false;
        wasErrorStatus = false;
        timeStampOfUnansweredCheck = 0;
    }
    
    /**
     * 
     * @param result
     */
    public void setCurrentDateAndStatus(CheckResult result)
    {
        moveCurrentStatusToHistory();
        currentCheckDate = new GregorianCalendar();
        currentCheckStatus = result;
    }
    
    /**
     * 
     */
    public void moveCurrentStatusToHistory()
    {
        if(currentCheckDate != null && currentCheckStatus != null)
        {
            history.add(new MonitorStatusHistoryEntry(currentCheckStatus, currentCheckDate.getTimeInMillis(), timeStampOfUnansweredCheck, smsSent));
            if(history.size() > MAX_HISTORY_COUNT)
            {
                history.remove(history.last());
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public MonitorStatusHistoryEntry getFirstHistoryEntry()
    {
        MonitorStatusHistoryEntry result = null;
        
        if(history.isEmpty() == false)
        {
            result = history.first();
        }
        
        return result;
    }

    /**
     * 
     * @return
     */
    public CheckResult getCheckResultOfFirstHistoryEntry()
    {
        CheckResult prev = null;
        MonitorStatusHistoryEntry entry = null;
        
        entry = getFirstHistoryEntry();
        if(entry != null)
        {
            prev = entry.getCheckStatus();
        }
        
        return prev;
    }

    public boolean getSmsSentOfFirstHistoryEntry()
    {
        boolean smsSent = false;
        MonitorStatusHistoryEntry entry = null;
        
        entry = getFirstHistoryEntry();
        if(entry != null)
        {
            smsSent = entry.wasSmsSent();
        }
        
        return smsSent;
    }

    /**
     * 
     */
    public void removeFirstHistoryEntry()
    {
        if(history.isEmpty() == false)
        {
            history.remove(history.first());
        }
    }
    
    /**
     * 
     * @return
     */
    public long getTimeInMillisOfCurrentCheck()
    {
        return currentCheckDate.getTimeInMillis();
    }
        
    /**
     * 
     * @return
     */
    public CheckResult getCurrentStatus()
    {
        return currentCheckStatus;
    }
    
    /**
     * @return the lastAmsCheckWasSuccessful
     */
    public boolean wasCurrentCheckSuccessful()
    {
        return currentCheckStatus == CheckResult.OK;
    }
    
    /**
     * 
     * @param c
     */
    public void setForceCheck(boolean c)
    {
        forceCheck = c;
    }
    
    /**
     * 
     * @return
     */
    public boolean getForceCheck()
    {
        return forceCheck;
    }

    /**
     * 
     * @param sent
     */
    public void setSmsSent(boolean sent)
    {
        smsSent = sent;
    }
    
    /**
     * 
     * @return
     */
    public boolean isSmsSent()
    {
        return smsSent;
    }

    /**
     * 
     * @param error
     */
    public void setErrorFlag(boolean error)
    {
        wasErrorStatus = error;
    }
    
    /**
     * 
     * @return
     */
    public boolean isErrorFlagSet()
    {
        return wasErrorStatus;
    }

    /**
     * 
     * @return
     */
    public boolean wasNotAnswered()
    {
        return (timeStampOfUnansweredCheck > 0);
    }
    
    /**
     * Timestamp of an unanswered check.
     * 
     * @return
     */
    public long getTimeStampOfUnansweredCheck()
    {
        return timeStampOfUnansweredCheck;
    }
    
    /**
     * 
     * @param timeStamp
     */
    public void setTimeStampOfUnansweredCheck(long timeStamp)
    {
        timeStampOfUnansweredCheck = timeStamp;
    }
    
    /**
     * 
     * @param timeStamp
     * @return
     */
    public boolean isValidAnswer(long timeStamp)
    {
        return (timeStampOfUnansweredCheck == timeStamp);
    }
    
    /**
     * @return
     */
    @Override
    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuffer s = new StringBuffer();
        
        s.append("CheckStatus{" + monitorStatusName + "," + dateFormat.format(currentCheckDate.getTime()) + "(" + currentCheckDate.getTime().getTime() + "),unansweredCheckTime=" + timeStampOfUnansweredCheck + "," + currentCheckStatus + ",forceCheck=" + forceCheck + ",smsSent=" + smsSent + ",errorWasSet=" + wasErrorStatus + "}");

        return s.toString();
    }
    
    /**
     * 
     * @return
     */
    public String toStringWithHistory()
    {
        StringBuffer s = new StringBuffer();
        
        s.append(toString());
        s.append("\n\nHistory for " + monitorStatusName + "\n");
        
        Iterator<MonitorStatusHistoryEntry> iter = history.iterator();
        while(iter.hasNext())
        {
            s.append(iter.next().toString() + "\n");
        }
        
        return s.toString();
    }
}
