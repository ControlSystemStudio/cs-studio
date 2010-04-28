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
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: ITopicsetService.java,v 1.1 2010/04/28
 * 07:44:07 jpenning Exp $
 */
package org.csstudio.alarm.table.service;

import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.alarm.table.jms.IAlarmTableListener;
import org.csstudio.alarm.table.preferences.TopicSet;

/**
 * This is a stateful service helping views by the management of connections and message lists. This
 * is a SINGLETON because it maintains the state for several views even beyond their lifetime. The
 * log view and its subclasses use this service to map a topic set to lists of messages and
 * connections. Usage:<br>
 * If a new topic set is selected by the user (find out if its new using hasTopicSet), an
 * appropriate message list is created and a connection to the underlying message system is built
 * (createAndConnectForTopicSet).<br>
 * If the user switches to another topic set, the former connection is maintained, allowing for
 * switching back quickly (retrieveing the current message list using getMessageListForTopicSet).
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 27.04.2010
 */
public interface ITopicsetService {
    
    /**
     * @param topicSet .
     * @return true, if topicSet is already known
     */
    boolean hasTopicSet(TopicSet topicSet);
    
    /**
     * If the topic set is not known to this service, a connection is created for it. A listener to
     * the incoming messages will be registered too. As the destination for the listener the message
     * list must be given, it is registered at the given listener.<br>
     * Precondition: !hasTopicSet(topicSet)
     * 
     * @param topicSet the topic set the connection is created for
     * @param messageList destination for the message listener
     * @param alarmListener callback for the messages
     */
    void createAndConnectForTopicSet(TopicSet topicSet,
                                     MessageList messageList,
                                     IAlarmTableListener alarmListener);
    
    /**
     * Precondition: hasTopicSet(topicSet)
     * 
     * @param topicSet .
     * @return the message list for the given topicSet
     */
    MessageList getMessageListForTopicSet(TopicSet topicSet);
    
    /**
     * Precondition: hasTopicSet(topicSet)
     * 
     * @param topicSet .
     * @return the connection for the given topic set
     */
    IAlarmConnection getAlarmConnectionForTopicSet(TopicSet topicSet);
    
    /**
     * This service is intended to be local to a view. It keeps track of the connections, so they
     * can be disconnected here at once. This is usually called from whithin the views dispose
     * method.
     */
    void disconnectAll();
}
