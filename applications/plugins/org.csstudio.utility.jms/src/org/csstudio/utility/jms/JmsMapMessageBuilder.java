
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

package org.csstudio.utility.jms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

/**
 * @author mmoeller
 * @version 2.0
 * @since 12.04.2012
 */
public class JmsMapMessageBuilder {
    
    private Map<String, String> content;
    
    public JmsMapMessageBuilder(String type) {
        content = new HashMap<String, String>();
        content.put("TYPE", type);
        content.put("EVENTTIME", createTimeString());
    }
    
    public JmsMapMessageBuilder setCreateTime() {
        content.put("CREATETIME", createTimeString());
        return this;
    }

    public JmsMapMessageBuilder setText(String text) {
        content.put("TEXT", text);
        return this;
    }
    
    public JmsMapMessageBuilder setName(String name) {
        content.put("NAME", name);
        return this;
    }

    public JmsMapMessageBuilder setStatus(String status) {
        content.put("STATUS", status);
        return this;
    }

    public JmsMapMessageBuilder setSeverity(String severity) {
        content.put("SEVERITY", severity);
        return this;
    }

    public JmsMapMessageBuilder setClass(String clazz) {
        content.put("CLASS", clazz);
        return this;
    }

    public JmsMapMessageBuilder setHost(String host) {
        content.put("HOST", host);
        return this;
    }

    public JmsMapMessageBuilder setApplicationId(String applicationId) {
        content.put("APPLICATION-ID", applicationId);
        return this;
    }

    public JmsMapMessageBuilder setUser(String user) {
        content.put("USER", user);
        return this;
    }

    public JmsMapMessageBuilder setDestination(String destination) {
        content.put("DESTINATION", destination);
        return this;
    }

    public MapMessage build(Session session) throws JMSException {
        
        MapMessage msg = null;
        
        msg = session.createMapMessage();
        Iterator<String> iter = content.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = content.get(key);
            msg.setString(key, value);
        }
        
        return msg;
    }
    
    public String createTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(Calendar.getInstance().getTime());
    }
}
