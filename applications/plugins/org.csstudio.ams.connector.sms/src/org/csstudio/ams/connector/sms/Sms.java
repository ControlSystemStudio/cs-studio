
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

package org.csstudio.ams.connector.sms;

import java.io.Serializable;

/**
 * @author Markus Moeller
 *
 */
public class Sms implements Serializable
{
    /**
     * @author Markus
     *
     */
    enum State
    {
        NEW, SENT, FAILED
    }
    
    /**
     * @author Markus
     *
     */
    enum Type
    {
        OUT, IN
    }
    
    /** Generated serial version id */
    private static final long serialVersionUID = -8520701491907741075L;

    /** The id of the SMS */
    private long id;
    
    /** Timestamp of the JMS message */
    private long smsTimestamp;
    
    /** Cell phone number of the receiver */
    private String phoneNumber;
    
    /** Text of the SMS */
    private String message;
    
    /** State of the SMS */
    private Sms.State state;
    
    /** Type of the SMS */
    private Sms.Type type;

    /** The one and only constructor. Every attributes have to be set if we create a new object. */
    public Sms(long id, long timestamp, String number, String text, Sms.Type type)
    {
        this.id = id;
        smsTimestamp = timestamp;
        phoneNumber = number;
        message = text;
        this.type = type;
        state = Sms.State.NEW;
    }
    
    /**
     * Overwrites the method <code>toString()</code> from Object. Creates a nice string containg the content
     * of this SMS message.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        
        result.append("SMS{");
        result.append(this.id + ",");
        result.append(this.smsTimestamp + ",");
        result.append(this.phoneNumber + ",");
        result.append(this.message + ",");
        result.append(this.state + ",");
        result.append(this.type + "}");
        
        return result.toString();
    }
    
    /**
     * Returns the current timestamp of this SMS message.
     * 
     * @return The timestamp
     */
    public long getSmsTimestamp()
    {
        return smsTimestamp;
    }
    
    /**
     * Sets the timestamp of this SMS message.
     * 
     * @param smsTimestamp
     */
    public void setSmsTimestamp(long smsTimestamp)
    {
        this.smsTimestamp = smsTimestamp;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Sms.State getState()
    {
        return state;
    }

    public void setState(Sms.State state)
    {
        this.state = state;
    }

    public Sms.Type getType()
    {
        return type;
    }

    public void setType(Sms.Type type)
    {
        this.type = type;
    }
}
