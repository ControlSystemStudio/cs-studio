
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

/**
 * This enum represents the cause / reason of an error
 * 
 * @author mmoeller
 * @version 1.0
 * @since 18.04.2012
 */
public enum ErrorReason {
    
    /** The default value */
    UNDEFINED(0, "undefined", "undefined"),
    
    /** The error was caused by the SystemMonitor itself */
    AMS_MONITOR(1, "AmsMonitor", "AMS-Monitor: The check of AMS is not possible."),
    
    /** The error was caused by the AMS (no answer from the AMS) */
    AMS(2, "AMS", "AMS-Monitor: AMS does not respond to the current check."),
    
    /** The error was caused by the SmsDeliveryWorker (no answer from the SmsDeliveryWorker) */
    SMS_DELIVERY_WORKER(4, "SmsDeliveryWorker", "SmsDeliveryWorker does not respond to the current check."),
    
    /** The error was caused by the delivery device */
    DELIVERY_DEVICE(8, "Modem", "SmsDeliveryWorker: The GSM modems respond an error: "),

    /** The error was caused by the JMS methods (sending, receiving, etc.) */
    JMS(16, "JMS", "AMS-Monitor: JMS-Error: Message cannot be sent.");
    
    /**
     * The error number is not really used at the moment. May be in the future...
     */
    private int errorNumber;
    
    private String description;
    
    private String alarmMessage;
    
    private ErrorReason(int number, String desc, String alarm) {
        errorNumber = number;
        description = desc;
        alarmMessage = alarm;
    }
    
    @Override
    public String toString() {
        return description;
    }
    
    public int getErrorNumber() {
        return errorNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getAlarmMessage() {
        return alarmMessage;
    }
}
