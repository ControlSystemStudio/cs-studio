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
 */
package org.csstudio.remote.jms.command;

import java.net.InetAddress;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Builder for JMS-Messages.
 * The current implementation will only build a command message.
 * 
 * @author jpenning
 * @since 17.01.2012
 */
public class JmsMessageBuilder {
    private final static String GROUP = "GROUP";
    private final static String HOST = "HOST";
    private final static String NAME = "NAME";
    private final static String TYPE = "TYPE";
    private final static String CREATETIME = "CREATETIME";
    private final static String EVENTTIME = "EVENTTIME";
    private final static String SEVERITY = "SEVERITY";
    private final static String STATUS = "STATUS";
    
    
    // intermediate store for the key-value-pairs inside the builder
    private Map<String, String> _propertyStore = new HashMap<String, String>();
    
    // create a message builder with several standard keys
    public JmsMessageBuilder() {
        _propertyStore.put(TYPE, "command");
        String timeString = createTimeString();
        _propertyStore.put(CREATETIME, timeString);
        _propertyStore.put(EVENTTIME, timeString);
        _propertyStore.put(GROUP, ClientGroup.UNDEFINED.toString());
        _propertyStore.put(NAME, "");
        _propertyStore.put(SEVERITY, "NO_ALARM");
        _propertyStore.put(STATUS, "NO_ALARM");
        storeHost();
    }

    private void storeHost() {
        try {
            _propertyStore.put(HOST, InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException e) {
            _propertyStore.put(HOST, "");
        }
    }
    
    /**
     * add value for GROUP property
     * 
     * @param group value for the GROUP property
     * @return the builder
     */
    public JmsMessageBuilder setGroup(@Nonnull final ClientGroup group) {
        _propertyStore.put(GROUP, group.toString());
        return this;
    }
    
    /**
     * add value for NAME property
     * 
     * @param command this value is actually stored in the NAME property
     * @return the builder
     */
    public JmsMessageBuilder setCommand(@Nonnull final String command) {
        _propertyStore.put(NAME, command);
        return this;
    }
    
    /**
     * add property with key and value
     * CAREFUL: The caller is especially responsible for the key, this must not interfere with the standard keys. 
     * 
     * @param key
     * @param value
     * @return the builder
     */
    public JmsMessageBuilder setProperty(@Nonnull final String key, @Nonnull final String value) {
        _propertyStore.put(key, value);
        return this;
    }
    
    /**
     * Eventually build the message in the given session
     * 
     * @param session
     * @return the newly built message
     * @throws JMSException
     */
    public Message build(@Nonnull final Session session) throws JMSException {
        MapMessage message = session.createMapMessage();
        for (String key : _propertyStore.keySet()) {
            message.setString(key, _propertyStore.get(key));
        }
        return message;
    }
    
    /**
     * creates date and time for the JMS message.
     * HINT: SimpleDateFormat is not thread-safe
     * 
     * @return time as string usable for jms messages times
     */
    private synchronized String createTimeString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return format.format(Calendar.getInstance().getTime());
    }
    
}
