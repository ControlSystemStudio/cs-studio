
package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class JMSActivator extends AbstractBundleActivator implements
		BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.service.messaging.impl.jms";
	private PreferenceService preferenceService;

	@OSGiBundleActivationMethod
	public void startBundle(@OSGiService
	@Required
	final ILogger logger, @OSGiService
	@Required
	final PreferenceService injectedPreferenceService) {
		this.preferenceService = injectedPreferenceService;
		JMSConsumer.staticInjectLogger(logger);
		JMSProducer.staticInjectLogger(logger);
		JMSMessagingSessionImpl.staticInject(this.preferenceService);
	}
}
