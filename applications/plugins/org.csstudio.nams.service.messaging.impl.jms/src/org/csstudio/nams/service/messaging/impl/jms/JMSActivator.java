package org.csstudio.nams.service.messaging.impl.jms;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiService;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class JMSActivator extends AbstractBundleActivator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.nams.service.messaging.impl.jms";
	
	@OSGiBundleActivationMethod
	public void startBundle(
			@OSGiService @Required Logger logger
	) {
		JMSConsumer.staticInjectLogger(logger);
		JMSProducer.staticInjectLogger(logger);
	}
}
