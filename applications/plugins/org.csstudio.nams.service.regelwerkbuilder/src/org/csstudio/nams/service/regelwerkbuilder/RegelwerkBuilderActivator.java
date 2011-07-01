
package org.csstudio.nams.service.regelwerkbuilder;

import org.csstudio.nams.common.activatorUtils.AbstractBundleActivator;
import org.csstudio.nams.common.activatorUtils.ExecutableEclipseRCPExtension;
import org.csstudio.nams.common.activatorUtils.OSGiBundleActivationMethod;
import org.csstudio.nams.common.activatorUtils.OSGiServiceOffers;
import org.csstudio.nams.common.activatorUtils.Required;
import org.csstudio.nams.service.regelwerkbuilder.declaration.RegelwerkBuilderService;
import org.csstudio.nams.service.regelwerkbuilder.extensionPoint.RegelwerkBuilderServiceFactory;
import org.osgi.framework.BundleActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class RegelwerkBuilderActivator extends AbstractBundleActivator
		implements BundleActivator {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.csstudio.nams.service.regelwerkbuilder";

	@OSGiBundleActivationMethod
	public OSGiServiceOffers startBundle(
			@ExecutableEclipseRCPExtension(extensionId = RegelwerkBuilderServiceFactory.class)
			@Required
			final Object regelwerkBuilderServiceFactory) {
		final OSGiServiceOffers result = new OSGiServiceOffers();

		final RegelwerkBuilderServiceFactory factory = (RegelwerkBuilderServiceFactory) regelwerkBuilderServiceFactory;
		result.put(RegelwerkBuilderService.class, factory.createService());
		return result;
	}
}
