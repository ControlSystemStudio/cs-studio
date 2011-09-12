
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeSet;
import org.csstudio.ams.systemmonitor.AmsSystemMonitorApplication;
import org.csstudio.ams.systemmonitor.check.CheckResult;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the current check status and the previous check status if it produces
 * an error or a time out.
 *
 * @author Markus Moeller
 */
public class MonitorStatusHandler
{
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(AmsSystemMonitorApplication.class);
    
    /** Status of the AMS system check */
    // private MonitorStatus thisStatus;
    
    /** Name of this status handler */
    private String statusHandlerName;

    /** Current check status */
    private MonitorStatusEntry currentStatus;

    /** History of check status, Key = timestamp value = Check status */
    private TreeSet<MonitorStatusEntry> statusHistory;

    /** Location of the workspace directory. Used to store the date object. */
    private String workspaceLocation;
    
    /** Check interval in ms. A value of 0 forces a check for every runtime. */
    private long _checkInterval;

    /** Number of allowed timeouts */
    private int _allowedTimeoutCount;
    
    /** Name of the status file */
    private String statusFileName;
    
    /**
     * 
     * @param name
     * @param filename
     */
    public MonitorStatusHandler(String name, String filename, int allowedTimeoutCount) {
        this(name, filename, 0, allowedTimeoutCount);
    }
    
    /**
     * 
     * @param name
     * @param filename
     * @param checkInterval
     */
    public MonitorStatusHandler(String name, String filename,
                                long checkInterval,
                                int allowedTimeoutCount) {
        
        statusHandlerName = name;
        statusFileName = filename;
        this._checkInterval = checkInterval;
        this._allowedTimeoutCount = allowedTimeoutCount;
        currentStatus = new MonitorStatusEntry(0, CheckResult.NONE, "", false, false, false);
        
        // Retrieve the location of the workspace directory
        try
        {
            workspaceLocation = Platform.getLocation().toPortableString();
            if(workspaceLocation.endsWith("/") == false)
            {
                workspaceLocation = workspaceLocation + "/";
            }
        }
        catch(IllegalStateException ise)
        {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }
        
        statusHistory = loadStatus();
        if(statusHistory == null)
        {
            LOG.warn("MonitorStatus object '" + statusFileName + "' not found on disk. Creating a fresh object.");
            statusHistory = new TreeSet<MonitorStatusEntry>(new MonitorStatusEntryComperator());
            if(storeStatus() == true)
            {
                LOG.info("Modem status object stored in " + workspaceLocation + statusFileName + ".");
            }
        }
    }

    /**
     * 
     * @return True if a check has to be performed
     */
    public boolean doNextCheck()
    {
        GregorianCalendar currentDate = null;
        MonitorStatusEntry prev = null;
        long delta = 0;
        
        // Do always a check if no check interval is set OR the history is empty (= no check before)
        if((_checkInterval == 0) || statusHistory.isEmpty())
        {
            return true;
        }
        
        // Get the first entry of the history (last check result)
        prev = statusHistory.first();
        
        // Is the flag set that indicates to force a check?
        if(prev.isForceCheck())
        {
            return true;
        }
        
        // Calculate the time difference
        currentDate = new GregorianCalendar();
        delta = currentDate.getTimeInMillis() - prev.getCheckDate();
        
        return ((_checkInterval - delta) <= 60000);
    }
    
    /**
     * 
     * @return Status entry
     */
    public MonitorStatusEntry getCurrentStatusEntry()
    {
        return currentStatus;
    }
        
    /**
     * 
     * @param status
     */
    public void setCurrentStatus(CheckResult status)
    {
        currentStatus.setCheckStatus(status);
    }

    /**
     * 
     * @return Check result
     */
    public CheckResult getCurrentStatus()
    {
        return currentStatus.getCheckStatus();
    }
    
    /**
     * 
     * @return Check result
     */
    public CheckResult getPreviousStatus()
    {
        CheckResult result = CheckResult.NONE;
        
        if(statusHistory.isEmpty() == false)
        {
            result = statusHistory.first().getCheckStatus();
        }
        
        return result;
    }
    
    /**
     * 
     */
    public void forceNextCheck()
    {
        currentStatus.setForceCheck(true);
    }
    
    /**
     * 
     */
    public void resetForcedCheck()
    {
        currentStatus.setForceCheck(false);
    }
    
    /**
     * 
     * @return True or False
     */
    public boolean sendErrorSms()
    {
        CheckResult cr = null;
        boolean decision = false;
        
        // If the current status is ERROR
        if(currentStatus.getCheckStatus() == CheckResult.ERROR)
        {
            cr = getPreviousStatus();
            if(cr != null)
            {
                // If the previous status also was ERROR AND no error SMS was yet sent, send the SMS
                if(((cr == CheckResult.ERROR) || (cr == CheckResult.TIMEOUT)) && (getSmsSentOfFirstHistoryEntry() == false))
                {
                    decision = true;
                }
                else if(cr == CheckResult.WARN)
                {
                    // If the status changed from WARN to ERROR, send a SMS
                    decision = true;
                }
            }
        }
        else if((currentStatus.getCheckStatus() == CheckResult.TIMEOUT) && (getSmsSentOfFirstHistoryEntry() == false))
        {
            if(getNumberOfTimeouts() > _allowedTimeoutCount)
            {
                decision = true;
            }
        }
        
        return decision;
    }

    /**
     * 
     * @return True or False
     */
    public boolean getSmsSentOfFirstHistoryEntry()
    {
        boolean smsSent = false;
        MonitorStatusEntry entry = null;
        
        if(statusHistory.isEmpty() == false)
        {
            entry = statusHistory.first();
            if(entry != null)
            {
                smsSent = entry.wasSmsSent();
            }
        }
        
        return smsSent;
    }

    /**
     * 
     * @return True or False
     */
    public boolean sendWarnMail()
    {
        CheckResult cr = null;
        boolean decision = false;
        
        // If the current status is WARN
        if(currentStatus.getCheckStatus() == CheckResult.WARN)
        {
            cr = this.getPreviousStatus(); // statusHistory.first().getCheckStatus();
            if(cr != null)
            {
                // If the previous status also was WARN, send the warning e-mail
                decision = ((cr == CheckResult.WARN) && (getSmsSentOfFirstHistoryEntry() == false));
            }
        }
        
        return decision;
    }

    /**
     * 
     * @param sent
     */
    public void setSmsSent(boolean sent)
    {
        currentStatus.setSmsSent(sent);
    }
    
    /**
     * 
     * @return True or False
     */
    public boolean isPriviousSmsSent()
    {
        return getSmsSentOfFirstHistoryEntry();
    }
    
    /**
     * 
     * @param errorStatus
     */
    public void setErrorStatusSet(boolean errorStatus)
    {
        currentStatus.setErrorStatus(errorStatus);
    }
    
    /**
     * 
     * @return True or False
     */
    public boolean isErrorStatusSet()
    {
        return currentStatus.wasErrorStatus();
    }
    
    /**
     * 
     */
    public void resetErrorFlag()
    {
        currentStatus.setErrorStatus(false);
    }
    
    /**
     * 
     * @return The number of timeouts the test had caused
     */
    public int getNumberOfTimeouts()
    {
        MonitorStatusEntry entry = null;
        int number = 0;
        
        if(currentStatus.getCheckStatus() == CheckResult.TIMEOUT)
        {
            number++;
            
            Iterator<MonitorStatusEntry> iter = statusHistory.iterator();
            while(iter.hasNext())
            {
                entry = iter.next();
                if(entry.getCheckStatus() == CheckResult.TIMEOUT)
                {
                    number++;
                }
                else
                {
                    // We only want to count sequenced TIMEOUT status entries.
                    break;
                }
            }
        }
        
        return number;
    }    

    /**
     * Does some basic work for the next check. It copies the previous check to the current check status object.
     * <b>This method have to be called before a check is done.</b>
     */
    public void beginCurrentCheck()
    {
        MonitorStatusEntry prevStatus = null;
        
        if(statusHistory.isEmpty() == false)
        {
            prevStatus = statusHistory.first();
            
            currentStatus = new MonitorStatusEntry(prevStatus);
            if(currentStatus.getCheckStatus() != CheckResult.TIMEOUT)
            {
                // If the status is NOT TIMEOUT then create a new unique check id
                // That causes the sending of a new check message
                currentStatus.setCheckId(createUniqueIdAsString());
            }
        }
        else
        {
            currentStatus = new MonitorStatusEntry();
            currentStatus.setCheckId(createUniqueIdAsString());
        }
    }
    
    /**
     * Does some work after a check was processed.
     * <b>This method have to be called after a check has been done.</b>
     */
    public void stopCurrentCheck()
    {
        MonitorStatusEntry prevStatus = null;
        
        // If the current check does not cause any error, clear the history
        if(currentStatus.getCheckStatus() == CheckResult.OK)
        {
            // Clear the history
            statusHistory.clear();
        }
        else
        {
            if(statusHistory.isEmpty() == false)
            {
                // Get the previous check status
                prevStatus = statusHistory.first();
                if(prevStatus.getCheckStatus() == CheckResult.OK)
                {
                    // If the previous check status was OK, remove it from the hstory
                    statusHistory.remove(prevStatus);
                }
            }
        }
        
        // Add the current status to the history
        statusHistory.add(currentStatus);

        storeStatus();
    }
    
    /**
     * Creates an unique id (type String) for different checks.
     * 
     * @return
     */
    private String createUniqueIdAsString()
    {
        String result = null;
        int hc;
        
        result = Long.toString(Calendar.getInstance().getTime().getTime(), 16);
        hc = Math.abs(result.hashCode());
        result += Integer.toString(hc, 16);
        
        return result;
    }

    /**
     * 
     * @return The status history string
     */
    public String getStatusHistoryAsString()
    {
        StringBuffer s = new StringBuffer();
        
        s.append("\nHistory for " + statusHandlerName + "\n");
        
        if(statusHistory.isEmpty())
        {
            s.append("EMPTY");
            return s.toString();
        }
        
        Iterator<MonitorStatusEntry> iter = statusHistory.iterator();
        while(iter.hasNext())
        {
            s.append(iter.next().toString() + "\n");
        }
        
        return s.toString();
    }
    
    /**
     * 
     * @return True or False
     */
    public boolean storeStatus()
    {
        FileOutputStream  fos = null;
        ObjectOutputStream oos = null;
        boolean result = false;
        
        try
        {
            fos = new FileOutputStream(workspaceLocation + statusFileName);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(statusHistory);
            result = true;
        }
        catch(FileNotFoundException fnfe)
        {
            LOG.error("storeDate(): [*** FileNotFoundException ***]: " + fnfe.getMessage());
            result = false;
        }
        catch(IOException ioe)
        {
            LOG.error("storeDate(): [*** IOException ***]: " + ioe.getMessage());
            result = false;
        }
        finally
        {
            if(oos!=null){try{oos.close();}catch(Exception e){/*Can be ignored*/}oos=null;}
            if(fos!=null){try{fos.close();}catch(Exception e){/*Can be ignored*/}fos=null;}
        }

        LOG.debug(getStatusHistoryAsString());

        return result;
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private TreeSet<MonitorStatusEntry> loadStatus()
    {
        FileInputStream  fis = null;
        ObjectInputStream ois = null;
        Object object = null;
        TreeSet<MonitorStatusEntry> status = null;
        
        try
        {
            fis = new FileInputStream(workspaceLocation + statusFileName);
            ois = new ObjectInputStream(fis);
            object = ois.readObject();
            if(object instanceof TreeSet<?>)
            {
                status = (TreeSet<MonitorStatusEntry>)object;
            }
        }
        catch(FileNotFoundException fnfe)
        {
            LOG.error("loadDate(): [*** FileNotFoundException ***]: " + fnfe.getMessage());
        }
        catch(IOException ioe)
        {
            LOG.error("loadDate(): [*** IOException ***]: " + ioe.getMessage());
        }
        catch(ClassNotFoundException cnfe)
        {
            LOG.error("loadDate(): [*** ClassNotFoundException ***]: " + cnfe.getMessage());
        }
        finally
        {
            if(ois!=null){try{ois.close();}catch(Exception e){/*Can be ignored*/}ois=null;}
            if(fis!=null){try{fis.close();}catch(Exception e){/*Can be ignored*/}fis=null;}
        }
        
        return status;
    }
}
