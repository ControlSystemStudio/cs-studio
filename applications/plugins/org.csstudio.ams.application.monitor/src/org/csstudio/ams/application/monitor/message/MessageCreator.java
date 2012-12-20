
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

package org.csstudio.ams.application.monitor.message;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.csstudio.ams.application.monitor.util.Environment;

/**
 * @author mmoeller
 * @version 1.0
 * @since 13.04.2012
 */
public class MessageCreator {

    private SimpleDateFormat dateFormat;
    
    public MessageCreator() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }
    
    private static String createUniqueIdAsString() {
        String result = Long.toString(Calendar.getInstance().getTime().getTime(), 16);
        int hc = Math.abs(result.hashCode());
        result += Integer.toString(hc, 16);
        return result;
    }

    public InitiatorMessage getInitiatorMessage(MessageType type) {
        InitiatorMessage message = new InitiatorMessage();
        message.setTypeValue(MessagePropertyValue.TYPE);
        message.setEventTimeValue(dateFormat.format(Calendar.getInstance().getTime()));
        message.setClassValue(createUniqueIdAsString());
        message.setNameValue(MessagePropertyValue.AMS_SYSTEM_CHECK);
        message.setApplicationIdValue(MessagePropertyValue.AMS_SYSTEM_MONITOR);
        message.setDestinationValue(type.toString());        
        message.setUserValue(Environment.getInstance().getUserName());
        message.setHostValue(Environment.getInstance().getHostName());
        message.setSeverityValue(MessagePropertyValue.NO_ALARM);
        message.setStatusValue(MessagePropertyValue.NO_ALARM);
        return message;
    }
    
    public enum MessageType {
        
        AMS(MessagePropertyValue.SYSTEM),
        SMS_DELIVERY_WORKER(MessagePropertyValue.SMS_DELIVERY_WORKER),
        EMAIL_DELIVERY_WORKER(MessagePropertyValue.EMAIL_DELIVERY_WORKER),
        VOICEMAIL_DELIVERY_WORKER(MessagePropertyValue.VOICEMAIL_DELIVERY_WORKER),
        ALL_DELIVERY_WORKER(MessagePropertyValue.ALL_DELIVERY_WORKER);
        
        private String name;
        
        private MessageType(String n) {
            this.name = n;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
