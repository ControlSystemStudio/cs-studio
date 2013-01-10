
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.delivery.message;

import java.io.Serializable;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 10.12.2011
 */
public class BaseAlarmMessage implements Serializable {
    
    /** Default serial version id */
    private static final long serialVersionUID = -1L;
    
    private static final int MAX_FAILED_TRIES = 3;
    
    /** Timestamp of the JMS message */
    protected long messageTimestamp;
    
    /** 
     * Priority of the message. Affects the order of the message.
     * LOW = 3, NORMAL = 2, HIGH = 1
     */
    protected Priority priority;

    /** Address (phone number, mail address, ...) of the receiver */
    protected String receiverAddress;
    
    /** Text of the message */
    protected String messageText;

    /** State of the message */
    protected State messageState;
    
    /** Type of the message */
    protected Type messageType;

    /** Id of the gateway */
    protected String deviceId;
    
    /** Number of tries to send this message. */
    protected int failCount;

    /** Indicates if the message is a kind of command message, that initiates a test of all devices. */
    protected boolean deviceTest;

    
    public BaseAlarmMessage(long timestamp, Priority p,
                            String address, String text,
                            State state, Type type,
                            String device) {
        
        this.messageTimestamp = timestamp;
        this.priority = p;
        this.receiverAddress = address;
        setMessageText(text);
        this.messageState = state;
        this.messageType = type;
        this.deviceId = device;
        this.failCount = 0;
    }

    /**
     * Overwrites the method <code>toString()</code> from Object. Creates a nice string containg the content
     * of this alarm message.
     */
    @Override
    public String toString()  {
        StringBuffer result = new StringBuffer();
        result.append("BaseAlarmMessage {");
        result.append(this.messageTimestamp + ",");
        result.append(this.receiverAddress + ",");
        result.append(this.messageText + ",");
        result.append(this.messageState + ",");
        result.append("Failed:" + this.failCount + ",");
        result.append(this.messageType + ",");
        result.append(this.priority + ",");
        result.append(this.deviceId + ",");
        result.append("isTestMessage:" + this.deviceTest + "}");
        return result.toString();
    }
    
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String t) {
        if (t != null) {
            messageText = t.trim();
            deviceTest = (messageText.contains("DEVICE_TEST{") ? true : false);
        } else {
            messageText = "";
            deviceTest = false;
        }
    }

    /**
     * Returns the current timestamp of this SMS message.
     * 
     * @return The timestamp
     */
    public long getMessageTimestamp() {
        return messageTimestamp;
    }
    
    /**
     * Sets the timestamp of this SMS message.
     * 
     * @param timestamp
     */
    public void setMessageTimestamp(long timestamp) {
        this.messageTimestamp = timestamp;
    }

    public void setPriority(Priority prio) {
        this.priority = prio;
    }
    
    public Priority getPriority() {
        return this.priority;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String r) {
        this.receiverAddress = r;
    }

    public State getMessageState() {
        return messageState;
    }

    public void setMessageState(State state) {
        this.messageState = state;
        if(messageState == State.FAILED) {
            if(++this.failCount >= MAX_FAILED_TRIES) {
                messageState = State.BAD;
            }
        } else if (messageState == State.SENT) {
            failCount = 0;
        }
    }

    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type type) {
        this.messageType = type;
    }
    
    public int getFailCount() {
        return failCount;
    }
    
    public boolean isDeviceTest() {
        return deviceTest;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String id) {
        this.deviceId = id;
    }

    public enum Priority {
        
        HIGH(1),
        NORMAL(2),
        LOW(3);
        
        private int priorityValue;
        
        private Priority(int p) {
            this.priorityValue = p;
        }
        
        public int getPriorityValue() {
            return priorityValue;
        }
    }

    public enum State {
        NEW, SENT, FAILED, BAD
    }
    
    public enum Type {
        OUT, IN
    }
}
