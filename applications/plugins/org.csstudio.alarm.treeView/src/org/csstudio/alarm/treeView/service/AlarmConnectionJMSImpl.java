/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 * 
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 * 
 * $Id$
 */
package org.csstudio.alarm.treeView.service;

import javax.jms.JMSException;
import javax.jms.Session;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.jms.AlarmMessageListener;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import com.sun.istack.internal.NotNull;

/**
 * This is the JMS based implementation of the AlarmConnection.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public final class AlarmConnectionJMSImpl implements IAlarmConnection {
    private static final String COULD_NOT_CREATE_LISTENER_SESSION = "Could not create listener session using the shared JMS connections";
    
    private final CentralLogger _log = CentralLogger.getInstance();
    
    private AlarmMessageListener _listener;
    private IMessageListenerSession _listenerSession;
    
    /**
     * Constructor may only be called from the AlarmService.
     */
    AlarmConnectionJMSImpl() {
    }
    
    public boolean canHandleTopics() {
        return true;
    }
    
    public void disconnect() throws AlarmConnectionException {
        // TODO Auto-generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectWithListener(final @NotNull IAlarmConnectionMonitor connectionMonitor,
                                    final @NotNull IAlarmListener listener) throws AlarmConnectionException {
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String[] topics = prefs.getString(AlarmTreePlugin.PLUGIN_ID,
                                          PreferenceConstants.JMS_TOPICS,
                                          "",
                                          null).split(",");
        connectWithListenerForTopics(connectionMonitor, listener, topics);
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectWithListenerForTopics(final @NotNull IAlarmConnectionMonitor connectionMonitor,
                                             final @NotNull IAlarmListener listener,
                                             final @NotNull String[] topics) throws AlarmConnectionException {
        try {
            _listenerSession = SharedJmsConnections.startMessageListener(_listener,
                                                                         topics,
                                                                         Session.AUTO_ACKNOWLEDGE);
            
            AlarmConnectionMonitorAdapter monitor = new AlarmConnectionMonitorAdapter(connectionMonitor);
            _listenerSession.addMonitor(monitor);
            if (_listenerSession.isActive()) {
                monitor.onConnected();
            }
        } catch (JMSException e) {
            _log.error(this, COULD_NOT_CREATE_LISTENER_SESSION);
            throw new AlarmConnectionException(COULD_NOT_CREATE_LISTENER_SESSION, e);
        }
    }
    
    /**
     * Object based adapter.
     * 
     * Adapts the IAlarmConnectionMonitor to the IConnectionMonitor expected by JMS.
     */
    private final static class AlarmConnectionMonitorAdapter implements IConnectionMonitor {
        
        private final IAlarmConnectionMonitor monitor;
        
        public AlarmConnectionMonitorAdapter(@NotNull final IAlarmConnectionMonitor monitor) {
            this.monitor = monitor;
        }
        
        public void onConnected() {
            monitor.onConnect();
        }
        
        public void onDisconnected() {
            monitor.onDisconnect();
        }
        
    }
    
}
