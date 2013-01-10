
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

package org.csstudio.ams.application.monitor.status;

import java.io.Serializable;

import org.csstudio.ams.application.monitor.message.AbstractCheckMessage;
import org.csstudio.ams.application.monitor.message.IAnswerMessage;
import org.csstudio.ams.application.monitor.message.MessageMemory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 13.04.2012
 */
public class MonitorStatus implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private MessageMemory<AbstractCheckMessage> messageMemory;
    
    private CheckStatusInfoHistory checkStatusHistory;
    
    private NotificationState notificationState;
    
    private int maxAllowedError;
    
    private int maxAllowedWarn;

    public MonitorStatus() {
        this(3, 3);
    }
    
    public MonitorStatus(int maxError, int maxWarn) {
        messageMemory = new MessageMemory<AbstractCheckMessage>();
        checkStatusHistory = new CheckStatusInfoHistory();
        // Create a dummy check info
//        checkStatusHistory.addCheckStatusInfo(new CheckStatusInfo(CheckStatus.OK, ErrorReason.UNDEFINED));
        notificationState = new NotificationState();
        maxAllowedError = maxError;
        maxAllowedWarn = maxWarn;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("MonitorStatus {");
        result.append(messageMemory.toString());
        result.append(checkStatusHistory.toString());
        result.append("}");
        return result.toString();
    }
    
    public void createCurrentCheckStatusInfo() {
        checkStatusHistory.addCheckStatusInfo(new CheckStatusInfo());
    }
    
    /**
     * The method clears the 
     */
    public void reset() {
        forgetAll();
        checkStatusHistory.clear();
        notificationState.clear();
    }
    
    public void addMessage(AbstractCheckMessage message) {
        messageMemory.add(message);
    }
    
    public void forgetAll() {
        messageMemory.forgetAll();
    }
    
    public void clearHistory() {
        checkStatusHistory.clear();
    }
    
    public boolean isAnswerForOlderMessage(IAnswerMessage e) {
        return messageMemory.containsInitiatorMessageForAnswer(e);
    }
    
    public void addCheckStatusInfo(long time, CheckStatus status, ErrorReason error) {
        checkStatusHistory.addCheckStatusInfo(new CheckStatusInfo(time,
                                                                  status,
                                                                  error));
    }

    public void addCheckStatusInfo(CheckStatusInfo o) {
        checkStatusHistory.addCheckStatusInfo(o);
    }

    public CheckStatusInfo getCurrentCheckStatusInfo() {
        return checkStatusHistory.getCurrentCheckStatusInfo();
    }

    public boolean hasCurrentCheckStatusInfo() {
        return (getCurrentCheckStatusInfo() != null);
    }

    public boolean hasPreviousCheckStatusInfo() {
        return (getPreviousCheckStatusInfo() != null);
    }
    
    public CheckStatusInfo getPreviousCheckStatusInfo() {
        return checkStatusHistory.getPreviousCheckStatusInfo();
    }

    public boolean previousCheckWasWarn() {
        boolean wasWarn = false;
        if (hasPreviousCheckStatusInfo()) {
            wasWarn = (getPreviousCheckStatusInfo().getCheckStatus() == CheckStatus.WARN);
        }
        return wasWarn;
    }
    
    public boolean previousCheckWasError() {
        boolean wasError = false;
        if (hasPreviousCheckStatusInfo()) {
            wasError = (getPreviousCheckStatusInfo().getCheckStatus() == CheckStatus.ERROR);
        }
        return wasError;
    }

    public boolean currentCheckIsWarn() {
        boolean isWarn = false;
        if (hasCurrentCheckStatusInfo()) {
            isWarn = (getCurrentCheckStatusInfo().getCheckStatus() == CheckStatus.WARN);
        }
        return isWarn;
    }
    
    public boolean currentCheckIsError() {
        boolean isError = false;
        if (hasPreviousCheckStatusInfo()) {
            isError = (getCurrentCheckStatusInfo().getCheckStatus() == CheckStatus.ERROR);
        }
        return isError;
    }

    public boolean amsWasRestarted() {
        boolean wasRestarted = false;
        if (hasPreviousCheckStatusInfo()) {
            wasRestarted = (getPreviousCheckStatusInfo().getCheckStatus() == CheckStatus.RESTARTED);
        }
        return wasRestarted;
    }

    public int getMaxAllowedError() {
        return maxAllowedError;
    }
    
    public boolean hasMaxErrorCount() {
        return (checkStatusHistory.getStatusCount(CheckStatus.ERROR) >= maxAllowedError);
    }
    
    public int getErrorCount() {
        return checkStatusHistory.getStatusCount(CheckStatus.ERROR);
    }

    public int getMaxAllowedWarn() {
        return maxAllowedWarn;
    }
    
    public boolean hasMaxWarnCount() {
        return (checkStatusHistory.getStatusCount(CheckStatus.WARN) >= maxAllowedWarn);
    }

    public int getWarnCount() {
        return checkStatusHistory.getStatusCount(CheckStatus.WARN);
    }
    
    public void setWarnSent(boolean sent) {
        notificationState.setWarnSent(sent);
    }
    
    public boolean wasWarnSent() {
        return notificationState.wasWarnSent();
    }
    
    public void setErrorSent(boolean sent) {
        notificationState.setErrorSent(sent);
    }
    
    public boolean wasErrorSent() {
        return notificationState.wasErrorSent();
    }
    
    public void clearNotificationState() {
        notificationState.clear();
    }
    
    public CheckStatusInfo first() {
        return checkStatusHistory.first();
    }
    
    public CheckStatusInfo last() {
        return checkStatusHistory.last();
    }
}
