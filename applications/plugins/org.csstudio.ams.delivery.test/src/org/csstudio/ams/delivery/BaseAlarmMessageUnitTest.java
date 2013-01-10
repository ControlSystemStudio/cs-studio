
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

import org.csstudio.ams.delivery.message.BaseAlarmMessage;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.Priority;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.message.BaseAlarmMessage.Type;
import org.junit.Test;
import org.junit.Assert;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 20.12.2011
 */
public class BaseAlarmMessageUnitTest {
    
    @Test
    public final void testStateChange() {
        BaseAlarmMessage out = new BaseAlarmMessage(System.currentTimeMillis(),
                                                    Priority.NORMAL,
                                                    "Yogi.Baer@Jellystone-Park.us",
                                                    "Take my picnic basket!",
                                                    State.NEW,
                                                    Type.OUT,
                                                    "NULL");
        
        out.setMessageState(State.FAILED);
        Assert.assertEquals(1, out.getFailCount());
        
        out.setMessageState(State.FAILED);
        Assert.assertEquals(2, out.getFailCount());

        out.setMessageState(State.FAILED);
        Assert.assertEquals(3, out.getFailCount());
        Assert.assertEquals(State.BAD, out.getMessageState());
        
        out.setMessageState(State.SENT);
        Assert.assertEquals(State.SENT, out.getMessageState());
        Assert.assertEquals(0, out.getFailCount());
        
        out = new BaseAlarmMessage(System.currentTimeMillis(),
                                   Priority.NORMAL,
                                   "Yogi.Baer@Jellystone-Park.us",
                                   " DEVICE_TEST{1234,device.1}",
                                   State.NEW,
                                   Type.OUT,
                                   "NULL");
        
        Assert.assertTrue(out.isDeviceTest());
    }
}
