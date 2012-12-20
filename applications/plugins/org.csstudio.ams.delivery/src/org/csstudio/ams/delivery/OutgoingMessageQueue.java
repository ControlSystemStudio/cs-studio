
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

package org.csstudio.ams.delivery;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.csstudio.ams.delivery.message.BaseAlarmMessage.Priority;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 18.12.2011
 */
public class OutgoingMessageQueue<E> {
    
    private static final Logger LOG = LoggerFactory.getLogger(OutgoingMessageQueue.class);
    
    private ConcurrentLinkedQueue<E> content;
    
    public OutgoingMessageQueue() {
        content = new ConcurrentLinkedQueue<E>();
    }

    public synchronized ArrayList<E> getCurrentContent() {
        ArrayList<E> result = new ArrayList<E>(content);
        content.removeAll(result);
        return result;
    }
    
    public boolean addMessage(E e) {
        return content.add(e);
    }
        
    
    private String cleanTextString(final String text) {

        if (text == null) {
            return "";
        } else if (text.length() == 0) {
            return "";
        }

        return text.replace("$", "");
    }

    private void acknowledge(Message message) {
        try {
            message.acknowledge();
        } catch (JMSException jmse) {
            LOG.warn("Cannot acknowledge message: {}", message);
        }
    }
}
