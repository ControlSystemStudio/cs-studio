
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

import org.csstudio.ams.delivery.message.BaseAlarmMessage;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 10.12.2011
 */
public class EMailAlarmMessage extends BaseAlarmMessage {
    
    /** Default serial version id */
    private static final long serialVersionUID = -1L;

    protected String receiverName;
    
    protected String mailSubject;
   
    public EMailAlarmMessage(long timestamp, Priority p,
                             String address, String text,
                             State state, Type type,
                             String device,
                             String name, String subject) {
        super(timestamp, p, address, text, state, type, device);
        this.receiverName = name;
        this.mailSubject = subject;
    }

    /**
     * Overwrites the method <code>toString()</code> from Object. Creates a nice string containg the content
     * of this e-mail message.
     */
    @Override
    public String toString()  {
        StringBuffer result = new StringBuffer();
        result.append("EMailAlarmMessage {");
        result.append(this.messageTimestamp + ",");
        result.append(this.receiverAddress + ",");
        result.append(this.receiverName + ",");
        result.append(this.mailSubject + ",");
        result.append(this.messageText + ",");
        result.append(this.messageState + ",");
        result.append("Failed:" + this.failCount + ",");
        result.append(this.messageType + ",");
        result.append(this.priority + ",");
        result.append(this.deviceId + ",");
        result.append("Test message:" + this.deviceTest + "}");
        return result.toString();
    }

    public String getReceiverName() {
        return receiverName;
    }
    
    public void setReceiverName(String name) {
        receiverName = name;
    }
    
    public String getMailSubject() {
        return mailSubject;
    }
    
    public void setMailSubject(String subject) {
        mailSubject = subject;
    }
}
