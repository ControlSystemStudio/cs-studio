
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

package org.csstudio.ams.delivery.email;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.Priority;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.Type;
import org.csstudio.ams.delivery.queue.AbstractMessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 11.12.2011
 */
public class OutgoingEMailQueue extends AbstractMessageQueue<EMailAlarmMessage> {
    
    private static final Logger LOG = LoggerFactory.getLogger(OutgoingEMailQueue.class);
    
    private EMailWorkerProperties props;
    
    public OutgoingEMailQueue(Object lock, EMailWorkerProperties properties) {
        super(lock);
        props = properties;
    }
    
    @Override
    protected EMailAlarmMessage convertMessage(MapMessage message) {
        
        EMailAlarmMessage result = null;
        String text;
        try {
            text = message.getString(AmsConstants.MSGPROP_RECEIVERTEXT);
            final String emailadr = message.getString(AmsConstants.MSGPROP_RECEIVERADDR);
            final String userName = message.getString(AmsConstants.MSGPROP_SUBJECT_USERNAME);
            final String mySubject = props.getMailSubject();
            String myContent = props.getMailContent();
            myContent = myContent.replaceAll("%N", userName);

            // Sometimes it happens that the placeholder (e.g. $VALUE$, $HOST$, ...)
            // for the alarm message properties are still present.
            // The dollar sign of this placeholders have to be deleted because they cause an
            // IllegalArgumentException when calling method replaceAll()
            text = cleanTextString(text);
            myContent = myContent.replaceAll("%AMSG", text);
            
            result = new EMailAlarmMessage(message.getJMSTimestamp(),
                                           Priority.NORMAL,
                                           emailadr,
                                           myContent,
                                           State.NEW,
                                           Type.OUT,
                                           "NONE",
                                           userName,
                                           mySubject);
        } catch (JMSException jmse) {
            LOG.warn("[*** JMSException ***]: convertMessage(): {}", jmse.getMessage());
        }

        return result;
    }
    
    private String cleanTextString(final String text) {

        if (text == null) {
            return "";
        } else if (text.length() == 0) {
            return "";
        }

        return text.replace("$", "");
    }
}
