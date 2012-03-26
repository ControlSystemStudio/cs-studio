
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
 *
 */

package org.csstudio.ams.systemmonitor.status;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TreeSet;
import org.csstudio.ams.systemmonitor.check.CheckResult;

/**
 * @author Markus Moeller
 *
 */
@Deprecated
public class MonitorStatus
{
    /** Name of this status object */
    private String monitorStatusName;

    /** Current check status */
    private MonitorStatusEntry currentStatus;

    /** History of check status, Key = timestamp value = Check status */
    private TreeSet<MonitorStatusEntry> statusHistory;
    
    public MonitorStatus(String name)
    {
        monitorStatusName = name;
        currentStatus = new MonitorStatusEntry();
        statusHistory = new TreeSet<MonitorStatusEntry>(new MonitorStatusEntryComperator());
    }
        
    /**
     * 
     * @param result
     */
    public void setCurrentDateAndStatus(CheckResult result)
    {
        // TODO: Nur ERROR status wird in die Historie aufgenommen
        moveCurrentStatusToHistory();
        
        currentStatus.setCheckDate(System.currentTimeMillis());
        currentStatus.setCheckStatus(result);
    }
    
    /**
     * 
     */
    public void moveCurrentStatusToHistory()
    {
        if(currentStatus.getCheckStatus() != CheckResult.OK)
        {
            statusHistory.add(currentStatus);
        }
    }
    
    public MonitorStatusEntry getFirstHistoryEntry()
    {
        MonitorStatusEntry result = null;
        
        if(statusHistory.isEmpty() == false)
        {
            result = statusHistory.first();
        }
        
        return result;
    }

    public CheckResult getCheckResultOfFirstHistoryEntry()
    {
        CheckResult prev = CheckResult.NONE;
        MonitorStatusEntry entry = null;
        
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
        MonitorStatusEntry entry = null;
        
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
        if(statusHistory.isEmpty() == false)
        {
            statusHistory.remove(statusHistory.first());
        }
    }
    
    /**
     * 
     */
    public void clearStatusHistory()
    {
        statusHistory.clear(); 
    }
    
    public long getCurrentCheckInMillis()
    {
        return currentStatus.getCheckDate();
    }
        
    public CheckResult getCurrentStatus()
    {
        return currentStatus.getCheckStatus();
    }
    
    /**
     * @return the lastAmsCheckWasSuccessful
     */
    public boolean wasCurrentCheckSuccessful()
    {
        return currentStatus.getCheckStatus() == CheckResult.OK;
    }
    
    /**
     * 
     * @param c
     */
    public void setForceCheck(boolean c)
    {
        currentStatus.setForceCheck(c);
    }
    
    public boolean getForceCheck()
    {
        return currentStatus.isForceCheck();
    }

    /**
     * 
     * @param sent
     */
    public void setSmsSent(boolean sent)
    {
        currentStatus.setSmsSent(sent);
    }
    
    public boolean isSmsSent()
    {
        return currentStatus.wasSmsSent();
    }

    /**
     * 
     * @param error
     */
    public void setErrorFlag(boolean error)
    {
        currentStatus.setErrorStatus(error);
    }
    
    public boolean isErrorFlagSet()
    {
        return currentStatus.wasErrorStatus();
    }

    public boolean wasNotAnswered()
    {
        return (currentStatus.getCheckId().length() == 0);
    }
    
    public long getTimeStampOfUnansweredCheck()
    {
        return 0L; //currentStatus.getTimeStampOfUnansweredCheck();
    }
    
    /**
     * 
     * @param timeStamp
     */
    public void setTimeStampOfUnansweredCheck(long timeStamp)
    {
        //currentStatus.setTimeStampOfUnansweredCheck(timeStamp);
    }
    
    public boolean isValidAnswer(long timeStamp)
    {
        return (currentStatus.getCheckDate() == timeStamp);
    }
    
    /**
     * Initializes the current MonitorStatusEntry object. Sets the check date to the current date, the
     * check result to OK and all flags to false.
     * 
     */
    public void resetCurrentStatus()
    {
        currentStatus = null;
        currentStatus = new MonitorStatusEntry(System.currentTimeMillis(), CheckResult.OK, "", false, false, false);
    }
    
    @Override
    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuffer s = new StringBuffer();
        
        s.append("MonitorStatus{" + monitorStatusName + "," + dateFormat.format(currentStatus.getCheckDate()) + "(" + currentStatus.getCheckDate() + "),unansweredCheckId=" + currentStatus.getCheckId() + "," + currentStatus.getCheckStatus() + ",forceCheck=" + currentStatus.isForceCheck() + ",smsSent=" + currentStatus.wasSmsSent() + ",errorWasSet=" + currentStatus.wasErrorStatus() + "}");

        return s.toString();
    }
    
    public String toStringWithHistory()
    {
        StringBuffer s = new StringBuffer();
        
        s.append(toString());
        s.append("\n\nHistory for " + monitorStatusName + "\n");
        
        Iterator<MonitorStatusEntry> iter = statusHistory.iterator();
        while(iter.hasNext())
        {
            s.append(iter.next().toString() + "\n");
        }
        
        return s.toString();
    }
}
