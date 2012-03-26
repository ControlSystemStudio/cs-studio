
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.csstudio.ams.systemmonitor.check.CheckResult;

/**
 * @author Markus Moeller
 *
 */
public class MonitorStatusEntry implements Serializable
{
    /** Generated serial version id */
    private static final long serialVersionUID = -1090491389161833809L;

    /** Time and date of the check date (in ms) */
    private long checkDate;
    
    /** Check id of the last unanswered check. */
    private String checkId;
    
    /** The last check status / answer of the SmsConnector */
    private CheckResult checkStatus;
        
    /** Flag, that indicates if a check have to be done. Only used if a check interval is defined. */
    private boolean forceCheck;

    /** Flag, that indicates if a SMS was sent for this entry. */
    private boolean smsSent;
    
    /** 
     * Flag, that indicates if the status was ERROR before. Used if the status changes from ERROR to WARN
     *  and then to OK. In this case we have to send an SMS instead of an e-mail.
     */
    private boolean wasErrorStatus;
    
    /**
     * 
     */
    public MonitorStatusEntry()
    {
        checkDate = System.currentTimeMillis();
        checkStatus = CheckResult.NONE;
        checkId = "";
        forceCheck = false;
        smsSent = false;
        wasErrorStatus = false;
    }
    
    /**
     * 
     * @param checkDate
     * @param checkStatus
     * @param checkId
     * @param forceCheck
     * @param smsSent
     * @param errorStatus
     */
    public MonitorStatusEntry(long checkDate, CheckResult checkStatus, String checkId, boolean forceCheck, boolean smsSent, boolean errorStatus)
    {
        this.checkDate = checkDate;
        this.checkStatus = checkStatus;
        this.checkId = (checkId != null) ? checkId.trim() : "";
        this.forceCheck = forceCheck;
        this.smsSent = smsSent;
        this.wasErrorStatus = errorStatus;
    }
    
    /**
     * This constructor creates a copy of the MonitorStatusEntry object that was passed as a parameter.
     * <b>The timestamp is set to the current date and time!</b>
     * 
     * @param statusEntry
     */
    public MonitorStatusEntry(MonitorStatusEntry statusEntry)
    {
        this.checkDate = System.currentTimeMillis();
        this.checkStatus = statusEntry.getCheckStatus();
        this.checkId = statusEntry.getCheckId();
        this.forceCheck = statusEntry.isForceCheck();
        this.smsSent = statusEntry.wasSmsSent();
        this.wasErrorStatus = statusEntry.wasErrorStatus();
    }

    /**
     * 
     * @return
     */
    public long getCheckDate()
    {
        return checkDate;
    }

    /**
     * 
     * @param timestamp
     */
    public void setCheckDate(long timestamp)
    {
        checkDate = timestamp;
    }
    
    /**
     * 
     * @return
     */
    public CheckResult getCheckStatus()
    {
        return checkStatus;
    }

    /**
     * 
     * @param currentCheckStatus
     */
    public void setCheckStatus(CheckResult currentCheckStatus)
    {
        this.checkStatus = currentCheckStatus;
    }

    /**
     * 
     * @return
     */
    public boolean isForceCheck()
    {
        return forceCheck;
    }

    /**
     * 
     * @param forceCheck
     */
    public void setForceCheck(boolean forceCheck)
    {
        this.forceCheck = forceCheck;
    }

    /**
     * 
     * @return
     */
    public boolean wasSmsSent()
    {
        return smsSent;
    }

    /**
     * 
     * @param smsSent
     */
    public void setSmsSent(boolean smsSent)
    {
        this.smsSent = smsSent;
    }

    /**
     * 
     * @return
     */
    public boolean wasErrorStatus()
    {
        return wasErrorStatus;
    }

    /**
     * 
     * @param errorStatus
     */
    public void setErrorStatus(boolean errorStatus)
    {
        this.wasErrorStatus = errorStatus;
    }

    /**
     * 
     * @return
     */
    public String getCheckId()
    {
        return checkId;
    }

    /**
     * 
     * @param checkId
     */
    public void setCheckId(String checkId)
    {
        this.checkId = (checkId != null) ? checkId.trim() : "";
    }
    
    /**
     * 
     */
    public void clearCheckId()
    {
        this.checkId = "";
    }

    /**
     * 
     */
    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuffer s = new StringBuffer();
        s.append("MonitorStatusEntry{checkTime=" + dateFormat.format(new Date(checkDate)) + "(" + checkDate + "),checkId=" + checkId +"," + checkStatus + ",forceCheck=" + forceCheck + ",smsSent=" + smsSent + ",wasErrorStatus=" + wasErrorStatus + "}");
        return s.toString();
    }
}
