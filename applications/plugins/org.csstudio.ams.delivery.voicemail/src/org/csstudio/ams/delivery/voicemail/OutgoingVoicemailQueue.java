
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
 */

package org.csstudio.ams.delivery.voicemail;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.Priority;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.Type;
import org.csstudio.ams.delivery.queue.AbstractMessageQueue;
import org.csstudio.ams.delivery.voicemail.isdn.TextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutgoingVoicemailQueue extends AbstractMessageQueue<VoicemailAlarmMessage> {

    /** Static class logger */
    private static final Logger LOG = LoggerFactory.getLogger(OutgoingVoicemailQueue.class);

    public OutgoingVoicemailQueue(Object lock) {
        super(lock);
    }
    
    @Override
    protected VoicemailAlarmMessage convertMessage(MapMessage message) {
        
        VoicemailAlarmMessage result = null;
        try {
            final String text = message.getString(AmsConstants.MSGPROP_RECEIVERTEXT);
            final String phone = message.getString(AmsConstants.MSGPROP_RECEIVERADDR);
            final String type = message.getString(AmsConstants.MSGPROP_TEXTTYPE);
            String chainIdAndPos = message.getString(AmsConstants.MSGPROP_MESSAGECHAINID_AND_POS);
            String waitUntil = message.getString(AmsConstants.MSGPROP_GROUP_WAIT_TIME);

            TextType textType = TextType.getTextTypeByNumber(type);
            
            result = new VoicemailAlarmMessage(message.getJMSTimestamp(),
                                               Priority.NORMAL,
                                               phone,
                                               text,
                                               State.NEW,
                                               Type.OUT,
                                               "NONE",
                                               textType,
                                               chainIdAndPos,
                                               waitUntil);
        } catch (JMSException jmse) {
            LOG.warn("[*** JMSException ***]: convertMessage(): {}", jmse.getMessage());
        }

        return result;
    }
}
