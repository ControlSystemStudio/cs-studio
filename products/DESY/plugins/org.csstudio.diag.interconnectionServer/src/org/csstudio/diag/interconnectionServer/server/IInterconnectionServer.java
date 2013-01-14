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
 */
package org.csstudio.diag.interconnectionServer.server;

import java.util.concurrent.ExecutorService;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

import org.csstudio.domain.common.statistic.Collector;


public interface IInterconnectionServer {
    boolean stopIcServer();
    
    void executeMe();
    
    void countJmsSendMessageErrorAndReconnectIfTooManyErrors();
    
    boolean sendLogMessage(final MapMessage message, final Session session);
    
    Collector getJmsMessageWriteCollector();
    
    Collector getClientRequestTheadCollector();
    
    Collector getBeaconReplyTimeCollector();
    
    Collector getMessageReplyTimeCollector();
    
    Collector getNumberOfDuplicateMessagesCollector();
    
    /**
     * Generates the next command id and returns it.
     * 
     * @return the next command id
     */
    int nextSendCommandId();
    
    boolean isQuit();
    
    Collector getNumberOfIocFailoverCollector();
    
    /**
     * Creates a new JMS session.
     *
     * @return the session.
     * @throws JMSException
     *             if an error occurs.
     */
    Session createJmsSession() throws JMSException;
    
    /**
     * The hostname of the local machine is cached, so this will respond quickly.
     * If the hostname could not be retrieved, "localHost-ND" is returned.
     * 
     * @return hostname of the local machine
     */
    String getLocalHostName();
    
    ExecutorService getCommandExecutor();
    
}
