
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.delivery.util.jms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 27.12.2011
 */
public abstract class AbstractMessageContent {
    
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractMessageContent.class);
    
    protected Hashtable<String, String> content;
    
    public AbstractMessageContent(MapMessage jmsMsg) {
        content = new Hashtable<String, String>();
        init(jmsMsg);
    }
    
    private Map<String, String> init(MapMessage jmsMsg) {
        Enumeration<?> keys = null;
        try {
            keys = jmsMsg.getMapNames();
        } catch (JMSException jmse) {
            LOG.error("Cannot read map names from JMS message: {}", jmse.getMessage());
        }
        
        if (keys != null) {
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                try {
                    content.put(key, jmsMsg.getString(key));
                } catch (JMSException jmse) {
                    LOG.error("Cannot read value for key '{}': {}", key, jmse.getMessage());
                }
            }
        }
        
        return content;
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("AbstractMessageContent {\n");
        buffer.append(" " + content.toString());
        buffer.append("}");
        return buffer.toString();
    }
    
    public boolean containsKey(String key) {
        return content.containsKey(key);
    }
    
    public String getValue(String key) {
        String value = null;
        if (key != null) {
            value = content.get(key);
        }
        return value;
    }
}
