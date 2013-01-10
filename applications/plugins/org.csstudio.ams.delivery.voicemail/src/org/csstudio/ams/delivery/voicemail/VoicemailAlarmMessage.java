
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

package org.csstudio.ams.delivery.voicemail;

import org.csstudio.ams.delivery.message.BaseAlarmMessage;
import org.csstudio.ams.delivery.voicemail.isdn.TextType;

/**
 * @author mmoeller
 * @version 1.0
 * @since 08.02.2012
 */
public class VoicemailAlarmMessage extends BaseAlarmMessage {

    private static final long serialVersionUID = 1L;
    
    private TextType textType;
    
    private long waitTime;
    
    private long messageChainIdAndPos;
    
    public VoicemailAlarmMessage(long timestamp,
                                 Priority p,
                                 String address,
                                 String text,
                                 State state,
                                 Type type,
                                 String device,
                                 TextType typeOfText,
                                 String waitUntil,
                                 String chainId) {
        super(timestamp, p, address, text, state, type, device);
        textType = typeOfText;
        waitTime = convertToLong(waitUntil);
        messageChainIdAndPos = convertToLong(chainId);
    }
    
    private long convertToLong(String s) {
        long result = 0L;
        try {
            result = Long.parseLong(s);
        } catch (NumberFormatException e) {
            result = 0L;
        }
        return result;
    }
    
    public TextType getTextType() {
        return textType;
    }
    
    public String getTextTypeNumberAsString() {
        return String.valueOf(textType.getTypeNumber());
    }

    public long getWaitTime() {
        return waitTime;
    }
    
    public String getWaitTimeAsString() {
        return String.valueOf(waitTime);
    }

    public long getMessageChainIdAndPos() {
        return messageChainIdAndPos;
    }
    
    public String getMessageChainIdAndPosAsString() {
        return String.valueOf(messageChainIdAndPos);
    }

    /**
     * Overwrites the method <code>toString()</code> from Object. Creates a nice string containing the content
     * of this alarm message.
     */
    @Override
    public String toString()  {
        StringBuffer result = new StringBuffer();
        result.append("VoicemailAlarmMessage {");
        result.append(this.messageTimestamp + ",");
        result.append(this.receiverAddress + ",");
        result.append(this.messageText + ",");
        result.append(this.messageState + ",");
        result.append("Failed:" + this.failCount + ",");
        result.append(this.messageType + ",");
        result.append(this.priority + ",");
        result.append(this.deviceId + ",");
        result.append("Test message:" + this.deviceTest + "}");
        return result.toString();
    }
}
