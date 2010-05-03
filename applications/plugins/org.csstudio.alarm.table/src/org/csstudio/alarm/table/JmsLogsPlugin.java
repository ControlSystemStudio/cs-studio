/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, Member of the Helmholtz Association,
 * (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT,
 * THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF
 * WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED
 * HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE
 * REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table;

import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.alarm.dbaccess.archivedb.IMessageTypes;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.table.jms.ISendMapMessage;
import org.csstudio.alarm.table.jms.SendMapMessage;
import org.csstudio.alarm.table.preferences.ISeverityMapping;
import org.csstudio.alarm.table.preferences.SeverityMapping;
import org.csstudio.alarm.table.service.AlarmSoundService;
import org.csstudio.alarm.table.service.IAlarmSoundService;
import org.csstudio.alarm.table.service.ITopicsetService;
import org.csstudio.alarm.table.service.TopicsetService;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * The main plugin class to be used in the desktop.
 */
public class JmsLogsPlugin extends AbstractCssUiPlugin {
    
    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.alarm.table"; //$NON-NLS-1$
    
    // The shared instance.
    private static JmsLogsPlugin plugin;
    
    private IMessageTypes _messageTypes;
    
    private ServiceReference _serviceReferenceMessageTypes;
    
    private ServiceReference _serviceReferenceArchiveAccess;
    
    private ILogMessageArchiveAccess _archiveAccess;
    
    private ServiceReference _serviceReferenceSeverityMapping;
    
    private ISeverityMapping _severityMapping;
    
    private ServiceReference _serviceReferenceSendMapMessage;
    
    private ISendMapMessage _sendMapMessage;
    
    /**
     * Stateful service shared by the log views
     */
    private ITopicsetService _topicsetServiceForLogViews;
    
    /**
     * Stateful service shared by the alarm views
     */
    private ITopicsetService _topicsetServiceForAlarmViews;
    
    /**
     * The alarm sound service
     */
    private IAlarmSoundService _alarmSoundService;
    
    /**
     * The alarm service
     */
    private IAlarmService _alarmService;
    
    /**
     * The constructor.
     */
    public JmsLogsPlugin() {
        if (plugin != null) {
            throw new IllegalStateException("Attempt to call plugin constructor more than once.");
        }
        plugin = this;
    }
    
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }
    
    /**
     * This method is called upon plug-in activation
     */
    public void doStart(final BundleContext context) throws Exception {
        _alarmService = getService(context, IAlarmService.class);
        
        // TODO jp might be registered as an OSGI service
        _topicsetServiceForLogViews = new TopicsetService();
        _topicsetServiceForAlarmViews = new TopicsetService();
        _alarmSoundService = new AlarmSoundService();
        
        ISeverityMapping severityMapping = new SeverityMapping();
        
        @SuppressWarnings("unused")
        ServiceRegistration severityMappingRegistration = context
                .registerService(ISeverityMapping.class.getName(), severityMapping, null);
        
        _serviceReferenceSeverityMapping = context.getServiceReference(ISeverityMapping.class
                .getName());
        if (_serviceReferenceSeverityMapping != null) {
            _severityMapping = (ISeverityMapping) context
                    .getService(_serviceReferenceSeverityMapping);
        }
        
        ISendMapMessage sendMapMessage = new SendMapMessage();
        
        @SuppressWarnings("unused")
        ServiceRegistration sendMapMessageRegistration = context
                .registerService(ISendMapMessage.class.getName(), sendMapMessage, null);
        
        _serviceReferenceSendMapMessage = context.getServiceReference(ISendMapMessage.class
                .getName());
        if (_serviceReferenceSendMapMessage != null) {
            _sendMapMessage = (ISendMapMessage) context.getService(_serviceReferenceSendMapMessage);
        }
        
        _serviceReferenceMessageTypes = context.getServiceReference(IMessageTypes.class.getName());
        if (_serviceReferenceMessageTypes != null) {
            _messageTypes = (IMessageTypes) context.getService(_serviceReferenceMessageTypes);
        }
        _serviceReferenceArchiveAccess = context.getServiceReference(ILogMessageArchiveAccess.class
                .getName());
        if (_serviceReferenceArchiveAccess != null) {
            _archiveAccess = (ILogMessageArchiveAccess) context
                    .getService(_serviceReferenceArchiveAccess);
        }
    }
    
    /**
     * This method is called when the plug-in is stopped
     */
    public void doStop(final BundleContext context) throws Exception {
        context.ungetService(_serviceReferenceMessageTypes);
        context.ungetService(_serviceReferenceArchiveAccess);
        context.ungetService(_serviceReferenceSendMapMessage);
        context.ungetService(_serviceReferenceSeverityMapping);
        
        plugin = null;
    }
    
    /**
     * Returns the shared instance.
     */
    public static JmsLogsPlugin getDefault() {
        return plugin;
    }
    
    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.csstudio.alarm.table", path); //$NON-NLS-1$
    }
    
    /** Add informational message to the plugin log. */
    public static void logInfo(final String message) {
        getDefault().log(IStatus.INFO, message, null);
    }
    
    /** Add error message to the plugin log. */
    public static void logError(final String message) {
        getDefault().log(IStatus.ERROR, message, null);
    }
    
    /** Add an exception to the plugin log. */
    public static void logException(final String message, final Exception e) {
        getDefault().log(IStatus.ERROR, message, e);
    }
    
    /**
     * Add a message to the log.
     * 
     * @param type
     * @param message
     */
    private void log(final int type, final String message, final Exception e) {
        getLog().log(new Status(type, PLUGIN_ID, IStatus.OK, message, e));
    }
    
    public ISeverityMapping getSeverityMapping() {
        return _severityMapping;
    }
    
    public IMessageTypes getMessageTypes() {
        return _messageTypes;
    }
    
    public ILogMessageArchiveAccess getArchiveAccess() {
        return _archiveAccess;
    }
    
    public ISendMapMessage getSendMapMessage() {
        return _sendMapMessage;
    }
    
    /**
     * @return the topic set service to be shared by the log views (or null)
     */
    public ITopicsetService getTopicsetServiceForLogViews() {
        return _topicsetServiceForLogViews;
    }
    
    /**
     * @return the topic set service to be shared by the alarm views (or null)
     */
    public ITopicsetService getTopicsetServiceForAlarmViews() {
        return _topicsetServiceForAlarmViews;
    }
    
    /**
     * @return the alarm sound service or null
     */
    public IAlarmSoundService getAlarmSoundService() {
        return _alarmSoundService;
    }
    
    /**
     * @return the alarm service or null
     */
    public IAlarmService getAlarmService() {
        return _alarmService;
    }
    
}
