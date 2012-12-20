
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

package org.csstudio.ams.delivery.voicemail.isdn;

import java.util.Collection;

import org.csstudio.ams.delivery.device.DeviceException;
import org.csstudio.ams.delivery.device.IDeliveryDevice;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.voicemail.VoicemailAlarmMessage;
import org.csstudio.ams.delivery.voicemail.VoicemailWorkerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 09.02.2012
 */
public class VoicemailDevice implements IDeliveryDevice<VoicemailAlarmMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(VoicemailDevice.class);
    
    /** The class that makes the telephone calls */
    private CallCenter callCenter;

    private VoicemailWorkerStatus workerStatus;
    
    public VoicemailDevice(VoicemailWorkerStatus status) throws DeviceException {
        workerStatus = status;
        try {
            callCenter = new CallCenter();
        } catch (CallCenterException e) {
            throw new DeviceException(e.getMessage(), e);
        }
    }
    
    @Override
    public boolean sendMessage(VoicemailAlarmMessage message) {
        boolean success = false;
        try {
            callCenter.makeCall(message.getReceiverAddress(),
                                message.getMessageText(),
                                message.getTextTypeNumberAsString(),
                                message.getMessageChainIdAndPosAsString(),
                                message.getWaitTimeAsString());
            message.setMessageState(State.SENT);
            success = true;
        } catch (CallCenterException e) {
            LOG.error("[*** CallCenterException ***]: {}", e.getMessage());
            message.setMessageState(State.FAILED);
        }
        workerStatus.setMadeCall(success);
        return success;
    }

    @Override
    public int sendMessages(Collection<VoicemailAlarmMessage> msgList) {
        int sent = 0;
        for (VoicemailAlarmMessage m : msgList) {
            if (sendMessage(m)) {
                sent++;
            }
        }
        return sent;
    }

    @Override
    public void stopDevice() {
        LOG.info("Stopping device.");
    }
}
