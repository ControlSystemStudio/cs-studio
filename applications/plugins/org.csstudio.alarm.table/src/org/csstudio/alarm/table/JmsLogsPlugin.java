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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.alarm.dbaccess.archivedb.IMessageTypes;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.table.dataModel.SeverityRegistry;
import org.csstudio.alarm.table.jms.ISendMapMessage;
import org.csstudio.alarm.table.jms.SendMapMessage;
import org.csstudio.alarm.table.preferences.ColumnDescription;
import org.csstudio.alarm.table.preferences.ITopicSetColumnService;
import org.csstudio.alarm.table.preferences.SeverityMapping;
import org.csstudio.alarm.table.preferences.TopicSetColumnService;
import org.csstudio.alarm.table.preferences.alarm.AlarmViewPreference;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.alarm.table.preferences.verifier.AmsVerifyViewPreferenceConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main plugin class to be used in the desktop.
 */
public class JmsLogsPlugin extends AbstractCssUiPlugin {
    
    private static final Logger LOG = LoggerFactory.getLogger(JmsLogsPlugin.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.alarm.table"; //$NON-NLS-1$

	// The shared instance.
	private static JmsLogsPlugin PLUGIN;

	private IMessageTypes _messageTypes;

	private ServiceReference _serviceReferenceMessageTypes;

	private ServiceReference _serviceReferenceArchiveAccess;

	private ILogMessageArchiveAccess _archiveAccess;

	private ServiceReference _serviceReferenceSendMapMessage;

	private ISendMapMessage _sendMapMessage;

	// Stateful services shared by the log views
	private ITopicsetService _topicsetServiceForLogViews;
	private ITopicSetColumnService _topicSetColumnServiceForLogViews;

	// Stateful services shared by the alarm views
	private ITopicsetService _topicsetServiceForAlarmViews;
	private ITopicSetColumnService _topicSetColumnServiceForAlarmViews;

	// Stateful services shared by the alarm views
	private ITopicSetColumnService _topicSetColumnServiceForVerifyViews;

	// The alarm sound service
	private IAlarmSoundService _alarmSoundService;

	// The alarm service
	private IAlarmService _alarmService;

	// The alarm configuration service
	private IAlarmConfigurationService _alarmConfigurationService;

	/**
	 * The constructor.
	 */
	public JmsLogsPlugin() {
		if (PLUGIN != null) {
			throw new IllegalStateException(
					"Attempt to call plugin constructor more than once.");
		}
		PLUGIN = this;
	}

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
    public void doStart(final BundleContext context) throws Exception {
		_alarmService = getService(context, IAlarmService.class);
		_alarmConfigurationService = getService(context,
				IAlarmConfigurationService.class);

		// Might be registered as OSGI services
		_topicsetServiceForLogViews = new TopicsetService("for log views");
		_topicsetServiceForAlarmViews = new TopicsetService("for alarm views");
		_alarmSoundService = AlarmSoundService.newAlarmSoundService();
		_topicSetColumnServiceForLogViews = new TopicSetColumnService(
				LogViewPreferenceConstants.TOPIC_SET,
				LogViewPreferenceConstants.P_STRING,
				getColumnDescriptionsForLogViews());
		_topicSetColumnServiceForAlarmViews = new TopicSetColumnService(
				AlarmViewPreference.ALARMVIEW_TOPIC_SET.getKeyAsString(),
				AlarmViewPreference.ALARMVIEW_P_STRING_ALARM.getKeyAsString(),
				getColumnDescriptionsForAlarmViews());
		_topicSetColumnServiceForVerifyViews = new TopicSetColumnService(
				AmsVerifyViewPreferenceConstants.TOPIC_SET,
				AmsVerifyViewPreferenceConstants.P_STRING,
				getColumnDescriptionsForVerifyViews());

		SeverityRegistry.setSeverityMapping(new SeverityMapping());

		final ISendMapMessage sendMapMessage = new SendMapMessage();
		@SuppressWarnings("unused")
		final ServiceRegistration sendMapMessageRegistration = context
				.registerService(ISendMapMessage.class.getName(),
						sendMapMessage, null);

		_serviceReferenceSendMapMessage = context
				.getServiceReference(ISendMapMessage.class.getName());
		if (_serviceReferenceSendMapMessage != null) {
			_sendMapMessage = (ISendMapMessage) context
					.getService(_serviceReferenceSendMapMessage);
		}

		_serviceReferenceMessageTypes = context
				.getServiceReference(IMessageTypes.class.getName());
		if (_serviceReferenceMessageTypes != null) {
			_messageTypes = (IMessageTypes) context
					.getService(_serviceReferenceMessageTypes);
		}
		_serviceReferenceArchiveAccess = context
				.getServiceReference(ILogMessageArchiveAccess.class.getName());
		if (_serviceReferenceArchiveAccess != null) {
			_archiveAccess = (ILogMessageArchiveAccess) context
					.getService(_serviceReferenceArchiveAccess);
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
    public void doStop(final BundleContext context) throws Exception {
		LOG.info("doStop");
		safelyDisconnect(_topicsetServiceForAlarmViews);
		safelyDisconnect(_topicsetServiceForLogViews);

		context.ungetService(_serviceReferenceMessageTypes);
		context.ungetService(_serviceReferenceArchiveAccess);
		context.ungetService(_serviceReferenceSendMapMessage);
		PLUGIN = null;
	}

	private void safelyDisconnect(final ITopicsetService topicsetService) {
		try {
			topicsetService.disconnectAll();
		} catch (final RuntimeException e) {
			LOG.error("Error while disconnecting {}", topicsetService,
					e);
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static JmsLogsPlugin getDefault() {
		return PLUGIN;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				"org.csstudio.alarm.table", path); //$NON-NLS-1$
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

	public ITopicSetColumnService getTopicSetColumnServiceForLogViews() {
		return _topicSetColumnServiceForLogViews;
	}

	public ITopicSetColumnService getTopicSetColumnServiceForAlarmViews() {
		return _topicSetColumnServiceForAlarmViews;
	}

	public ITopicSetColumnService getTopicSetColumnServiceForVerifyViews() {
		return _topicSetColumnServiceForVerifyViews;
	}

	/**
	 * @return the topic set service to be shared by the alarm views (or null)
	 */
	@CheckForNull
	public ITopicsetService getTopicsetServiceForAlarmViews() {
		return _topicsetServiceForAlarmViews;
	}

	/**
	 * @return the alarm sound service or null
	 */
	@CheckForNull
	public IAlarmSoundService getAlarmSoundService() {
		return _alarmSoundService;
	}

	/**
	 * @return the alarm service or null
	 */
	@CheckForNull
	public IAlarmService getAlarmService() {
		return _alarmService;
	}

	/**
	 * @return the alarm configuration service or null
	 */
	@CheckForNull
	public IAlarmConfigurationService getAlarmConfigurationService() {
		return _alarmConfigurationService;
	}

	@Nonnull
	private List<ColumnDescription> getColumnDescriptionsForLogViews() {
		final List<ColumnDescription> result = new ArrayList<ColumnDescription>();
		result.add(ColumnDescription.IS_DEFAULT_ENTRY);
		result.add(ColumnDescription.TOPIC_SET);
		result.add(ColumnDescription.NAME_FOR_TOPIC_SET);
		result.add(ColumnDescription.AUTO_START);
		result.add(ColumnDescription.FONT);
		return result;
	}

	@Nonnull
	private List<ColumnDescription> getColumnDescriptionsForAlarmViews() {
		final List<ColumnDescription> result = new ArrayList<ColumnDescription>();
		result.add(ColumnDescription.IS_DEFAULT_ENTRY);
		result.add(ColumnDescription.TOPIC_SET);
		result.add(ColumnDescription.NAME_FOR_TOPIC_SET);
		result.add(ColumnDescription.AUTO_START);
		result.add(ColumnDescription.RETRIEVE_INITIAL_STATE);
		result.add(ColumnDescription.FONT);
		return result;
	}

	@Nonnull
	private List<ColumnDescription> getColumnDescriptionsForVerifyViews() {

		final List<ColumnDescription> result = new ArrayList<ColumnDescription>();
		result.add(ColumnDescription.IS_DEFAULT_ENTRY);
		result.add(ColumnDescription.TOPIC_SET);
		result.add(ColumnDescription.NAME_FOR_TOPIC_SET);
		result.add(ColumnDescription.AUTO_START);
		result.add(ColumnDescription.FONT);
		return result;
	}

}
