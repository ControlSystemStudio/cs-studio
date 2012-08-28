/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: DesyKrykCodeTemplates.xml,v 1.7
 * 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.alarm.table.jms;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;

/**
 * Listener for alarm tables use a message list as destination for the incoming messages. The
 * listener allows for registration of another listener. This provides for view-based actions.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 28.04.2010
 */
public interface IAlarmTableListener extends IAlarmListener {
    
    /**
     * Set the messageList which is the destination for the messages
     * 
     * @param messageList .
     */
    void setMessageList(@Nonnull final AbstractMessageList messageList);
    
    /**
     * Register an alarm listener, which is called when a message comes in.
     * 
     * @param alarmListener
     */
    void registerAlarmListener(@Nonnull final IAlarmListener alarmListener);
    
    /**
     * Deregister an alarm listener. If listener is not present, nothing happens.
     * 
     * @param alarmListener
     */
    void deRegisterAlarmListener(@Nonnull final IAlarmListener alarmListener);

    /**
     * Enable or disable filtering
     * 
     * If filtering is enabled, the pv in the incoming message must be contained in the current configuration of the alarm service,
     * otherwise the message is discarded.
     * The default is false, no filtering takes place.
     * 
     * @param enable
     */
    void enableFilter(final boolean enable);
    
}
